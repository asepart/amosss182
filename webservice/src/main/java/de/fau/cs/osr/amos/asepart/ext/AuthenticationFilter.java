package de.fau.cs.osr.amos.asepart.ext;

import de.fau.cs.osr.amos.asepart.client.DatabaseClient;
import de.fau.cs.osr.amos.asepart.service.WebServiceSecurityContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.Priority;
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

/**
 * This class is a request filter which handles authentication.
 * Any request method annotated with @RolesAllowed will trigger
 * calling the filter() method of this class. The client must send
 * username and password using HTTP Basic Authorization and also
 * use the custom X-ASEPART-Role header to specify is the client is
 * acting in the Admin or User role.
 *
 * If username and password are missing or wrong, 401 Unauthorized
 * is returned to the client. If the role of the account is not
 * included in the list of allowed roles of the request method
 * (provided by the @RolesAllowed annotation), 403 Forbidden is
 * returned to the client.
 */

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

    // TODO: (maybe) introduce login limit to avoid brute force attacks

    @Override
    public void filter(ContainerRequestContext request)
    {
        final Method method = resourceInfo.getResourceMethod();

        if (!method.isAnnotationPresent(RolesAllowed.class)) // Method is not restricted
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

        try (DatabaseClient dbClient = new DatabaseClient())
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

        // Check if role is allowed for method
        Set<String> rolesSet = new HashSet<>(Arrays.asList(method.getAnnotation(RolesAllowed.class).value()));

        if (!rolesSet.contains(roleName))
        {
            request.abortWith(Response.status(Response.Status.FORBIDDEN).entity(ACCESS_FORBIDDEN).build());
            return;
        }

        SecurityContext sc = new WebServiceSecurityContext(accountName, roleName, request.getUriInfo().getRequestUri().getScheme());
        request.setSecurityContext(sc);
    }
}
