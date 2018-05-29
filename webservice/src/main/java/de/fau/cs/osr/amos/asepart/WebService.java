package de.fau.cs.osr.amos.asepart;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.Principal;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
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

import org.hibernate.Session;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import de.fau.cs.osr.amos.asepart.filters.AuthenticationFilter;
import de.fau.cs.osr.amos.asepart.filters.CORSFilter;
import de.fau.cs.osr.amos.asepart.entities.*;

@Path("/")
public class WebService
{
    @Path("/login")
    @OPTIONS
    @PermitAll
    public Response login()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

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

    @Path("/projects")
    @OPTIONS
    @PermitAll
    public Response projects()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/projects")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response listProjects(@Context SecurityContext sc)
    {
        try (Session session = Database.openSession())
        {
            return Response.ok(Database.listProjects(session, sc.getUserPrincipal().getName())).build();
        }
    }

    @Path("/projects")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response writeProject(@Context SecurityContext sc, Project project)
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            Project target;

            if (Database.isProject(session, project.getEntryKey()))
            {
                Project oldProject = Database.getProject(session, project.getEntryKey());

                if (!oldProject.getOwner().equals(sc.getUserPrincipal().getName()))
                {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }

                target = oldProject;
                target.setProjectName(project.getProjectName());
                target.setOwner(project.getOwner());
            }

            else target = project;

            Database.writeProject(session, target);
            session.getTransaction().commit();
        }

        return Response.ok().build();
    }

    @Path("/projects/{key}")
    @OPTIONS
    @PermitAll
    public Response project()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/projects/{key}")
    @DELETE
    @RolesAllowed({"Admin"})
    public Response deleteProject(@Context SecurityContext sc, @PathParam("key") String entryKey)
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            if (!Database.isProject(session, entryKey))
                return Response.status(Response.Status.NOT_FOUND).build();
            if (!Database.isAdminOfProject(session, sc.getUserPrincipal().getName(), entryKey))
                return Response.status(Response.Status.FORBIDDEN).build();

            Database.deleteProject(session, entryKey);
            session.getTransaction().commit();
        }

        return Response.ok().build();
    }

    @Path("/projects/{key}/tickets/")
    @OPTIONS
    @PermitAll
    public Response tickets()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/projects/{key}/tickets/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response writeTicket(@Context SecurityContext sc, @PathParam("key") String projectKey, Ticket ticket)
    {
        ticket.setProjectKey(projectKey);

        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            if (!Database.isProject(session, projectKey))
                return Response.status(Response.Status.NOT_FOUND).build();

            if (!Database.isAdminOfProject(session, sc.getUserPrincipal().getName(), projectKey))
                return Response.status(Response.Status.FORBIDDEN).build();

            Ticket target;

            if (ticket.getId() != null && Database.isTicket(session, ticket.getId()))
            {
                target = Database.getTicket(session, ticket.getId());
                target.setTicketName(ticket.getTicketName());
                target.setTicketSummary(ticket.getTicketSummary());
                target.setTicketDescription(ticket.getTicketDescription());
                target.setTicketCategory(ticket.getTicketCategory());
                target.setRequiredObservations(ticket.getRequiredObservations());
                target.setProjectKey(ticket.getProjectKey());
            }

            else target = ticket;

            Database.writeTicket(session, target);
            session.getTransaction().commit();

            return Response.ok().build();
        }
    }

    @Path("/projects/{key}/tickets")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin", "User"})
    public Response getTicketsOfProject(@Context SecurityContext sc, @PathParam("key") String projectKey)
    {
        Principal principal = sc.getUserPrincipal();
        final String role = sc.isUserInRole("Admin") ? "Admin" : "User";

        try (Session session = Database.openSession())
        {
            Project project = Database.getProject(session, projectKey);

            if (role.equals("Admin") && !project.getOwner().equals(principal.getName()))
            {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            else if (role.equals("User") && !Database.isUserMemberOfProject(session, principal.getName(), projectKey))
            {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            Ticket[] tickets = Database.getTicketsOfProject(session, projectKey);
            return Response.ok(tickets).build();
        }
    }

    @Path("/projects/{key}/tickets/{id}")
    @OPTIONS
    @PermitAll
    public Response ticket()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/projects/{key}/tickets/{id}")
    @GET
    @RolesAllowed({"Admin", "User"})
    public Response getTicket(@Context SecurityContext sc, @PathParam("key") String projectKey, @PathParam("id") Integer ticketId)
    {
        Principal principal = sc.getUserPrincipal();
        final String role = sc.isUserInRole("Admin") ? "Admin" : "User";

        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            if (!Database.isProject(session, projectKey) || !Database.isTicket(session, ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Ticket ticket = Database.getTicket(session, ticketId);
            Project project = Database.getProject(session, projectKey);

            if (!ticket.getProjectKey().equals(projectKey))
                return Response.status(Response.Status.BAD_REQUEST).build();

            if (role.equals("Admin") && !project.getOwner().equals(principal.getName()))
                return Response.status(Response.Status.FORBIDDEN).build();

            else if (role.equals("User") && !Database.isUserMemberOfProject(session, principal.getName(), projectKey))
                return Response.status(Response.Status.FORBIDDEN).build();

            session.getTransaction().commit();

            return Response.ok(ticket).build();
        }
    }

    @Path("/projects/{key}/tickets/{id}")
    @DELETE
    @RolesAllowed({"Admin"})
    public Response deleteTicket(@Context SecurityContext sc, @PathParam("key") String projectKey, @PathParam("id") Integer ticketId)
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            Ticket oldTicket = Database.getTicket(session, ticketId);

            if (oldTicket == null)
                return Response.status(Response.Status.NOT_FOUND).build();
            if (!oldTicket.getProjectKey().equals(projectKey))
                return Response.status(Response.Status.BAD_REQUEST).build();
            if (!Database.isAdminOfProject(session, sc.getUserPrincipal().getName(), oldTicket.getProjectKey()))
                return Response.status(Response.Status.FORBIDDEN).build();

            Database.deleteTicket(session, ticketId);
            session.getTransaction().commit();
        }

        return Response.ok().build();
    }

    @Path("/projects/{key}/tickets/{id}/accept")
    @OPTIONS
    @PermitAll
    public Response acceptTicket()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/projects/{key}/tickets/{id}/accept")
    @POST
    @RolesAllowed({"User"})
    public Response acceptTicket(@Context SecurityContext sc, @PathParam("key") String projectKey, @PathParam("id") Integer ticketId)
    {
        Principal principal = sc.getUserPrincipal();

        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            if (!Database.isProject(session, projectKey) || !Database.isTicket(session, ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Ticket ticket = Database.getTicket(session, ticketId);

            if (!ticket.getProjectKey().equals(projectKey))
                return Response.status(Response.Status.BAD_REQUEST).build();

            if (!Database.isUserMemberOfProject(session, principal.getName(), projectKey))
                return Response.status(Response.Status.FORBIDDEN).build();

            Database.acceptTicket(session, principal.getName(), ticketId);
            session.getTransaction().commit();

            return Response.ok().build();
        }
    }

    @Path("/projects/{key}/users")
    @OPTIONS
    @PermitAll
    public Response getUsersOfProject()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/projects/{key}/users")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response getUsersOfProject(@Context SecurityContext sc, @PathParam("key") String projectKey)
    {
        try (Session session = Database.openSession())
        {
            if (!Database.isProject(session, projectKey))
                return Response.status(Response.Status.NOT_FOUND).build();
            if (!Database.isAdminOfProject(session, sc.getUserPrincipal().getName(), projectKey))
                return Response.status(Response.Status.FORBIDDEN).build();

            User[] users = Database.getUsersOfProject(session, projectKey);
            return Response.ok(users).build();
        }
    }

    @Path("/projects/{key}/users/{username}")
    @OPTIONS
    @PermitAll
    public Response removeUserFromProject()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/projects/{key}/users/{username}")
    @DELETE
    @RolesAllowed({"Admin"})
    public Response removeUserFromProject(@Context SecurityContext sc, @PathParam("key") String projectKey, @PathParam("username") String user)
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            if (!Database.isUser(session, user))
                return Response.status(Response.Status.NOT_FOUND).entity("User does not exist.").build();

            if (!Database.isProject(session, projectKey))
                return Response.status(Response.Status.NOT_FOUND).entity("Project does not exist.").build();

            if (!Database.isUserMemberOfProject(session, user, projectKey))
                return Response.status(Response.Status.BAD_REQUEST).entity("User not member of project.").build();

            Database.leaveProject(session, user, projectKey);
            session.getTransaction().commit();
        }

        return Response.ok().build();
    }

    @Path("/join")
    @OPTIONS
    @PermitAll
    public Response joinProject()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/join")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @RolesAllowed({"User"})
    public Response joinProject(@Context SecurityContext sc, String entryKey)
    {
        final String userName = sc.getUserPrincipal().getName();

        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            if (!Database.isUser(session, userName))
                return Response.status(Response.Status.NOT_FOUND).entity("User does not exist.").build();

            if (!Database.isProject(session, entryKey))
                return Response.status(Response.Status.NOT_FOUND).entity("Project does not exist.").build();

            if (Database.isUserMemberOfProject(session, userName, entryKey))
                return Response.status(Response.Status.BAD_REQUEST).entity("User already member of project.").build();

            Database.joinProject(session, userName, entryKey);
            session.getTransaction().commit();

            return Response.ok().build();
        }
    }

    @Path("/join")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"User"})
    public Response joinProjectPreview(@Context SecurityContext sc, @QueryParam("key") String entryKey)
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();
            Project project = Database.getProject(session, entryKey);
            session.getTransaction().commit();

            return Response.ok(project.getProjectName()).build();
        }
    }

    @Path("/messages/{ticket}")
    @OPTIONS
    @PermitAll
    public Response messages()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/messages/{ticket}")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @RolesAllowed({"Admin", "User"})
    public Response sendMessage(@Context SecurityContext sc, @PathParam("ticket") Integer ticketId, String message)
    {
        Principal principal = sc.getUserPrincipal();
        final String role = sc.isUserInRole("Admin") ? "Admin" : "User";

        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            Ticket ticket = Database.getTicket(session, ticketId);
            Project project = Database.getProject(session, ticket.getProjectKey());

            if (role.equals("Admin") && !project.getOwner().equals(principal.getName()))
            {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            if (role.equals("User") && !Database.isUserMemberOfProject(session, principal.getName(), ticket.getProjectKey()))
            {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            Database.sendMessage(session, ticketId, message, principal.getName());
            session.getTransaction().commit();
        }

        return Response.ok().build();
    }

    @Path("/messages/{ticket}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin", "User"})
    public Response listMessages(@Context SecurityContext sc, @PathParam("ticket") Integer ticketId)
    {
        Principal principal = sc.getUserPrincipal();
        final String role = sc.isUserInRole("Admin") ? "Admin" : "User";

        try (Session session = Database.openSession())
        {
            if (!Database.isTicket(session, ticketId))
                return Response.status(Response.Status.NOT_FOUND).build();

            Ticket ticket = Database.getTicket(session, ticketId);
            Project project = Database.getProject(session, ticket.getProjectKey());

            if (role.equals("Admin") && !project.getOwner().equals(principal.getName()))
            {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            if (role.equals("User") && !Database.isUserMemberOfProject(session, principal.getName(), ticket.getProjectKey()))
            {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            return Response.ok(Database.listMessages(session, ticketId)).build();
        }
    }

    @Path("/users")
    @OPTIONS
    @PermitAll
    public Response users()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/users")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response writeUser(@Context SecurityContext sc, User user)
    {
        try (Session session = Database.openSession())
        {
            final String loginName = user.getLoginName();

            session.beginTransaction();

            User target;

            if (Database.isUser(session, loginName))
            {
                target = Database.getUser(session, loginName);
                target.setFirstName(user.getFirstName());
                target.setLastName(user.getLastName());
                target.setPassword(user.getPassword());
                target.setPhone(user.getPhone());
            }

            else target = user;

            if (Database.isAdmin(session, user.getLoginName()))
                return Response.status(Response.Status.BAD_REQUEST).build();

            Database.writeUser(session, target);
            session.getTransaction().commit();

            return Response.ok().build();
        }
    }

    @Path("/users")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response listUsers(@Context SecurityContext sc)
    {
        try (Session session = Database.openSession())
        {
            return Response.ok(Database.listUsers(session)).build();
        }
    }

    @Path("/users/{username}")
    @OPTIONS
    @PermitAll
    public Response deleteUser()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/users/{username}")
    @DELETE
    @RolesAllowed({"Admin"})
    public Response deleteUser(@Context SecurityContext sc, @PathParam("username") String userName)
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            if (!Database.isUser(session, userName))
                return Response.status(Response.Status.NOT_FOUND).build();

            Database.deleteUser(session, userName);

            session.getTransaction().commit();
        }

        return Response.ok().build();
    }

    @Path("/admins")
    @OPTIONS
    @PermitAll
    public Response admins()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/admins")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response putAdmin(@Context SecurityContext sc, Admin admin)
    {
        try (Session session = Database.openSession())
        {
            final String loginName = admin.getLoginName();

            session.beginTransaction();

            Admin target;

            if (Database.isAdmin(session, loginName))
            {
                target = Database.getAdmin(session, loginName);
                target.setFirstName(admin.getFirstName());
                target.setLastName(admin.getLastName());
                target.setPassword(admin.getPassword());
            }

            else target = admin;

            if (Database.isUser(session, admin.getLoginName()))
                return Response.status(Response.Status.BAD_REQUEST).build();

            Database.writeAdmin(session, target);
            session.getTransaction().commit();

            return Response.ok().build();
        }
    }

    @Path("/admins")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response listAdmins(@Context SecurityContext sc)
    {
        try (Session session = Database.openSession())
        {
            return Response.ok(Database.listAdmins(session)).build();
        }
    }

    @Path("/admins/{adminname}")
    @DELETE
    @RolesAllowed({"Admin"})
    public Response deleteAdmin(@Context SecurityContext sc, @PathParam("adminname") String adminName)
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            if (!Database.isAdmin(session, adminName))
                return Response.status(Response.Status.NOT_FOUND).build();

            Database.deleteAdmin(session, adminName);

            session.getTransaction().commit();
        }

        return Response.ok().build();
    }

    public static void startBackground(int port)
    {
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
    
    public static void main(String[] args)
    {
        int port = 12345;

        try
        {
            port = Integer.parseInt(System.getenv("PORT"));
        }

        catch (NumberFormatException e)
        {
            System.err.println("Environment variable PORT not set, using default: " + port);
        }

        startBackground(port);
    }
}
