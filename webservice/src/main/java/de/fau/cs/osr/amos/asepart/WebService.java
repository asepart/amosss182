package de.fau.cs.osr.amos.asepart;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.xml.crypto.Data;

import org.hibernate.Session;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import de.fau.cs.osr.amos.asepart.filters.AuthenticationFilter;
import de.fau.cs.osr.amos.asepart.filters.CORSFilter;
import de.fau.cs.osr.amos.asepart.entities.*;

@Path("/")
public class WebService
{
    @OPTIONS
    @PermitAll
    public Response options()
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

    @Path("/projects/{name}")
    @PUT
    @RolesAllowed({"Admin"})
    public Response createProject(@PathParam("name") String name, String entryKey)
    {
        try (Session session = Database.openSession())
        {
            Database.putProject(session, name, entryKey);
        }

        return Response.ok(String.format("Project %s created with %s.", name, entryKey)).build();
    }

    @Path("/projects")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response listProjects()
    {
        try (Session session = Database.openSession())
        {
            return Response.ok(Database.listProjects(session)).build();
        }
    }

    @Path("/projects/{name}/users/{username}")
    @PUT
    @RolesAllowed({"Admin"})
    public Response addUserToProject(@PathParam("name") String name, @PathParam("username") String username)
    {
        try (Session session = Database.openSession())
        {
            Database.addUsersToProject(session, username, name);
        }

        return Response.ok(String.format("Added user %s to project %s.", username, name)).build();
    }

    @Path("/projects/{name}/users")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response getUsersOfProject(@PathParam("name") String name)
    {
        try (Session session = Database.openSession())
        {
            User[] users = Database.getUsersOfProject(session, name);
            return Response.ok(users).build();
        }
    }

    @Path("/users")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response addUser(User newUser)
    {
        try (Session session = Database.openSession())
        {
            String loginName = newUser.getLoginName();

            if (Database.isUser(session, loginName))
                return Response.status(Response.Status.BAD_REQUEST).build();

            Database.putUser(session, newUser);
            return Response.ok(String.format("Added new user %s.", newUser.getLoginName())).build();
        }
    }

    @Path("/admins")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response addAdmin(Admin newAdmin)
    {
        try (Session session = Database.openSession())
        {
            String loginName = newAdmin.getLoginName();

            if (Database.isAdmin(session, loginName))
                return Response.status(Response.Status.BAD_REQUEST).build();

            Database.putAdmin(session, newAdmin);
            return Response.ok(String.format("Added new admin %s.", newAdmin.getLoginName())).build();
        }
    }

    public static void main(String[] args)
    {
        try
        {
            final String ip = InetAddress.getLocalHost().getHostAddress();
            final String address = "http://" + ip + "/";
            final URI uri = UriBuilder.fromUri(address).port(12345).build();

            ResourceConfig config = new ResourceConfig(WebService.class);
            config.register(CORSFilter.class);
            config.register(AuthenticationFilter.class);

            JdkHttpServerFactory.createHttpServer(uri, config);
        }

        catch (UnknownHostException e)
        {
            System.err.println("Failed to get server's own ip address.");
            e.printStackTrace();
        }
    }
}
