package de.fau.cs.osr.amos.asepart;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.hibernate.Session;

import de.fau.cs.osr.amos.asepart.filters.AuthenticationFilter;
import de.fau.cs.osr.amos.asepart.filters.CORSFilter;
import de.fau.cs.osr.amos.asepart.entities.*;

@Path("/")
public class WebService
{
    /*
        TODO: Replace PermitAll with RolesAllowed
        TODO: Actually talk with database instead of using dummies
        TODO: get user name from @Context SecurityContext sc parameter for each request

        TODO: (maybe) login limit to avoid brute force attacks
    */

    @Path("/login")
    @GET
    @RolesAllowed({"Admin", "User"})
    public Response login()
    {
        /* If credentials are invalid, the method call will automatically fail.
         * This is done by the AuthenticationFilter, so if the return statement
         * below is reached the credentials have been validated already.
         */

        return Response.ok("Your identification is invalid").build();
    }

    @GET
    @PermitAll
    public Response get()
    {
        return Response.ok("Hello, World!").build();
    }

    @Path("/projects/{name}")
    @PUT
    @PermitAll
    public Response createProject(@PathParam("name") String name, String entryKey)
    {
        return Response.ok(String.format("Project %s created with %s.", name, entryKey)).build();
    }

    @Path("/projects")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
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

    @Path("/projects/{name}/users/{accountname}")
    @PUT
    @PermitAll
    public Response addUserToProject(@PathParam("name") String name, @PathParam("accountname") String accountname)
    {
        return Response.ok(String.format("Added account %s to project %s.", accountname, name)).build();
    }

    @Path("/projects/{name}/users")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getUsersOfProject(@PathParam("name") String name) //not working because project table not working TODO: add other status code responses
    {
    		try (Session session = DatabaseController.newSession())
        {
            session.beginTransaction();

//            Project p = session.get(Project.class, name);	// add this when projects table works
//            User[] users = new User[p.getUsers().size()];	// add this when projects table works
//            	users = (User[]) p.getUsers().toArray();		// add this when projects table works

            session.getTransaction().commit();
			return Response.ok(/*users*/).build();	// add this when projects table works
        }
    }

    // TODO create User and create Admin

    public static void main(String[] args)
    {
        try
        {
            final String ip = InetAddress.getLocalHost().getHostAddress();
            final String address = "http://" + ip + "/";
            int port = 12345;
			
	    try {
        	port = Integer.parseInt(System.getenv("PORT"));
    	    }
    	    catch (NumberFormatException e) {
	    }
			
            final URI uri = UriBuilder.fromUri(address).port(port).build();

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
