package de.fau.cs.osr.amos.asepart;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.file.FileAlreadyExistsException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ManagedAsync;
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

        try (DatabaseClient db = new DatabaseClient())
        {
            if (db.isAdmin(loginName))
                return Response.status(Response.Status.BAD_REQUEST).build();

            if (db.isUser(loginName))
            {
                db.updateUser(loginName, user.get("firstName"), user.get("lastName"), user.get("phoneNumber"));

                if (user.containsKey("password"))
                    db.changePassword(loginName, user.get("password"));
            }

            else
            {
                if (!user.containsKey("password"))
                    return Response.status(Response.Status.BAD_REQUEST).build();

                db.insertUser(loginName, user.get("firstName"), user.get("lastName"), user.get("phoneNumber"));
                db.changePassword(loginName, user.get("password"));
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
        try (DatabaseClient db = new DatabaseClient())
        {
            return Response.ok(db.listUsers()).build();
        }
    }

    @Path("/users/{name}")
    @GET
    @RolesAllowed({"Admin"})
    public Response getUser(@Context SecurityContext sc, @PathParam("name") String user) throws Exception
    {
        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isUser(user))
                return Response.status(Response.Status.NOT_FOUND).build();

            return Response.ok(db.getUser(user)).build();
        }
    }

    @Path("/users/{name}")
    @DELETE
    @RolesAllowed({"Admin"})
    public Response deleteUser(@Context SecurityContext sc, @PathParam("name") String user) throws Exception
    {
        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isUser(user))
                return Response.status(Response.Status.NOT_FOUND).build();

            db.deleteAccount(user);
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

        try (DatabaseClient db = new DatabaseClient())
        {
            if (db.isUser(loginName))
                return Response.status(Response.Status.BAD_REQUEST).build();

            if (db.isAdmin(loginName))
            {
                db.updateAdmin(loginName, admin.get("firstName"), admin.get("lastName"));

                if (admin.containsKey("password"))
                {
                    if (sc.getUserPrincipal().getName().equals(loginName))
                        db.changePassword(loginName, admin.get("password"));
                    else return Response.status(Response.Status.FORBIDDEN).build();
                }
            }

            else
            {
                if (!admin.containsKey("password"))
                    return Response.status(Response.Status.BAD_REQUEST).build();

                db.insertAdmin(loginName, admin.get("firstName"), admin.get("lastName"));
                db.changePassword(loginName, admin.get("password"));
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
        try (DatabaseClient db = new DatabaseClient())
        {
            return Response.ok(db.listAdmins()).build();
        }
    }

    @Path("/admins/{name}")
    @GET
    @RolesAllowed({"Admin"})
    public Response getAdmin(@Context SecurityContext sc, @PathParam("name") String admin) throws Exception
    {
        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isAdmin(admin))
                return Response.status(Response.Status.NOT_FOUND).build();

            return Response.ok(db.getAdmin(admin)).build();
        }
    }

    @Path("/admins/{name}")
    @DELETE
    @RolesAllowed({"Admin"})
    public Response deleteAdmin(@Context SecurityContext sc, @PathParam("name") String admin) throws Exception
    {
        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isAdmin(admin))
                return Response.status(Response.Status.NOT_FOUND).build();

            db.deleteAccount(admin);
        }

        return Response.ok().build();
    }

    @Path("/projects")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response listProjects(@Context SecurityContext sc) throws Exception
    {
        try (DatabaseClient db = new DatabaseClient())
        {
            return Response.ok(db.listProjects(sc.getUserPrincipal().getName())).build();
        }
    }

    @Path("/projects")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response writeProject(@Context SecurityContext sc, Map<String, String> project) throws Exception
    {
        try (DatabaseClient db = new DatabaseClient())
        {
            String entryKey = project.get("entryKey");

            if (db.isProject(entryKey))
            {
                if (!db.isAdminOwnerOfProject(sc.getUserPrincipal().getName(), entryKey))
                    return Response.status(Response.Status.FORBIDDEN).build();

                db.updateProject(entryKey, project.get("name"), project.get("owner"));
            }

            else db.insertProject(entryKey, project.get("name"), project.get("owner")); 
        }

        return Response.ok().build();
    }

    @Path("/projects/{key}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response getProject(@Context SecurityContext sc, @PathParam("key") String entryKey) throws Exception
    {
        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isProject(entryKey))
                return Response.status(Response.Status.NOT_FOUND).build();

            return Response.ok(db.getProject(entryKey)).build();
        }
    }
    
    @Path("/projects/{key}")
    @DELETE
    @RolesAllowed({"Admin"})
    public Response deleteProject(@Context SecurityContext sc, @PathParam("key") String entryKey) throws Exception
    {
        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isProject(entryKey))
                return Response.status(Response.Status.NOT_FOUND).build();
            if (!db.isAdminOwnerOfProject(sc.getUserPrincipal().getName(), entryKey))
                return Response.status(Response.Status.FORBIDDEN).build();

            db.deleteProject(entryKey);
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

        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isProject(projectKey))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> project = db.getProject(projectKey);

            if (role.equals("Admin") && !project.get("owner").equals(principal.getName()))
            {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            else if (role.equals("User") && !db.isUserMemberOfProject(principal.getName(), projectKey))
            {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            List<Map<String, String>> tickets = db.getTicketsOfProject(projectKey);

            if (role.equals("User"))
            {
                for (Map<String, String> ticket : tickets)
                {
                    int ticketId = Integer.parseInt(ticket.get("id"));

                    if (ticket.get("status").equals("open") && db.hasUserAcceptedTicket(principal.getName(), ticketId))
                    {
                        ticket.put("status", "accepted");

                        if (db.observationCount(principal.getName(), ticketId) > 0)
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

        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isProject(projectKey))
                return Response.status(Response.Status.NOT_FOUND).build();

            if (!db.isAdminOwnerOfProject(sc.getUserPrincipal().getName(), projectKey))
                return Response.status(Response.Status.FORBIDDEN).build();

            if (ticket.containsKey("id"))
            {
                int id = Integer.parseInt(ticket.get("id"));

                if (!db.isTicket(id))
                    return Response.status(Response.Status.NOT_FOUND).build();

                db.updateTicket(id, ticket.get("name"), ticket.get("summary"),
                                          ticket.get("description"), ticket.get("category"),
                                          Integer.parseInt(ticket.get("requiredObservations")));

                return Response.ok().build();
            }

            else
            {
                db.insertTicket(ticket.get("name"), ticket.get("summary"),
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

        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = db.getTicket(ticketId);
            Map<String, String> project = db.getProject(ticket.get("projectKey"));

            if (role.equals("Admin") && !project.get("owner").equals(principal.getName()))
                return Response.status(Response.Status.FORBIDDEN).build();

            else if (role.equals("User"))
            {
                if (!db.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }

                if (ticket.get("status").equals("open") && db.hasUserAcceptedTicket(principal.getName(), ticketId))
                {
                    ticket.put("status", "accepted");

                    if (db.observationCount(principal.getName(), ticketId) > 0)
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
        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = db.getTicket(ticketId);

            if (!db.isAdminOwnerOfProject(sc.getUserPrincipal().getName(), ticket.get("projectKey")))
                return Response.status(Response.Status.FORBIDDEN).build();

            db.deleteTicket(ticketId);
        }

        try
        {
            FileStorageClient fs = new FileStorageClient();
            fs.cascade(ticketId);
        }

        catch (UnsupportedOperationException e)
        {
            // ignore if file storage is not enabled
        }

        return Response.ok().build();
    }

    @Path("/tickets/{id}/accept")
    @POST
    @RolesAllowed({"User"})
    public Response acceptTicket(@Context SecurityContext sc, @PathParam("id") int ticketId) throws Exception
    {
        Principal principal = sc.getUserPrincipal();

        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = db.getTicket(ticketId);

            if (!db.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                return Response.status(Response.Status.FORBIDDEN).build();

            if (!db.hasUserAcceptedTicket(principal.getName(), ticketId))
                db.acceptTicket(principal.getName(), ticketId);
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

        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = db.getTicket(ticketId);

            if (!db.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")) || !db.hasUserAcceptedTicket(principal.getName(), ticketId))
                return Response.status(Response.Status.FORBIDDEN).build();

            db.submitObservation(principal.getName(), ticketId,
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

        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = db.getTicket(ticketId);
            Map<String, String> project = db.getProject(ticket.get("projectKey"));

            if (role.equals("Admin") && !project.get("owner").equals(principal.getName()))
                return Response.status(Response.Status.FORBIDDEN).build();

            if (role.equals("User") && !db.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                return Response.status(Response.Status.FORBIDDEN).build();

            return Response.ok(db.listObservations(ticketId)).build();
        }
    }

    @Path("/projects/{key}/users")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response getUsersOfProject(@Context SecurityContext sc, @PathParam("key") String projectKey) throws Exception
    {
        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isProject(projectKey))
                return Response.status(Response.Status.NOT_FOUND).build();
            if (!db.isAdminOwnerOfProject(sc.getUserPrincipal().getName(), projectKey))
                return Response.status(Response.Status.FORBIDDEN).build();

            List<Map<String, String>> users = db.getUsersOfProject(projectKey);

            return Response.ok(users).build();
        }
    }

    @Path("/projects/{key}/users/{name}")
    @DELETE
    @RolesAllowed({"Admin"})
    public Response removeUserFromProject(@Context SecurityContext sc, @PathParam("key") String entryKey, @PathParam("name") String user) throws Exception
    {
        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isProject(entryKey) || !db.isUser(user))
                return Response.status(Response.Status.NOT_FOUND).build();
            if (!db.isAdminOwnerOfProject(sc.getUserPrincipal().getName(), entryKey))
                return Response.status(Response.Status.FORBIDDEN).build();
            if (!db.isUserMemberOfProject(user, entryKey))
                return Response.status(Response.Status.BAD_REQUEST).build();

            db.leaveProject(user, entryKey);
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

        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isProject(entryKey) || !db.isUser(user))
                return Response.status(Response.Status.NOT_FOUND).build();
            if (db.isUserMemberOfProject(user, entryKey))
                return Response.status(Response.Status.BAD_REQUEST).build();

            db.joinProject(user, entryKey);
        }

        return Response.ok().build();
    }

    @Path("/join")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"User"})
    public Response joinProjectPreview(@Context SecurityContext sc, @QueryParam("key") String entryKey) throws Exception
    {
        try (DatabaseClient db = new DatabaseClient())
        {
            Map<String, String> project = db.getProject(entryKey);
            return Response.ok(project.get("name")).build();
        }
    }

    @Path("/messages/{ticket}")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @RolesAllowed({"Admin", "User"})
    public Response sendMessage(@Context SecurityContext sc,
                                @PathParam("ticket") int ticketId,
                                @QueryParam("attachment") String attachment, String message) throws Exception
    {
        Principal principal = sc.getUserPrincipal();
        final String role = sc.isUserInRole("Admin") ? "Admin" : "User";

        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = db.getTicket(ticketId);
            Map<String, String> project = db.getProject(ticket.get("projectKey"));

            if (role.equals("Admin") && !project.get("owner").equals(principal.getName()))
                return Response.status(Response.Status.FORBIDDEN).build();

            if (role.equals("User") && !db.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                return Response.status(Response.Status.FORBIDDEN).build();

            db.sendMessage(principal.getName(), message, attachment, ticketId);
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

        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = db.getTicket(ticketId);
            Map<String, String> project = db.getProject(ticket.get("projectKey"));

            if (role.equals("Admin") && !project.get("owner").equals(principal.getName()))
                return Response.status(Response.Status.FORBIDDEN).build();

            if (role.equals("User") && !db.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                return Response.status(Response.Status.FORBIDDEN).build();

            return Response.ok(db.listMessages(ticketId)).build();
        }
    }

    @Path("/listen/{ticket}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin", "User"})
    @ManagedAsync
    public void listenChannel(@Suspended final AsyncResponse response, @Context SecurityContext sc,
                              @PathParam("ticket") int ticketId) throws Exception
    {

        Principal principal = sc.getUserPrincipal();
        final String role = sc.isUserInRole("Admin") ? "Admin" : "User";

        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isTicket(ticketId))
                response.resume(Response.status(Response.Status.NOT_FOUND).build());

            Map<String, String> ticket = db.getTicket(ticketId);
            Map<String, String> project = db.getProject(ticket.get("projectKey"));

            if (role.equals("Admin") && !project.get("owner").equals(principal.getName()))
                response.resume(Response.status(Response.Status.FORBIDDEN).build());

            if (role.equals("User") && !db.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                response.resume(Response.status(Response.Status.FORBIDDEN).build());

            try
            {
                List<Map<String, String>> messages = db.listenChannel(ticketId);
                response.resume(Response.ok(messages).build());
            }

            catch (SQLException e)
            {
                response.resume(Response.serverError().build());
            }
        }
    }

    @Path("/files/{ticket}")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed({"Admin", "User"})
    public Response uploadFile(@Context SecurityContext sc, @PathParam("ticket") int ticketId,
                               @FormDataParam("file") InputStream stream,
                               @FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception
    {
        Principal principal = sc.getUserPrincipal();
        final String role = sc.isUserInRole("Admin") ? "Admin" : "User";

        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = db.getTicket(ticketId);
            Map<String, String> project = db.getProject(ticket.get("projectKey"));

            if (role.equals("Admin") && !project.get("owner").equals(principal.getName()))
                return Response.status(Response.Status.FORBIDDEN).build();

            if (role.equals("User") && !db.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                return Response.status(Response.Status.FORBIDDEN).build();
        }

        try
        {
            FileStorageClient fs = new FileStorageClient();
            fs.upload(ticketId, fileDetail.getFileName(), stream);

            return Response.ok().build();
        }

        catch (FileAlreadyExistsException e)
        {
            return Response.status(Response.Status.CONFLICT).build();
        }

        catch (UnsupportedOperationException e)
        {
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        }
    }

    @Path("/files/{ticket}/{file}")
    @GET
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed({"Admin", "User"})
    public Response downloadFile(@Context SecurityContext sc,
                                 @PathParam("ticket") int ticketId, @PathParam("file") String fileName) throws Exception
    {
        Principal principal = sc.getUserPrincipal();
        final String role = sc.isUserInRole("Admin") ? "Admin" : "User";

        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = db.getTicket(ticketId);
            Map<String, String> project = db.getProject(ticket.get("projectKey"));

            if (role.equals("Admin") && !project.get("owner").equals(principal.getName()))
                return Response.status(Response.Status.FORBIDDEN).build();

            if (role.equals("User") && !db.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                return Response.status(Response.Status.FORBIDDEN).build();
        }

        try
        {
            FileStorageClient fs = new FileStorageClient();
            InputStream stream = fs.download(ticketId, fileName);

            return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
                    .header("content-disposition", "attachment; filename = " + fileName).build();
        }

        catch (UnsupportedOperationException e)
        {
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        }
    }

    @Path("/files/{ticket}/{file}")
    @DELETE
    @RolesAllowed({"Admin", "User"})
    public Response removeFile(@Context SecurityContext sc,
                               @PathParam("ticket") int ticketId, @PathParam("file") String fileName) throws Exception
    {
        Principal principal = sc.getUserPrincipal();
        final String role = sc.isUserInRole("Admin") ? "Admin" : "User";

        try (DatabaseClient db = new DatabaseClient())
        {
            if (!db.isTicket(ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Map<String, String> ticket = db.getTicket(ticketId);
            Map<String, String> project = db.getProject(ticket.get("projectKey"));

            if (role.equals("Admin") && !project.get("owner").equals(principal.getName()))
                return Response.status(Response.Status.FORBIDDEN).build();

            if (role.equals("User") && !db.isUserMemberOfProject(principal.getName(), ticket.get("projectKey")))
                return Response.status(Response.Status.FORBIDDEN).build();
        }

        try
        {
            FileStorageClient fs = new FileStorageClient();
            fs.remove(ticketId, fileName);

            return Response.ok().build();
        }

        catch (UnsupportedOperationException e)
        {
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        }
    }

    static String address = "http://localhost/";
    static int port = 12345;
    
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
            address = "http://" + ip + "/";
            final URI uri = UriBuilder.fromUri(address).port(port).build();

            ResourceConfig config = new ResourceConfig(WebService.class);

            config.register(CORSFilter.class); // allow cross-origin requests
            config.register(AuthenticationFilter.class); // enable authentication

            config.register(DebugExceptionMapper.class); // display exceptions in server log
            config.register(MultiPartFeature.class); // enable file upload

            GrizzlyHttpServerFactory.createHttpServer(uri, config);
        }

        catch (UnknownHostException e)
        {
            System.err.println("Failed to get server's own ip address.");
            e.printStackTrace();
        }
    }
}
