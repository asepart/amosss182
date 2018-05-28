package de.fau.cs.osr.amos.asepart;

import java.net.URI;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.fau.cs.osr.amos.asepart.entities.*;

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
    public void testLogin()
    {
        try (Response response = getAdminClient().path("/login").request().get())
        {
            String answer = response.readEntity(String.class);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("Your identification is valid: admin", answer);
        }
    }

    @Test
    public void testListProject()
    {
        try (Response response = getAdminClient().path("/projects").request().get())
        {
            GenericType<Project[]> type = new GenericType<Project[]>() {};
            Project[] projects = response.readEntity(type);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("pizza", projects[0].getEntryKey());
        }
    }

    @Test
    public void testCreateProject()
    {
        Project project = new Project();
        project.setEntryKey("junit_test");
        project.setOwner("admin");
        project.setProjectName("JUnit Test Project");

        try (Response response = getAdminClient().path("/projects").request().post(Entity.json(project)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/projects").path("junit_test").request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    public void testDeleteNonExistingProject()
    {
        try (Response response = getAdminClient().path("/projects").path("idonotexist").request().delete())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    public void testCreateTicket()
    {
        Ticket ticket = new Ticket();
        ticket.setProjectKey("pizza");
        ticket.setTicketName("Test Ticket");
        ticket.setTicketSummary("Test Ticket Summary");
        ticket.setTicketDescription("Description of Test Ticket");
        ticket.setTicketCategory(TicketCategory.TRACE);
        ticket.setRequiredObservations(42);

        try (Response response = getAdminClient().path("/projects/pizza/tickets").request().post(Entity.json(ticket)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    public void testModifyDeleteTicket()
    {
        Ticket lastTicket;

        try (Response response = getAdminClient().path("/projects/pizza/tickets").request().get())
        {
            GenericType<Ticket[]> type = new GenericType<Ticket[]>() {};
            Ticket[] tickets = response.readEntity(type);
            lastTicket = tickets[tickets.length - 1];

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/projects/pizza/tickets").path(lastTicket.getId().toString()).request().get())
        {
            Ticket ticket = response.readEntity(Ticket.class);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals(lastTicket.getTicketName(), ticket.getTicketName());
        }

        lastTicket.setTicketName("Test Ticket Modified");

        try (Response response = getAdminClient().path("/projects/pizza/tickets").request().post(Entity.json(lastTicket)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/projects/pizza/tickets").path(lastTicket.getId().toString()).request().get())
        {
            Ticket ticket = response.readEntity(Ticket.class);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("Test Ticket Modified", ticket.getTicketName());
        }

        try (Response response = getAdminClient().path("/projects/pizza/tickets").path(lastTicket.getId().toString()).request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/projects/pizza/tickets").path(lastTicket.getId().toString()).request().delete())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    public void testJoinProject()
    {
        try (Response response = getUserClient().path("/join").queryParam("key", "pizza").request().get())
        {
            String projectName = response.readEntity(String.class);
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("Pizza Project", projectName);
        }

        try (Response response = getUserClient().path("/join").request().post(Entity.text("pizza")))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/join").request().post(Entity.text("pizza")))
        {
            assertEquals(Response.Status.BAD_REQUEST, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/projects/pizza/users/user").request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/projects/pizza/users/user").request().delete())
        {
            assertEquals(Response.Status.BAD_REQUEST, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    public void testMessages()
    {
        Project project = new Project();
        project.setEntryKey("junit_test");
        project.setOwner("admin");
        project.setProjectName("JUnit Test Project");

        try (Response response = getAdminClient().path("/projects").request().post(Entity.json(project)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        Ticket ticket = new Ticket();
        ticket.setProjectKey("junit_test");
        ticket.setTicketName("Test Ticket");
        ticket.setTicketSummary("Test Ticket Summary");
        ticket.setTicketDescription("Description of Test Ticket");
        ticket.setTicketCategory(TicketCategory.TRACE);
        ticket.setRequiredObservations(42);

        try (Response response = getAdminClient().path("/projects/junit_test/tickets").request().post(Entity.json(ticket)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        Integer ticketId;

        try (Response response = getAdminClient().path("/projects/junit_test/tickets").request().get())
        {
            GenericType<Ticket[]> type = new GenericType<Ticket[]>() {};
            Ticket[] tickets = response.readEntity(type);
            ticketId = tickets[tickets.length - 1].getId();

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/messages").path(ticketId.toString()).request().post(Entity.text("Hello, World!")))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/messages").path(ticketId.toString()).request().get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

            GenericType<Message[]> type = new GenericType<Message[]>() {};
            Message[] messages = response.readEntity(type);

            assertEquals(1, messages.length);
            assertEquals("Hello, World!", messages[0].getContent());
            assertEquals("admin", messages[0].getSender());
        }

        try (Response response = getAdminClient().path("/projects").path("junit_test").request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/messages").path(ticketId.toString()).request().get())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

}
