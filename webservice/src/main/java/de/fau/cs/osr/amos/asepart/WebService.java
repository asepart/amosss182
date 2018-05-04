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

    @GET
    @PermitAll
    public Response get()
    {
        return Response.ok("Hello, World!").build();
    }

    @Path("/projects/{name}")
    @PUT
    @RolesAllowed({"Admin"})
    public Response createProject(@PathParam("name") String name, String entryKey)
    {
        return Response.ok(String.format("Project %s created with %s.", name, entryKey)).build();
    }

    @Path("/projects")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response listProjects()
    {
        Project p1 = new Project();
        p1.setProjectName("Project1");
        p1.setEntryKey("foo");

        Project p2 = new Project();
        p2.setProjectName("Project2");
        p2.setEntryKey("bar");

        Project[] proj = new Project[2];
        proj[0] = p1;
        proj[1] = p2;

        return Response.ok(proj).build();
    }

    @Path("/projects/{name}/users/{username}")
    @PUT
    @RolesAllowed({"Admin"})
    public Response addUserToProject(@PathParam("name") String name, @PathParam("username") String username)
    {
        try (DatabaseClient client = new DatabaseClient())
        {
            client.addUsersToProject(username, name);
        }

        return Response.ok(String.format("Added user %s to project %s.", username, name)).build();
    }

    @Path("/projects/{name}/users")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response getUsersOfProject(@PathParam("name") String name)
    {
        try (DatabaseClient client = new DatabaseClient())
        {
            User[] users = client.getUsersOfProject(name);
            return Response.ok(users).build();
        }
    }

    @Path("/users")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response addUser(User newUser)
    {
        try (DatabaseClient client = new DatabaseClient())
        {
            String loginName = newUser.getLoginName();

            if (client.isUser(loginName))
                return Response.status(Response.Status.BAD_REQUEST).build();

            client.putUser(newUser);
            return Response.ok(String.format("Added new user %s.", newUser.getLoginName())).build();
        }
    }

    @Path("/admins")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"Admin"})
    public Response addAdmin(Admin newAdmin)
    {
        try (DatabaseClient client = new DatabaseClient())
        {
            String loginName = newAdmin.getLoginName();

            if (client.isAdmin(loginName))
                return Response.status(Response.Status.BAD_REQUEST).build();

            client.putAdmin(newAdmin);
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
