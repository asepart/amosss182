package de.fau.cs.osr.amos.asepart;

import java.lang.reflect.Method;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.Priority;
import javax.annotation.security.PermitAll;
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

// TODO: (maybe) introduce login limit to avoid brute force attacks

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter
{
    @Context
    private ResourceInfo resourceInfo;

    private static final String ROLE_PROPERTY = "X-ASEPART-Role";

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";

    private static final String ACCESS_UNAUTHORIZED = "Your identification is invalid.";
    private static final String ACCESS_FORBIDDEN = "Your account has no rights to access this resource.";

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
            request.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(ACCESS_UNAUTHORIZED).build());
            return;
        }

        final List<String> role = headers.get(ROLE_PROPERTY);

        // If no role information present; block access
        if (role == null || role.isEmpty())
        {
            request.abortWith(Response.status(Response.Status.FORBIDDEN).entity(ACCESS_FORBIDDEN).build());
            return;
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

        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.authenticate(accountName, password, roleName))
            {
                request.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(ACCESS_UNAUTHORIZED).build());
                return;
            }
        }

        catch (Exception e)
        {
            request.abortWith(Response.status(Response.Status.FORBIDDEN).entity(ACCESS_FORBIDDEN).build());
            return;
        }

        SecurityContext sc = new WebServiceSecurityContext(accountName, roleName, request.getUriInfo().getRequestUri().getScheme());
        request.setSecurityContext(sc);
    }
}
