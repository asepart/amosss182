package de.fau.cs.osr.amos.asepart;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

@Path("/")
public class WebService
{
    @Path("/login")
    @GET
    @RolesAllowed({"Admin", "User"})
    public Response login(@Context SecurityContext sc)
    {
        /* If credentials are invalid, the method call will automatically fail.
         * This is done by the AuthenticationFilter, so if the return statement
         * below is reached only if the credentials have been validated already.
         */

        return Response.ok("Your identification is valid: " + sc.getUserPrincipal().getName()).build();
    }

    @Path("/users")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response writeUser(@Context SecurityContext sc, Map<String, String> user) throws Exception
    {
        final String loginName = user.get("loginName");

        try (DBClient dbClient = new DBClient())
        {
            if (dbClient.isAdmin(loginName))
                return Response.status(Response.Status.BAD_REQUEST).build();

            if (dbClient.isUser(loginName))
                dbClient.updateUser(loginName, user.get("password"), user.get("firstName"), user.get("lastName"), user.get("phoneNumber"));

            else
            {
                dbClient.insertUser(loginName, user.get("password"), user.get("firstName"), user.get("lastName"), user.get("phoneNumber"));
            }
        }

        return Response.ok().build();
    }

    @Path("/users")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response listUsers(@Context SecurityContext sc) throws Exception
    {
        try (DBClient dbClient = new DBClient())
        {
            return Response.ok(dbClient.listUsers()).build();
        }
    }

    @Path("/users/{name}")
    @GET
    @RolesAllowed({"Admin"})
    public Response getUser(@Context SecurityContext sc, @PathParam("name") String user) throws Exception
    {
        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isUser(user))
                return Response.status(Response.Status.NOT_FOUND).build();

            return Response.ok(dbClient.getUser(user)).build();
        }
    }

    @Path("/users/{name}")
    @DELETE
    @RolesAllowed({"Admin"})
    public Response deleteUser(@Context SecurityContext sc, @PathParam("name") String user) throws Exception
    {
        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isUser(user))
                return Response.status(Response.Status.NOT_FOUND).build();

            dbClient.deleteAccount(user);
        }

        return Response.ok().build();
    }

    @Path("/admins")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response writeAdmin(@Context SecurityContext sc, Map<String, String> admin) throws Exception
    {
        final String loginName = admin.get("loginName");

        try (DBClient dbClient = new DBClient())
        {
            if (dbClient.isUser(loginName))
                return Response.status(Response.Status.BAD_REQUEST).build();

            if (dbClient.isAdmin(loginName))
                dbClient.updateAdmin(loginName, admin.get("password"), admin.get("firstName"), admin.get("lastName"));

            else
            {
                dbClient.insertAdmin(loginName, admin.get("password"), admin.get("firstName"), admin.get("lastName"));
            }
        }

        return Response.ok().build();
    }

    @Path("/admins")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response listAdmins(@Context SecurityContext sc) throws Exception
    {
        try (DBClient dbClient = new DBClient())
        {
            return Response.ok(dbClient.listAdmins()).build();
        }
    }

    @Path("/admins/{name}")
    @GET
    @RolesAllowed({"Admin"})
    public Response getAdmin(@Context SecurityContext sc, @PathParam("name") String admin) throws Exception
    {
        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isAdmin(admin))
                return Response.status(Response.Status.NOT_FOUND).build();

            return Response.ok(dbClient.getAdmin(admin)).build();
        }
    }

    @Path("/admins/{name}")
    @DELETE
    @RolesAllowed({"Admin"})
    public Response deleteAdmin(@Context SecurityContext sc, @PathParam("name") String admin) throws Exception
    {
        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isAdmin(admin))
                return Response.status(Response.Status.NOT_FOUND).build();

            dbClient.deleteAccount(admin);
        }

        return Response.ok().build();
    }

    @Path("/projects")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response listProjects(@Context SecurityContext sc) throws Exception
    {
        try (DBClient dbClient = new DBClient())
        {
            return Response.ok(dbClient.listProjects(sc.getUserPrincipal().getName())).build();
        }
    }

    @Path("/projects")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response writeProject(@Context SecurityContext sc, Map<String, String> project) throws Exception
    {
        try (DBClient dbClient = new DBClient())
        {
            String entryKey = project.get("entryKey");

            if (dbClient.isProject(entryKey))
            {
                if (!dbClient.isAdminOwnerOfProject(sc.getUserPrincipal().getName(), entryKey))
                    return Response.status(Response.Status.FORBIDDEN).build();

                dbClient.updateProject(entryKey, project.get("name"), project.get("owner"));
            }

            else dbClient.insertProject(entryKey, project.get("name"), project.get("owner")); 
        }

        return Response.ok().build();
    }

    @Path("/projects/{key}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response getProject(@Context SecurityContext sc, @PathParam("key") String entryKey) throws Exception
    {
        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isProject(entryKey))
                return Response.status(Response.Status.NOT_FOUND).build();

            return Response.ok(dbClient.getProject(entryKey)).build();
        }
    }
    
    @Path("/projects/{key}")
    @DELETE
    @RolesAllowed({"Admin"})
    public Response deleteProject(@Context SecurityContext sc, @PathParam("key") String entryKey) throws Exception
    {
        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isProject(entryKey))
                return Response.status(Response.Status.NOT_FOUND).build();
            if (!dbClient.isAdminOwnerOfProject(sc.getUserPrincipal().getName(), entryKey))
                return Response.status(Response.Status.FORBIDDEN).build();

            dbClient.deleteProject(entryKey);
        }

        return Response.ok().build();
    }

    @Path("/projects/{key}/tickets")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin", "User"})
    public Response getTicketsOfProject(@Context SecurityContext sc, @PathParam("key") String projectKey) throws Exception
    {
        Principal principal = sc.getUserPrincipal();
        final String role = sc.isUserInRole("Admin") ? "Admin" : "User";

        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isProject(projectKey))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> project = dbClient.getProject(projectKey);

            if (role.equals("Admin") && !project.get("owner").equals(principal.getName()))
            {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            else if (role.equals("User") && !dbClient.isUserMemberOfProject(principal.getName(), projectKey))
            {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            List<Map<String, String>> tickets = dbClient.getTicketsOfProject(projectKey);

            if (role.equals("User"))
            {
                for (Map<String, String> ticket : tickets)
                {
                    int ticketId = Integer.parseInt(ticket.get("id"));

                    if (ticket.get("status").equals("open") && dbClient.hasUserAcceptedTicket(principal.getName(), ticketId))
                    {
                        ticket.put("status", "accepted");

                        if (dbClient.observationCount(principal.getName(), ticketId) > 0)
                        {
                            ticket.put("status", "processed");
                        }
                    }
                }
            }

            return Response.ok(tickets).build();
        }
    }

    @Path("/tickets/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"Admin"})
    public Response writeTicket(@Context SecurityContext sc, Map<String, String> ticket) throws Exception
    {
        if (!ticket.containsKey("projectKey"))
            return Response.status(Response.Status.BAD_REQUEST).build();

        String projectKey = ticket.get("projectKey");

        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isProject(projectKey))
                return Response.status(Response.Status.NOT_FOUND).build();

            if (!dbClient.isAdminOwnerOfProject(sc.getUserPrincipal().getName(), projectKey))
                return Response.status(Response.Status.FORBIDDEN).build();

            if (ticket.containsKey("id"))
            {
                int id = Integer.parseInt(ticket.get("id"));

                if (!dbClient.isTicket(id))
                    return Response.status(Response.Status.NOT_FOUND).build();

                dbClient.updateTicket(id, ticket.get("name"), ticket.get("summary"),
                                          ticket.get("description"), ticket.get("category"),
                                          Integer.parseInt(ticket.get("requiredObservations")));

                return Response.ok().build();
            }

            else
            {
                dbClient.insertTicket(ticket.get("name"), ticket.get("summary"),
                         ticket.get("description"), ticket.get("category"),
                         Integer.parseInt(ticket.get("requiredObservations")), projectKey);

                return Response.ok().build();
            }
        }
    }

    @Path("/tickets/{id}")
    @GET
    @RolesAllowed({"Admin", "User"})
    public Response getTicket(@Context SecurityContext sc, @PathParam("id") int ticketId) throws Exception
    {
        Principal principal = sc.getUserPrincipal();
        final String role = sc.isUserInRole("Admin") ? "Admin" : "User";

        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = dbClient.getTicket(ticketId);
            Map<String, String> project = dbClient.getProject(ticket.get("projectKey"));

            if (role.equals("Admin") && !project.get("owner").equals(principal.getName()))
                return Response.status(Response.Status.FORBIDDEN).build();

            else if (role.equals("User"))
            {
                if (!dbClient.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }

                if (ticket.get("status").equals("open") && dbClient.hasUserAcceptedTicket(principal.getName(), ticketId))
                {
                    ticket.put("status", "accepted");

                    if (dbClient.observationCount(principal.getName(), ticketId) > 0)
                    {
                        ticket.put("status", "processed");
                    }
                }
            }

            return Response.ok(ticket).build();
        }
    }

    @Path("/tickets/{id}")
    @DELETE
    @RolesAllowed({"Admin"})
    public Response deleteTicket(@Context SecurityContext sc, @PathParam("id") int ticketId) throws Exception
    {
        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = dbClient.getTicket(ticketId);

            if (!dbClient.isAdminOwnerOfProject(sc.getUserPrincipal().getName(), ticket.get("projectKey")))
                return Response.status(Response.Status.FORBIDDEN).build();

            dbClient.deleteTicket(ticketId);
        }

        return Response.ok().build();
    }

    @Path("/tickets/{id}/accept")
    @POST
    @RolesAllowed({"User"})
    public Response acceptTicket(@Context SecurityContext sc, @PathParam("id") int ticketId) throws Exception
    {
        Principal principal = sc.getUserPrincipal();

        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = dbClient.getTicket(ticketId);

            if (!dbClient.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                return Response.status(Response.Status.FORBIDDEN).build();

            if (!dbClient.hasUserAcceptedTicket(principal.getName(), ticketId))
                dbClient.acceptTicket(principal.getName(), ticketId);
        }

        return Response.ok().build();
    }

    @Path("/tickets/{id}/observations")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User"})
    public Response submitObservation(@Context SecurityContext sc, @PathParam("id") int ticketId, Map<String, String> observation) throws Exception
    {
        Principal principal = sc.getUserPrincipal();

        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = dbClient.getTicket(ticketId);

            if (!dbClient.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                return Response.status(Response.Status.FORBIDDEN).build();

            dbClient.submitObservation(principal.getName(), ticketId,
                    observation.get("outcome"), Integer.parseInt(observation.get("quantity")));
        }

        return Response.ok().build();
    }

    @Path("/tickets/{id}/observations")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin", "User"})
    public Response listObservations(@Context SecurityContext sc, @PathParam("id") int ticketId) throws Exception
    {
        Principal principal = sc.getUserPrincipal();
        final String role = sc.isUserInRole("Admin") ? "Admin" : "User";

        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = dbClient.getTicket(ticketId);
            Map<String, String> project = dbClient.getProject(ticket.get("projectKey"));

            if (role.equals("Admin") && !project.get("owner").equals(principal.getName()))
                return Response.status(Response.Status.FORBIDDEN).build();

            if (role.equals("User") && !dbClient.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                return Response.status(Response.Status.FORBIDDEN).build();

            return Response.ok(dbClient.listObservations(ticketId)).build();
        }
    }

    @Path("/projects/{key}/users")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response getUsersOfProject(@Context SecurityContext sc, @PathParam("key") String projectKey) throws Exception
    {
        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isProject(projectKey))
                return Response.status(Response.Status.NOT_FOUND).build();
            if (!dbClient.isAdminOwnerOfProject(sc.getUserPrincipal().getName(), projectKey))
                return Response.status(Response.Status.FORBIDDEN).build();

            List<Map<String, String>> users = dbClient.getUsersOfProject(projectKey);

            return Response.ok(users).build();
        }
    }

    @Path("/projects/{key}/users/{name}")
    @DELETE
    @RolesAllowed({"Admin"})
    public Response removeUserFromProject(@Context SecurityContext sc, @PathParam("key") String entryKey, @PathParam("name") String user) throws Exception
    {
        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isProject(entryKey) || !dbClient.isUser(user))
                return Response.status(Response.Status.NOT_FOUND).build();
            if (!dbClient.isAdminOwnerOfProject(sc.getUserPrincipal().getName(), entryKey))
                return Response.status(Response.Status.FORBIDDEN).build();
            if (!dbClient.isUserMemberOfProject(user, entryKey))
                return Response.status(Response.Status.BAD_REQUEST).build();

            dbClient.leaveProject(user, entryKey);
        }

        return Response.ok().build();
    }

    @Path("/join")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @RolesAllowed({"User"})
    public Response joinProject(@Context SecurityContext sc, String entryKey) throws Exception
    {
        final String user = sc.getUserPrincipal().getName();

        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isProject(entryKey) || !dbClient.isUser(user))
                return Response.status(Response.Status.NOT_FOUND).build();
            if (dbClient.isUserMemberOfProject(user, entryKey))
                return Response.status(Response.Status.BAD_REQUEST).build();

            dbClient.joinProject(user, entryKey);
        }

        return Response.ok().build();
    }

    @Path("/join")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"User"})
    public Response joinProjectPreview(@Context SecurityContext sc, @QueryParam("key") String entryKey) throws Exception
    {
        try (DBClient dbClient = new DBClient())
        {
            Map<String, String> project = dbClient.getProject(entryKey);
            return Response.ok(project.get("name")).build();
        }
    }

    @Path("/messages/{ticket}")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @RolesAllowed({"Admin", "User"})
    public Response sendMessage(@Context SecurityContext sc, @PathParam("ticket") int ticketId, String message) throws Exception
    {
        Principal principal = sc.getUserPrincipal();
        final String role = sc.isUserInRole("Admin") ? "Admin" : "User";

        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = dbClient.getTicket(ticketId);
            Map<String, String> project = dbClient.getProject(ticket.get("projectKey"));

            if (role.equals("Admin") && !project.get("owner").equals(principal.getName()))
                return Response.status(Response.Status.FORBIDDEN).build();

            if (role.equals("User") && !dbClient.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                return Response.status(Response.Status.FORBIDDEN).build();

            dbClient.sendMessage(principal.getName(), message, ticketId);
        }

        return Response.ok().build();
    }

    @Path("/messages/{ticket}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin", "User"})
    public Response listMessages(@Context SecurityContext sc, @PathParam("ticket") int ticketId) throws Exception
    {
        Principal principal = sc.getUserPrincipal();
        final String role = sc.isUserInRole("Admin") ? "Admin" : "User";

        try (DBClient dbClient = new DBClient())
        {
            if (!dbClient.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = dbClient.getTicket(ticketId);
            Map<String, String> project = dbClient.getProject(ticket.get("projectKey"));

            if (role.equals("Admin") && !project.get("owner").equals(principal.getName()))
                return Response.status(Response.Status.FORBIDDEN).build();

            if (role.equals("User") && !dbClient.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                return Response.status(Response.Status.FORBIDDEN).build();

            return Response.ok(dbClient.listMessages(ticketId)).build();
        }
    }

    public static int port = 12345;
    
    public static void main(String[] args)
    {
        try
        {
            port = Integer.parseInt(System.getenv("PORT"));
        }

        catch (NumberFormatException e)
        {
            System.err.println("Environment variable PORT not set, using default: " + port);
        }

        try
        {
            final String ip = InetAddress.getLocalHost().getHostAddress();
            final String address = "http://" + ip + "/";

            final URI uri = UriBuilder.fromUri(address).port(port).build();

            ResourceConfig config = new ResourceConfig(WebService.class);
            config.register(CORSFilter.class);
            config.register(AuthenticationFilter.class);
            config.register(DebugExceptionMapper.class);

            JdkHttpServerFactory.createHttpServer(uri, config);
        }

        catch (UnknownHostException e)
        {
            System.err.println("Failed to get server's own ip address.");
            e.printStackTrace();
        }
    }
}
