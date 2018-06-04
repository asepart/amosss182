package de.fau.cs.osr.amos.asepart;

import java.net.URI;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import de.fau.cs.osr.amos.asepart.relationships.Observation;
import de.fau.cs.osr.amos.asepart.relationships.ObservationOutcome;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        return getUserClient("user", "user");
    }

    private WebTarget getUserClient(String username, String password)
    {
        WebTarget client = getClient();

        HttpAuthenticationFeature af = HttpAuthenticationFeature.basic(username, password);
        WebServiceRole role = WebServiceRole.user();

        client.register(af);
        client.register(role);

        return client;
    }

    private WebTarget getAdminClient()
    {
        return getAdminClient("admin", "admin");
    }

    private WebTarget getAdminClient(String adminname, String password)
    {
        WebTarget client = getClient();

        HttpAuthenticationFeature af = HttpAuthenticationFeature.basic(adminname, password);
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
        {
            Response response = getAdminClient().path("/login").request().options();
            response.close();
        }

        try (Response response = getAdminClient().path("/login").request().get())
        {
            String answer = response.readEntity(String.class);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("Your identification is valid: admin", answer);
        }
    }

    @Test
    public void testUnauthorized()
    {
        try (Response response = getClient().path("/login").request().get())
        {
            String answer = response.readEntity(String.class);

            assertEquals(Response.Status.UNAUTHORIZED, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    public void testMissingRole()
    {
        WebTarget client = getClient();
        HttpAuthenticationFeature af = HttpAuthenticationFeature.basic("admin", "admin");

        client.register(af);

        try (Response response = client.path("/login").request().get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    public void testInvalidRole()
    {
        WebTarget client = getClient();
        HttpAuthenticationFeature af = HttpAuthenticationFeature.basic("admin", "admin");

        client.register(af);

        try (Response response = client.path("/login").request().header("X-ASEPART-Role", "God").get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    public void testWrongPassword()
    {
        try (Response response = getAdminClient("invalid", "wrong").path("/login").request().get())
        {
            String answer = response.readEntity(String.class);

            assertEquals(Response.Status.UNAUTHORIZED, Response.Status.fromStatusCode(response.getStatus()));
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
    public void testCreateDeleteProject()
    {
        Project project = new Project();
        project.setEntryKey("junit_test");
        project.setOwner("admin");
        project.setProjectName("JUnit Test Project xxx");

        try (Response response = getAdminClient().path("/projects").request().post(Entity.json(project)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        project.setProjectName("JUnit Test Project");

        try (Response response = getAdminClient().path("/projects").request().post(Entity.json(project)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/join").request().post(Entity.text("junit_test")))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/projects/junit_test/users").request().get())
        {
            GenericType<User[]> type = new GenericType<User[]>() {};
            User[] users = response.readEntity(type);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals(1, users.length);
            assertEquals("user", users[0].getLoginName());
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

        try (Response response = getUserClient().path("/projects/pizza/tickets").path(lastTicket.getId().toString()).request().get())
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

        try (Response response = getUserClient().path("/projects/pizza/tickets").path(lastTicket.getId().toString()).request().get())
        {
            Ticket ticket = response.readEntity(Ticket.class);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("Test Ticket Modified", ticket.getTicketName());
        }

        try (Response response = getUserClient().path("/projects/pizza/tickets").path(lastTicket.getId().toString()).path("accept").request().post(Entity.text("")))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/projects/pizza/tickets").path(lastTicket.getId().toString()).request().get())
        {
            Ticket ticket = response.readEntity(Ticket.class);

            assertEquals(TicketStatus.ACCEPTED, ticket.getTicketStatus());
        }

        Observation o = new Observation();
        o.setOutcome(ObservationOutcome.POSITIVE);
        o.setQuantity(4);

        try (Response response = getUserClient().path("/projects/pizza/tickets").path(lastTicket.getId().toString()).path("observations").request().post(Entity.json(o)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/projects/pizza/tickets").path(lastTicket.getId().toString()).request().get())
        {
            Ticket ticket = response.readEntity(Ticket.class);

            assertEquals(TicketStatus.PROCESSED, ticket.getTicketStatus());
        }

        try (Response response = getUserClient().path("/projects/nonsense/tickets").path(lastTicket.getId().toString()).path("accept").request().post(Entity.text("")))
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
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

        try (Response response = getAdminClient().path("/projects/pizza/users/user").request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
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

    @Test
    public void testCreateDeleteUser()
    {
        User newUser = new User();
        newUser.setFirstName("JUnit");
        newUser.setLastName("User");
        newUser.setLoginName("junit_user");
        newUser.setPassword("secure");
        newUser.setPhone("01INVALID");

        try (Response response = getAdminClient().path("/users").request().post(Entity.json(newUser)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("junit_user", "secure").path("/projects/pizza/tickets/1").request().get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("junit_user", "secure").path("/join").request().post(Entity.text("pizza")))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("junit_user", "secure").path("/projects/pizza/tickets/1").request().get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        newUser.setPhone("+4917123456789");

        try (Response response = getAdminClient().path("/users").request().post(Entity.json(newUser)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/users").request().get())
        {
            GenericType<User[]> type = new GenericType<User[]>() {};
            User[] users = response.readEntity(type);

            assertEquals(2, users.length);
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/users/junit_user").request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/users/junit_user").request().delete())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    public void testCreateAdmin()
    {
        Admin newAdmin = new Admin();
        newAdmin.setFirstName("JUnit");
        newAdmin.setLastName("Admin");
        newAdmin.setLoginName("junit_admin");
        newAdmin.setPassword("secure");

        try (Response response = getAdminClient().path("/admins").request().post(Entity.json(newAdmin)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        newAdmin.setPassword("supergeheim");

        try (Response response = getAdminClient("junit_admin", "secure").path("/admins").request().post(Entity.json(newAdmin)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient("junit_admin", "secure").path("/projects").path("pizza").request().delete())
        {
            assertEquals(Response.Status.UNAUTHORIZED, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient("junit_admin", "supergeheim").path("/projects").path("pizza").request().delete())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient("junit_admin", "supergeheim").path("/projects").path("pizza").path("tickets").request().get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/admins").request().get())
        {
            GenericType<Admin[]> type = new GenericType<Admin[]>() {};
            Admin[] admins = response.readEntity(type);

            assertEquals(2, admins.length);
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/admins/junit_admin").request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/admins/junit_admin").request().delete())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    public void testCreateUserWithExistingAdminName()
    {
        User newUser = new User();
        newUser.setFirstName("JUnit");
        newUser.setLastName("User");
        newUser.setLoginName("admin");
        newUser.setPassword("secure");
        newUser.setPhone("01INVALID");

        try (Response response = getAdminClient().path("/users").request().post(Entity.json(newUser)))
        {
            assertEquals(Response.Status.BAD_REQUEST, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    public void testCreateAdminWithExistingUserName()
    {
        Admin newAdmin = new Admin();
        newAdmin.setFirstName("JUnit");
        newAdmin.setLastName("Admin");
        newAdmin.setLoginName("user");
        newAdmin.setPassword("secure");

        try (Response response = getAdminClient().path("/admins").request().post(Entity.json(newAdmin)))
        {
            assertEquals(Response.Status.BAD_REQUEST, Response.Status.fromStatusCode(response.getStatus()));
        }
    }
}
