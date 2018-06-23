package de.fau.cs.osr.amos.asepart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebServiceTest
{
    private WebTarget getClient()
    {
        final URI uri = UriBuilder.fromUri(WebService.address).port(WebService.port).build();
        return ClientBuilder.newBuilder().register(MultiPartFeature.class).build().target(uri);
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
    static void start()
    {
        WebService.main(null);
    }

    @Test
    void testLogin()
    {
        try (Response response = getAdminClient().path("/login").request().options())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/login").request().get())
        {
            String answer = response.readEntity(String.class);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("Your identification is valid: admin", answer);
        }
    }

    @Test
    void testUnauthorized()
    {
        try (Response response = getClient().path("/login").request().get())
        {
            String answer = response.readEntity(String.class);

            assertEquals(Response.Status.UNAUTHORIZED, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("Your identification is invalid.", answer);
        }
    }

    @Test
    void testMissingRole()
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
    void testInvalidRole()
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
    void testWrongRole()
    {
        try (Response response = getUserClient().path("/users").request().get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }
    }


    @Test
    void testWrongPassword()
    {
        try (Response response = getAdminClient("invalid", "wrong").path("/login").request().get())
        {
            assertEquals(Response.Status.UNAUTHORIZED, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testUserAsAdmin()
    {
        try (Response response = getAdminClient("user", "user").path("/login").request().get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testAdminAsUser()
    {
        try (Response response = getUserClient("admin", "admin").path("/login").request().get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testCreateDeleteUser()
    {
        Map<String, String> newUser = new HashMap<>(3);
        newUser.put("loginName", "junit_user");
        newUser.put("firstName", "JUnit");
        newUser.put("lastName", "User");
        newUser.put("phoneNumber", "01INVALID");

        try (Response response = getAdminClient().path("/users").request().post(Entity.json(newUser)))
        {
            assertEquals(Response.Status.BAD_REQUEST, Response.Status.fromStatusCode(response.getStatus()));
        }

        newUser.put("password", "foobar");

        try (Response response = getAdminClient().path("/users").request().post(Entity.json(newUser)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        newUser.put("password", "secure");

        try (Response response = getAdminClient().path("/users").request().post(Entity.json(newUser)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        newUser.remove("password");
        newUser.put("phoneNumber", "+4900000");

        try (Response response = getAdminClient().path("/users").request().post(Entity.json(newUser)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/users/junit_user").request().get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

            GenericType<Map<String, String>> type = new GenericType<Map<String, String>>() {};
            Map<String, String> currentUser = response.readEntity(type);

            assertEquals(newUser.get("loginName"), currentUser.get("loginName"));
        }

        try (Response response = getAdminClient().path("/users/invalid").request().get())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("junit_user", "secure").path("/projects/pizza/tickets").request().get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("junit_user", "secure").path("/tickets").path("1").request().get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("junit_user", "secure").path("/join").request().post(Entity.text("pizza")))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("junit_user", "secure").path("/projects/pizza/tickets").request().get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        newUser.put("phoneNumber","+4917123456789");

        try (Response response = getAdminClient().path("/users").request().post(Entity.json(newUser)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/users").request().get())
        {
            GenericType<List<Map<String, String>>> type = new GenericType<List<Map<String, String>>>() {};
            List<Map<String, String>> users = response.readEntity(type);

            assertEquals(3, users.size());
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
    void testCreateAdmin()
    {
        Map<String, String> newAdmin = new HashMap<>(3);
        newAdmin.put("loginName", "junit_admin");
        newAdmin.put("firstName", "JUnit");
        newAdmin.put("lastName", "Admin");

        try (Response response = getAdminClient().path("/admins").request().post(Entity.json(newAdmin)))
        {
            assertEquals(Response.Status.BAD_REQUEST, Response.Status.fromStatusCode(response.getStatus()));
        }

        newAdmin.put("password", "secure");

        try (Response response = getAdminClient().path("/admins").request().post(Entity.json(newAdmin)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/admins/junit_admin").request().get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

            GenericType<Map<String, String>> type = new GenericType<Map<String, String>>() {};
            Map<String, String> currentAdmin = response.readEntity(type);

            assertEquals(newAdmin.get("loginName"), currentAdmin.get("loginName"));
        }

        try (Response response = getAdminClient().path("/admins/invalid").request().get())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }

        newAdmin.put("password","supergeheim");

        try (Response response = getAdminClient("junit_admin", "secure").path("/admins").request().post(Entity.json(newAdmin)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        newAdmin.put("password","hostile_attack");

        try (Response response = getAdminClient().path("/admins").request().post(Entity.json(newAdmin)))
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient("junit_admin", "secure").path("/projects").path("pizza").request().delete())
        {
            assertEquals(Response.Status.UNAUTHORIZED, Response.Status.fromStatusCode(response.getStatus()));
        }

        Map<String, String> pizza = new HashMap<>(3);
        pizza.put("entryKey", "pizza");
        pizza.put("owner", "junit_admin");
        pizza.put("name", "Pizza Project");

        try (Response response = getAdminClient("junit_admin", "supergeheim").path("/projects").request().post(Entity.json(pizza)))
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
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
            GenericType<List<Map<String, String>>> type = new GenericType<List<Map<String, String>>>() {};
            List<Map<String, String>> admins = response.readEntity(type);

            assertEquals(3, admins.size());
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
    void testCreateUserWithExistingAdminName()
    {
        Map<String, String> newUser = new HashMap<>(3);
        newUser.put("loginName", "admin");
        newUser.put("password", "secure");
        newUser.put("firstName", "JUnit");
        newUser.put("lastName", "User");
        newUser.put("phoneNumber", "01INVALID");

        try (Response response = getAdminClient().path("/users").request().post(Entity.json(newUser)))
        {
            assertEquals(Response.Status.BAD_REQUEST, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testCreateAdminWithExistingUserName()
    {
        Map<String, String> newAdmin = new HashMap<>(3);
        newAdmin.put("loginName", "user");
        newAdmin.put("password", "secure");
        newAdmin.put("firstName", "JUnit");
        newAdmin.put("lastName", "Admin");

        try (Response response = getAdminClient().path("/admins").request().post(Entity.json(newAdmin)))
        {
            assertEquals(Response.Status.BAD_REQUEST, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testListProjects()
    {
        try (Response response = getAdminClient().path("/projects").request().get())
        {
            GenericType<List<Map<String, String>>> type = new GenericType<List<Map<String, String>>>() {};
            List<Map<String, String>> projects = response.readEntity(type);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("pizza", projects.get(0).get("entryKey"));
        }
    }

    @Test
    void testCreateDeleteProject()
    {
        Map<String, String> project = new HashMap<>(3);
        project.put("entryKey", "junit_test");
        project.put("owner", "admin");
        project.put("name", "JUnit Test Project xxx");

        try (Response response = getAdminClient().path("/projects").request().post(Entity.json(project)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/projects/junit_test").request().get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        project.put("name", "JUnit Test Project");

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
            GenericType<List<Map<String, String>>> type = new GenericType<List<Map<String, String>>>() {};
            List<Map<String, String>> users = response.readEntity(type);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals(1, users.size());
            assertEquals("user", users.get(0).get("loginName"));
        }

        try (Response response = getAdminClient("nobodyadmin", "nobodyadmin").path("/projects/junit_test/users").request().get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/projects/doesnotexist/users").request().get())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/projects").path("junit_test").request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testGetNonExistingProject()
    {
        try (Response response = getAdminClient().path("/projects").path("idonotexist").request().get())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/projects").path("idonotexist").path("tickets").request().get())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testDeleteNonExistingProject()
    {
        try (Response response = getAdminClient().path("/projects").path("idonotexist").request().delete())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testCreateTicket()
    {
        Map<String, String> ticket = new HashMap<>(6);
        ticket.put("projectKey", "pizza");
        ticket.put("name", "Test Ticket");
        ticket.put("summary", "Test Ticket Summary");
        ticket.put("description", "Description of Test Ticket");
        ticket.put("category", "trace");
        ticket.put("requiredObservations", "4");

        try (Response response = getAdminClient().path("/tickets").request().post(Entity.json(ticket)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient("nobodyadmin", "nobodyadmin").path("/tickets").request().post(Entity.json(ticket)))
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testCreateTicketWithInvalidProjectKey()
    {
        Map<String, String> ticket = new HashMap<>(6);
        ticket.put("name", "Test Ticket");
        ticket.put("summary", "Test Ticket Summary");
        ticket.put("description", "Description of Test Ticket");
        ticket.put("category", "trace");
        ticket.put("requiredObservations", "500");

        try (Response response = getAdminClient().path("/tickets").request().post(Entity.json(ticket)))
        {
            assertEquals(Response.Status.BAD_REQUEST, Response.Status.fromStatusCode(response.getStatus()));
        }

        ticket.put("projectKey", "invalid-key");

        try (Response response = getAdminClient().path("/tickets").request().post(Entity.json(ticket)))
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testModifyDeleteTicket()
    {
        Map<String, String> lastTicket;
        int lastTicketId;
        String lastTicketName;

        try (Response response = getAdminClient().path("/projects/pizza/tickets").request().get())
        {
            GenericType<List<Map<String, String>>> type = new GenericType<List<Map<String, String>>>() {};
            List<Map<String, String>> tickets = response.readEntity(type);

            lastTicket = tickets.get(tickets.size() - 1);
            lastTicketId = Integer.parseInt(lastTicket.get("id"));
            lastTicketName = lastTicket.get("name");

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/tickets").path(String.valueOf(lastTicketId)).request().get())
        {
            GenericType<Map<String, String>> type = new GenericType<Map<String, String>>() {};
            Map<String, String> ticket = response.readEntity(type);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals(lastTicketName, ticket.get("name"));
        }

        lastTicket.put("name","Test Ticket Modified");

        try (Response response = getAdminClient().path("/tickets").request().post(Entity.json(lastTicket)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/tickets").path(String.valueOf(lastTicketId)).request().get())
        {
            GenericType<Map<String, String>> type = new GenericType<Map<String, String>>() {};
            Map<String, String> ticket = response.readEntity(type);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("Test Ticket Modified", ticket.get("name"));
        }

        try (Response response = getUserClient("nobodyuser", "nobodyuser").path("/tickets").path(String.valueOf(lastTicketId)).path("accept").request().post(Entity.text("")))
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/tickets").path(String.valueOf(99999999)).path("accept").request().post(Entity.text("")))
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/tickets").path(String.valueOf(lastTicketId)).path("accept").request().post(Entity.text("")))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient("nobodyadmin", "nobodyadmin").path("/tickets").path(String.valueOf(lastTicketId)).request().get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/tickets").path(String.valueOf(lastTicketId)).request().get())
        {
            GenericType<Map<String, String>> type = new GenericType<Map<String, String>>() {};
            Map<String, String> ticket = response.readEntity(type);

            assertEquals("accepted", ticket.get("status"));
        }

        Map<String, String> observation = new HashMap<>(2);
        observation.put("outcome", "positive");
        observation.put("quantity", "2");

        try (Response response = getUserClient("nobodyuser", "nobodyuser").path("/tickets").path(String.valueOf(lastTicketId)).path("observations").request().post(Entity.json(observation)))
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/tickets").path(String.valueOf(lastTicketId)).path("observations").request().post(Entity.json(observation)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("nobodyuser", "nobodyuser").path("/tickets").path(String.valueOf(lastTicketId)).path("observations").request().get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient("nobodyadmin", "nobodyadmin").path("/tickets").path(String.valueOf(lastTicketId)).path("observations").request().get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/tickets").path(String.valueOf(lastTicketId)).path("observations").request().get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

            GenericType<List<Map<String, String>>> type = new GenericType<List<Map<String, String>>>() {};
            List<Map<String, String>> observations = response.readEntity(type);

            assertEquals(observation.get("outcome"), observations.get(0).get("outcome"));
            assertEquals(observation.get("quantity"), observations.get(0).get("quantity"));
        }

        try (Response response = getUserClient().path("/tickets").path(String.valueOf(lastTicketId)).request().get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

            GenericType<Map<String, String>> type = new GenericType<Map<String, String>>() {};
            Map<String, String> ticket = response.readEntity(type);

            assertEquals("processed", ticket.get("status"));
        }

        try (Response response = getUserClient().path("/projects").path("pizza").path("tickets").request().get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        observation.put("quantity", "1");

        try (Response response = getUserClient().path("/tickets").path(String.valueOf(lastTicketId)).path("observations").request().post(Entity.json(observation)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/projects").path("pizza").path("tickets").request().get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/tickets").path(String.valueOf(lastTicketId)).path("observations").request().post(Entity.json(observation)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/tickets").path(String.valueOf(lastTicketId)).request().get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

            GenericType<Map<String, String>> type = new GenericType<Map<String, String>>() {};
            Map<String, String> ticket = response.readEntity(type);

            assertEquals("finished", ticket.get("status"));
        }

        try (Response response = getUserClient().path("/projects").path("pizza").path("tickets").request().get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient("nobodyadmin", "nobodyadmin").path("/tickets").path(String.valueOf(lastTicketId)).request().delete())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/tickets").path(String.valueOf(lastTicketId)).request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/tickets").path(String.valueOf(lastTicketId)).request().delete())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/tickets").path(String.valueOf(lastTicketId)).request().get())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testModifyTicketWithInvalidId()
    {
        Map<String, String> ticket = new HashMap<>(7);
        ticket.put("projectKey", "pizza");
        ticket.put("name", "Test Ticket");
        ticket.put("summary", "Test Ticket Summary");
        ticket.put("description", "Description of Test Ticket");
        ticket.put("category", "trace");
        ticket.put("requiredObservations", "500");
        ticket.put("id", "500000");

        try (Response response = getAdminClient().path("/tickets").request().post(Entity.json(ticket)))
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testSubmitObservationWithInvalidId()
    {
        Map<String, String> observation = new HashMap<>(2);
        observation.put("outcome", "negative");
        observation.put("quantity", "2");

        try (Response response = getUserClient().path("/tickets").path(String.valueOf(1000000)).path("observations").request().post(Entity.json(observation)))
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testListObservationsWithInvalidId()
    {
        try (Response response = getUserClient().path("/tickets").path(String.valueOf(1000000)).path("observations").request().get())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }
    }


    @Test
    void testJoinProject()
    {
        try (Response response = getUserClient().path("/join").queryParam("key", "pizza").request().get())
        {
            String projectName = response.readEntity(String.class);
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("Pizza Project", projectName);
        }

        try (Response response = getUserClient().path("/join").request().post(Entity.text("pizza")))
        {
            assertEquals(Response.Status.BAD_REQUEST, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/join").request().post(Entity.text("doesnotexist")))
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient("nobodyadmin", "nobodyadmin").path("/projects/pizza/users/user").request().delete())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/projects/pizza/users/user").request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/projects/pizza/users/user").request().delete())
        {
            assertEquals(Response.Status.BAD_REQUEST, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/projects/pizza/users/invaliduser").request().delete())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/join").request().post(Entity.text("pizza")))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient().path("/join").request().post(Entity.text("pizza")))
        {
            assertEquals(Response.Status.BAD_REQUEST, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testStatistics()
    {
        Map<String, String> newUser = new HashMap<>(3);
        newUser.put("loginName", "stat_user1");
        newUser.put("firstName", "Stat");
        newUser.put("lastName", "User1");
        newUser.put("phoneNumber", "01INVALID");
        newUser.put("password", "test");

        try (Response response = getAdminClient().path("/users").request().post(Entity.json(newUser)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        newUser.put("loginName", "stat_user2");
        newUser.put("firstName", "Stat");
        newUser.put("lastName", "User2");

        try (Response response = getAdminClient().path("/users").request().post(Entity.json(newUser)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        Map<String, String> project = new HashMap<>(3);
        project.put("entryKey", "stat_project");
        project.put("owner", "admin");
        project.put("name", "Statistics Test Project");

        try (Response response = getAdminClient().path("/projects").request().post(Entity.json(project)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        Map<String, String> ticket = new HashMap<>(6);
        ticket.put("projectKey", "stat_project");
        ticket.put("name", "Test Ticket");
        ticket.put("summary", "Test Ticket Summary");
        ticket.put("description", "Description of Test Ticket");
        ticket.put("category", "one-time-error");
        ticket.put("requiredObservations", "20");

        try (Response response = getAdminClient().path("/tickets").request().post(Entity.json(ticket)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("stat_user1", "test").path("/join").request().post(Entity.text("stat_project")))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("stat_user2", "test").path("/join").request().post(Entity.text("stat_project")))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        int ticketId;

        try (Response response = getAdminClient().path("/projects/stat_project/tickets").request().get())
        {
            GenericType<List<Map<String, String>>> type = new GenericType<List<Map<String, String>>>() {};
            List<Map<String, String>> tickets = response.readEntity(type);
            ticketId = Integer.parseInt(tickets.get(tickets.size() - 1).get("id"));

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("stat_user1", "test").path("/tickets").path(String.valueOf(ticketId)).path("accept").request().post(Entity.text("")))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        Map<String, String> observation = new HashMap<>(2);
        observation.put("outcome", "negative");
        observation.put("quantity", "2");

        try (Response response = getUserClient("stat_user1", "test").path("/tickets").path(String.valueOf(ticketId)).path("observations").request().post(Entity.json(observation)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("stat_user1", "test").path("/tickets").path(String.valueOf(ticketId)).request().get())
        {
            GenericType<Map<String, String>> type = new GenericType<Map<String, String>>() {};
            ticket = response.readEntity(type);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("1", ticket.get("U"));
            assertEquals("0", ticket.get("UP"));
            assertEquals("0", ticket.get("OP"));
            assertEquals("2", ticket.get("ON"));
        }

        observation.put("outcome", "positive");

        try (Response response = getUserClient("stat_user1", "test").path("/tickets").path(String.valueOf(ticketId)).path("observations").request().post(Entity.json(observation)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("stat_user1", "test").path("/tickets").path(String.valueOf(ticketId)).request().get())
        {
            GenericType<Map<String, String>> type = new GenericType<Map<String, String>>() {};
            ticket = response.readEntity(type);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("1", ticket.get("U"));
            assertEquals("1", ticket.get("UP"));
            assertEquals("2", ticket.get("OP"));
            assertEquals("2", ticket.get("ON"));
        }

        try (Response response = getUserClient("stat_user2", "test").path("/tickets").path(String.valueOf(ticketId)).path("accept").request().post(Entity.text("")))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("stat_user1", "test").path("/tickets").path(String.valueOf(ticketId)).request().get())
        {
            GenericType<Map<String, String>> type = new GenericType<Map<String, String>>() {};
            ticket = response.readEntity(type);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("2", ticket.get("U"));
            assertEquals("1", ticket.get("UP"));
            assertEquals("2", ticket.get("OP"));
            assertEquals("2", ticket.get("ON"));
        }

        try (Response response = getUserClient("stat_user2", "test").path("/tickets").path(String.valueOf(ticketId)).path("observations").request().post(Entity.json(observation)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("stat_user1", "test").path("/tickets").path(String.valueOf(ticketId)).request().get())
        {
            GenericType<Map<String, String>> type = new GenericType<Map<String, String>>() {};
            ticket = response.readEntity(type);

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
            assertEquals("2", ticket.get("U"));
            assertEquals("2", ticket.get("UP"));
            assertEquals("4", ticket.get("OP"));
            assertEquals("2", ticket.get("ON"));
        }

        try (Response response = getAdminClient().path("/projects").path("stat_project").request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/users/stat_user1").request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/users/stat_user2").request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

    }

    @Test
    void testMessages() throws Exception
    {
        Map<String, String> project = new HashMap<>(3);
        project.put("entryKey", "junit_test");
        project.put("owner", "admin");
        project.put("name", "JUnit Test Project");

        try (Response response = getAdminClient().path("/projects").request().post(Entity.json(project)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        Map<String, String> ticket = new HashMap<>(6);
        ticket.put("projectKey", "junit_test");
        ticket.put("name", "Test Ticket");
        ticket.put("summary", "Test Ticket Summary");
        ticket.put("description", "Description of Test Ticket");
        ticket.put("category", "trace");
        ticket.put("requiredObservations", "42");

        try (Response response = getAdminClient().path("/tickets").request().post(Entity.json(ticket)))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        int ticketId;

        try (Response response = getAdminClient().path("/projects/junit_test/tickets").request().get())
        {
            GenericType<List<Map<String, String>>> type = new GenericType<List<Map<String, String>>>() {};
            List<Map<String, String>> tickets = response.readEntity(type);
            ticketId = Integer.parseInt(tickets.get(tickets.size() - 1).get("id"));

            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/messages").path(String.valueOf(ticketId)).request().post(Entity.text("Hello, World!")))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient("nobodyadmin", "nobodyadmin").path("/messages").path(String.valueOf(ticketId)).request().post(Entity.text("Hello, World!")))
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("nobodyuser", "nobodyuser").path("/messages").path(String.valueOf(ticketId)).request().post(Entity.text("Hello, World!")))
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/messages").path(String.valueOf(10000000)).request().post(Entity.text("Hello, World!")))
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/messages").path(String.valueOf(ticketId)).request().get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

            GenericType<List<Map<String, String>>> type = new GenericType<List<Map<String, String>>>() {};
            List<Map<String, String>> messages = response.readEntity(type);

            assertEquals(1, messages.size());
            assertEquals("Hello, World!", messages.get(0).get("content"));
            assertEquals("admin", messages.get(0).get("sender"));
        }

        try (Response response = getAdminClient("nobodyadmin", "nobodyadmin").path("/messages").path(String.valueOf(ticketId)).request().get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getUserClient("nobodyuser", "nobodyuser").path("/messages").path(String.valueOf(ticketId)).request().get())
        {
            assertEquals(Response.Status.FORBIDDEN, Response.Status.fromStatusCode(response.getStatus()));
        }

        /*
        Future<Response> futureResponse = getAdminClient().path("/listen").path(String.valueOf(ticketId)).request().async().get();

        try (Response response = getAdminClient().path("/messages").path(String.valueOf(ticketId)).request().post(Entity.text("Hello, other thread!")))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = futureResponse.get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }
        */

        try (Response response = getAdminClient().path("/projects").path("junit_test").request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (Response response = getAdminClient().path("/messages").path(String.valueOf(ticketId)).request().get())
        {
            assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        }
    }

    @Test
    void testFiles() throws IOException
    {
        final String fileName = "/tmp/asepart-test-file.txt";
        final Path filePath = Paths.get(fileName);
        final String fileContent = "This is a test file for JUnit.";

        try (PrintWriter writer = new PrintWriter(fileName, "UTF-8"))
        {
            writer.println(fileContent);
        }

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File(fileName));

        try (FormDataMultiPart multipart = (FormDataMultiPart)  new FormDataMultiPart().bodyPart(filePart);
             Response response = getAdminClient().path("/files/1").request().post(Entity.entity(multipart, multipart.getMediaType())))
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }

        try (FormDataMultiPart multipart = (FormDataMultiPart)  new FormDataMultiPart().bodyPart(filePart);
             Response response = getAdminClient().path("/files/1").request().post(Entity.entity(multipart, multipart.getMediaType())))
        {
            assertEquals(Response.Status.CONFLICT, Response.Status.fromStatusCode(response.getStatus()));
        }

        Files.delete(filePath);

        try (Response response = getAdminClient().path("/files/1").path("asepart-test-file.txt").request().get())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

            InputStream stream = response.readEntity(InputStream.class);
            Files.copy(stream, filePath);
            List<String> contents = Files.readAllLines(filePath);

            assertEquals(fileContent, contents.get(0));
        }

        Files.delete(filePath);

        try (Response response = getAdminClient().path("/files/1").path("asepart-test-file.txt").request().delete())
        {
            assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        }
    }
}
