package de.fau.cs.osr.amos.asepart.authentication;

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
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;
import org.hibernate.Session;
import org.mindrot.jbcrypt.BCrypt;

import de.fau.cs.osr.amos.asepart.DatabaseController;
import de.fau.cs.osr.amos.asepart.entities.Account;
import de.fau.cs.osr.amos.asepart.entities.Admin;
import de.fau.cs.osr.amos.asepart.entities.User;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter
{
    @Context
    private ResourceInfo resourceInfo;

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";

    private static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
            .entity("You cannot access this resource.").build();

    @Override
    public void filter(ContainerRequestContext requestContext)
    {
        final Method method = resourceInfo.getResourceMethod();

        if (method.isAnnotationPresent(PermitAll.class)) // Authorization not needed for method
            return;

        // Get request headers
        final MultivaluedMap<String, String> headers = requestContext.getHeaders();

        // Fetch authorization header
        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

        // If no authorization information present; block access
        if (authorization == null || authorization.isEmpty())
        {
            requestContext.abortWith(ACCESS_DENIED);
        }

        // Get encoded username and password
        final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

        // Decode username and password
        String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));

        // Split username and password tokens
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String accountName = tokenizer.nextToken();
        final String password = tokenizer.nextToken();

        if (method.isAnnotationPresent(RolesAllowed.class))
        {
            RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
            Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

            // Is user valid?
            if (!isAuthorized(accountName, password, rolesSet))
                requestContext.abortWith(ACCESS_DENIED);
        }
    }

    private boolean isAuthorized(final String loginName, final String password, final Set<String> rolesSet)
    {
        boolean authorized = false;

        try (Session session = DatabaseController.newSession())
        {
            session.beginTransaction();

            Account account = session.get(Account.class, loginName);
            if (account == null) // account with loginName does not exist
                return false;

            final String savedHash = account.getPasswordHash();
            if (!BCrypt.checkpw(password, savedHash)) // password does not match
                return false;

            for (String role : rolesSet) // check if account is in any allowed role
            {
                if (role.equals("Admin"))
                {
                    final Admin admin = session.get(Admin.class, account.getLoginName());

                    if (admin != null)
                        return true;
                }

                else if (role.equals("User"))
                {
                    final User user = session.get(User.class, account.getLoginName());

                    if (user != null)
                        return true;
                }
            }

            session.getTransaction().commit();
        }

        return false;
    }
}
