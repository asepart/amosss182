package de.fau.cs.osr.amos.asepart;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
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
         * below is reached the credentials have been validated already.
         */

        return Response.ok("Your identification is valid: " + sc.getUserPrincipal().getName()).build();
    }

    @Path("/tickets")
    @OPTIONS
    @PermitAll
    public Response tickets()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/tickets")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response createTicket(@Context SecurityContext sc, Ticket ticket)
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();
            Database.putTicket(session, ticket);
            session.getTransaction().commit();

            return Response.ok(String.format("Added new ticket with name \"%s\".", ticket.getTicketName())).build();
        }
    }
    
    @Path("/projects/{name}/tickets/{ticketname}")
    @OPTIONS
    @PermitAll
    public Response addTicketToProject()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/projects/{name}/tickets/{ticketname}")
    @POST
    @RolesAllowed({"Admin"})
    public Response addTicketToProject(@Context SecurityContext sc, @PathParam("name") String name, @PathParam("ticketname") String ticketname)
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();
            Database.addTicketToProject(session, sc.getUserPrincipal().getName(), ticketname, name);
            session.getTransaction().commit();
        }

        return Response.ok(String.format("Added ticket %s to project %s.", ticketname, name)).build();
    }
    
    @Path("/projects/{name}/tickets")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response getTicketsOfProject(@Context SecurityContext sc, @PathParam("name") String name)
    {
        try (Session session = Database.openSession())
        {
            Ticket[] tickets = Database.getTicketsOfProject(session, sc.getUserPrincipal().getName(), name);
            return Response.ok(tickets).build();
        }

        catch (WebApplicationException e)
        {
            return e.getResponse();
        }
    }

    @Path("/projects/{name}")
    @OPTIONS
    @PermitAll
    public Response project()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/projects/{name}")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @RolesAllowed({"Admin"})
    public Response createProject(@Context SecurityContext sc, @PathParam("name") String name, String entryKey)
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();
            Database.putProject(session, sc.getUserPrincipal().getName(), name, entryKey);
            session.getTransaction().commit();
        }

        return Response.ok(String.format("Project %s created with %s.", name, entryKey)).build();
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

    @Path("/projects/{name}/users/{username}")
    @OPTIONS
    @PermitAll
    public Response addUserToProject()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/projects/{name}/users/{username}")
    @POST
    @RolesAllowed({"Admin"})
    public Response addUserToProject(@Context SecurityContext sc, @PathParam("name") String name, @PathParam("username") String username)
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();
            Database.addUserToProject(session, sc.getUserPrincipal().getName(), username, name);
            session.getTransaction().commit();
        }

        return Response.ok(String.format("Added user %s to project %s.", username, name)).build();
    }

    @Path("/projects/{name}/users")
    @OPTIONS
    @PermitAll
    public Response getUsersOfProject()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/projects/{name}/users")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response getUsersOfProject(@Context SecurityContext sc, @PathParam("name") String name)
    {
        try (Session session = Database.openSession())
        {
            User[] users = Database.getUsersOfProject(session, sc.getUserPrincipal().getName(), name);
            return Response.ok(users).build();
        }

        catch (WebApplicationException e)
        {
            return e.getResponse();
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
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response addUser(@Context SecurityContext sc, User newUser)
    {
        try (Session session = Database.openSession())
        {
            String loginName = newUser.getLoginName();

            if (Database.isUser(session, loginName))
            { return Response.status(Response.Status.BAD_REQUEST).build(); }

            session.beginTransaction();
            Database.putUser(session, newUser);
            session.getTransaction().commit();

            return Response.ok(String.format("Added new user %s.", newUser.getLoginName())).build();
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

    @Path("/admins")
    @OPTIONS
    @PermitAll
    public Response admins()
    {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/admins")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response addAdmin(@Context SecurityContext sc, Admin newAdmin)
    {
        try (Session session = Database.openSession())
        {
            String loginName = newAdmin.getLoginName();

            if (Database.isAdmin(session, loginName))
            { return Response.status(Response.Status.BAD_REQUEST).build(); }

            session.beginTransaction();
            Database.putAdmin(session, newAdmin);
            session.getTransaction().commit();

            return Response.ok(String.format("Added new admin %s.", newAdmin.getLoginName())).build();
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
    
    public static void main(String[] args)
    {
        try
        {
            final String ip = InetAddress.getLocalHost().getHostAddress();
            final String address = "http://" + ip + "/";
            int port = 12345;

            try
            {
                port = Integer.parseInt(System.getenv("PORT"));
            }

            catch (NumberFormatException e)
            {
            }

            final URI uri = UriBuilder.fromUri(address).port(port).build();

            ResourceConfig config = new ResourceConfig(WebService.class);
            config.register(CORSFilter.class);
            config.register(AuthenticationFilter.class);
            // config.register(DebugExceptionMapper.class);

            JdkHttpServerFactory.createHttpServer(uri, config);
        }

        catch (UnknownHostException e)
        {
            System.err.println("Failed to get server's own ip address.");
            e.printStackTrace();
        }
    }
}
