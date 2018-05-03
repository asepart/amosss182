package de.fau.cs.osr.amos.asepart.filters;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.Priority;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;
import org.hibernate.Session;
import org.mindrot.jbcrypt.BCrypt;

import de.fau.cs.osr.amos.asepart.WebServiceSecurityContext;
import de.fau.cs.osr.amos.asepart.DatabaseController;
import de.fau.cs.osr.amos.asepart.entities.Account;
import de.fau.cs.osr.amos.asepart.entities.Admin;
import de.fau.cs.osr.amos.asepart.entities.User;

// TODO: (maybe) login limit to avoid brute force attacks

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter
{
    @Context
    private ResourceInfo resourceInfo;

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";

    private static final Response ACCESS_UNAUTHORIZED = Response.status(Response.Status.UNAUTHORIZED)
            .entity("Your identification is invalid.").build();

    private static final Response ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN)
            .entity("Your account has no rights to access this ressource.").build();

    private static final String ROLE_PROPERTY = "X-ASEPART-Role";

    @Override
    public void filter(ContainerRequestContext request)
    {
        final Method method = resourceInfo.getResourceMethod();

        if (method.isAnnotationPresent(PermitAll.class)) // Authorization not needed for method
            return;

        // Get request headers
        final MultivaluedMap<String, String> headers = request.getHeaders();

        // Fetch authorization header
        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

        // If no authorization information present; block access
        if (authorization == null || authorization.isEmpty())
        {
            request.abortWith(ACCESS_UNAUTHORIZED);
        }

        final List<String> role = headers.get(ROLE_PROPERTY);

        // If no role information present; block access
        if (role == null || role.isEmpty())
        {
            request.abortWith(ACCESS_FORBIDDEN);
        }

        // Get encoded username and password
        final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

        // Decode username and password
        String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));

        // Split username and password tokens
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String accountName = tokenizer.nextToken();
        final String password = tokenizer.nextToken();

        // Get role name
        final String roleName = role.get(0);

        if (method.isAnnotationPresent(RolesAllowed.class))
        {
            RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
            Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

            if (!rolesSet.contains(roleName))
            {
                request.abortWith(ACCESS_FORBIDDEN);
            }

            final Response error = checkPassword(accountName, password, roleName);
            if (error != null)
                request.abortWith(error);
        }

        SecurityContext sc = new WebServiceSecurityContext(accountName, roleName, request.getUriInfo().getRequestUri().getScheme());
        request.setSecurityContext(sc);
    }

    private Response checkPassword(final String loginName, final String password, final String role)
    {
        Session session = DatabaseController.newSession();
        session.beginTransaction();

        try
        {
            Account account = null;
            Class<?> accountType;

            switch (role)
            {
                case "Admin":
                    accountType = Admin.class;
                    break;
                case "User":
                    accountType = User.class;
                    break;
                default:
                    return ACCESS_FORBIDDEN;
            }


            account = (Account) session.get(accountType, loginName);
            if (account == null) // account with loginName does not exist
                return ACCESS_UNAUTHORIZED;

            final String savedHash = account.getPasswordHash();

            if (!BCrypt.checkpw(password, savedHash)) // password does not match
                return ACCESS_UNAUTHORIZED;
        }

        finally
        {
            session.getTransaction().commit();
            session.close();
        }

        return null;
    }
}
