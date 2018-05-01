package de.fau.cs.osr.amos.asepart;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import de.fau.cs.osr.amos.asepart.authentication.AuthenticationFilter;
import de.fau.cs.osr.amos.asepart.entities.*;

@Path("/")
public class WebService
{
	@GET @PermitAll
    public Response get()
    {
    		return Response.ok("Hello, World!").build();
    }
	
	@Path("projects/{name}/users")
    @GET @PermitAll
    public Response getUsersOfProject(@PathParam("name") String name)
    {
		//TODO
		//get projectusers from database
		
		//hello world behavior
        String result = "Peter, 01601234567" + "\n" + "Hans, 01707654321" + "\n";  	
    		return Response.ok(result).build();
    }

    public static void main(String[] args)
    {
        try
        {
            final String ip = InetAddress.getLocalHost().getHostAddress();
            final String address = "http://" + ip + "/";
            final URI uri = UriBuilder.fromUri(address).port(12345).build();

            ResourceConfig config = new ResourceConfig(WebService.class);
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
