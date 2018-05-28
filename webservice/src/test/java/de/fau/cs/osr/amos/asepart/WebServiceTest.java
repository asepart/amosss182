package de.fau.cs.osr.amos.asepart;

import java.net.URI;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebServiceTest
{
    private final static int port = 12345;

    private WebTarget getClient()
    {
        final URI uri = UriBuilder.fromUri("http://localhost/").port(port).build();
        WebTarget client = ClientBuilder.newClient().target(uri);

        return client;
    }

    private WebTarget getUserClient()
    {
        WebTarget client = getClient();

        HttpAuthenticationFeature af = HttpAuthenticationFeature.basic("user", "user");
        WebServiceRole role = WebServiceRole.user();

        client.register(af);
        client.register(role);

        return client;
    }

    private WebTarget getAdminClient()
    {
        WebTarget client = getClient();

        HttpAuthenticationFeature af = HttpAuthenticationFeature.basic("admin", "admin");
        WebServiceRole role = WebServiceRole.admin();

        client.register(af);
        client.register(role);

        return client;
    }

    @BeforeAll
    public static void startServer()
    {
        WebService.startBackground(port);
    }

    @Test
    public void loginTest()
    {
        try (Response response = getAdminClient().path("/login").request().get())
        {
            String answer = response.readEntity(String.class);
            assertEquals("Your identification is valid: admin", answer);
        }
    }
}
