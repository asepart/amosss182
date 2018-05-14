package de.fau.cs.osr.amos.asepart;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import javax.ws.rs.WebApplicationException;

import org.hibernate.Session;

import de.fau.cs.osr.amos.asepart.entities.*;

public class WebServiceTest
{
    @BeforeAll
    public static void createAccounts()
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            Database.putAdmin(session, "testadmin", "foobar", "Test", "Admin");
            Database.putUser(session, "testuser", "foobar", "Test", "User", "+4991112345");

            session.getTransaction().commit();
        }
    }

    @Test
    public void testWriteRead()
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            Database.putUser(session, "knowsnothing", "whitespace",
                    "Jon", "Snow", "00000000000");
            User u = Database.getUser(session, "knowsnothing");

            Database.putProject(session, "testadmin", "test0", "1234");
            Project p = Database.getProject(session, "testadmin", "test0");

            session.getTransaction().commit();

            assertEquals("00000000000", u.getPhone());
            assertEquals("1234", p.getEntryKey());
        }
    }

    @Test
    public void testCreateTicket()
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            Database.putProject(session, "testadmin", "test", "12345");

            Integer id = Database.putTicket(session, "testadmin", "test", "Demo Ticket",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR,
                    13);
            Ticket t = Database.getTicket(session, id);

            session.getTransaction().commit();

            assertEquals("This is the ticket summary", t.getTicketSummary());
            assertEquals("Here is the description", t.getTicketDescription());
            assertEquals(TicketCategory.ONE_TIME_ERROR, t.getTicketCategory());
            assertEquals((Integer) 13, t.getRequiredObservations());
        }
    }

    @Test
    public void testGetTicketsOfProject()
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            Database.putProject(session, "testadmin", "test1", "123456");
            Database.putProject(session, "testadmin", "test2", "1234567");

            Integer id = Database.putTicket(session, "testadmin", "test1", "Demo Ticket 1",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR,
                    13);

            Integer id2 = Database.putTicket(session, "testadmin", "test1", "Demo Ticket 2",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR,
                    13);

            Integer id3 = Database.putTicket(session, "testadmin", "test1", "Demo Ticket 3",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR,
                    13);

            Integer id4 = Database.putTicket(session, "testadmin", "test2", "Demo Ticket 4",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR,
                    13);

            Ticket t = Database.getTicket(session, id);
            Ticket[] ts = Database.getTicketsOfProject(session, "testadmin", "test1");
            Ticket[] ts2 = Database.getTicketsOfProject(session, "testadmin", "test2");

            session.getTransaction().commit();

            assertEquals(t.getTicketSummary(), "This is the ticket summary");
            assertEquals(ts[0].getTicketName(), "Demo Ticket 1");
            assertEquals(ts[1].getTicketName(), "Demo Ticket 2");
            assertEquals(ts[2].getTicketName(), "Demo Ticket 3");
            assertEquals(ts2[0].getTicketName(), "Demo Ticket 4");
            assertNotEquals(ts[0], ts[1]);
        }
    }

    @Test
    public void testGetUsersOfProject()
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            Database.putUser(session, "testaa", "password12345",
                    "TestFirstNameP1", "TestLastNameP1", "01601111111");
            Database.putUser(session, "testbb", "supergeheim",
                    "TestFirstNameP2", "TestLastNameP2", "01702222222");

            Database.putProject(session, "testadmin", "Test1", "foo");
            Database.putProject(session, "testadmin", "Test2", "bar");

            Database.addUserToProject(session, "testadmin", "testaa", "Test1");
            Database.addUserToProject(session, "testadmin", "testbb", "Test1");
            Database.addUserToProject(session, "testadmin", "testaa", "Test2");
            Database.addUserToProject(session, "testadmin", "testbb", "Test2");

            User[] expected = new User[]{Database.getUser(session, "testaa"), Database.getUser(session, "testbb")};
            User[] actual1 = Database.getUsersOfProject(session, "testadmin", "Test1");
            User[] actual2 = Database.getUsersOfProject(session, "testadmin", "Test2");

            session.getTransaction().commit();

            assertEquals(expected[0].getLoginName(), actual1[0].getLoginName());
            assertEquals(expected[0].getLoginName(), actual2[0].getLoginName());
            assertEquals(expected[1].getLoginName(), actual1[1].getLoginName());
            assertEquals(expected[1].getLoginName(), actual2[1].getLoginName());
        }
    }

    @Test
    public void testChatMessages()
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            Database.putProject(session, "testadmin", "chattest", "32423");

            Integer ticketId = Database.putTicket(session, "testadmin", "chattest", "Chat Ticket",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR,
                    2);

            Database.putUser(session, "chatuser", "lolrofl",
                    "Chat", "User", "3242342323");

            Database.addUserToProject(session, "testadmin", "chatuser", "chattest");

            String message = "Hello, World!";

            Database.putMessage(session, ticketId, message, "chatuser", "User");
            Message[] messages = Database.listMessages(session, ticketId, "chatuser", "User");

            session.getTransaction().commit();

            assertEquals(message, messages[0].getContent());
        }
    }
}
