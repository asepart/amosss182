package de.fau.cs.osr.amos.asepart;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import org.hibernate.Session;

import de.fau.cs.osr.amos.asepart.entities.KeyValueEntry;

@Path("/")
public class WebService
{
    @GET
    public Response get()
    {
        Session session = DatabaseController.newSession();
        session.beginTransaction();

        KeyValueEntry demo = session.get(KeyValueEntry.class, "demo");

        if (demo == null)
        {
            demo = new KeyValueEntry();
            demo.setKey("demo");
            demo.setValue(0);
        }

        int count = demo.getValue();
        count++;
        demo.setValue(count);

        session.save(demo);
        session.getTransaction().commit();
        session.close();

        return Response.ok("Hello, World! Counter: " + count).build();
    }

    public static void main(String[] args)
    {
        try
        {
            final String ip = InetAddress.getLocalHost().getHostAddress();
            final String address = "http://" + ip + "/";

            final URI uri = UriBuilder.fromUri(address).port(12345).build();
            ResourceConfig config = new ResourceConfig(WebService.class);
            JdkHttpServerFactory.createHttpServer(uri, config);
        }

        catch (UnknownHostException e)
        {
            System.err.println("Failed to get server's own ip address.");
            e.printStackTrace();
        }
    }
}
