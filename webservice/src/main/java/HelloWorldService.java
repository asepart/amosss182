import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

@Path("/")
public class HelloWorldService
{
    @GET
    public Response get()
    {
        return Response.ok("Hello, World!").build();
    }

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
            
            ResourceConfig config = new ResourceConfig(HelloWorldService.class);
            JdkHttpServerFactory.createHttpServer(uri, config);
        }

        catch (UnknownHostException e)
        {
            System.err.println("Failed to get server's own ip address.");
            e.printStackTrace();
        }
    }
}
