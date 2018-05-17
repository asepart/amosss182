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

            Database.putProject(session, "testkey", "testproject", "testadmin");
            Project p = Database.getProject(session, "testkey");

            session.getTransaction().commit();

            assertEquals("00000000000", u.getPhone());
            assertEquals("testkey", p.getEntryKey());
        }
    }

    @Test
    public void testCreateTicket()
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            Database.putProject(session, "myTestKey", "myTestName", "testadmin");

            Integer id = Database.putTicket(session, "testadmin", "myTestKey", "Demo Ticket",
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

            Database.putProject(session, "key1", "name1", "testadmin");
            Database.putProject(session, "key2", "name2", "testadmin");

            Integer id1 = Database.putTicket(session, "testadmin", "key1", "Demo Ticket 1",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR,
                    13);

            Integer id2 = Database.putTicket(session, "testadmin", "key1", "Demo Ticket 2",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR,
                    13);

            Integer id3 = Database.putTicket(session, "testadmin", "key1", "Demo Ticket 3",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR,
                    13);

            Integer id4 = Database.putTicket(session, "testadmin", "key2", "Demo Ticket 4",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR,
                    13);

            Ticket t = Database.getTicket(session, id1);
            Ticket[] ts1 = Database.getTicketsOfProject(session, "testadmin", "Admin", "key1");
            Ticket[] ts2 = Database.getTicketsOfProject(session, "testadmin", "Admin", "key2");

            session.getTransaction().commit();

            assertEquals(t.getTicketSummary(), "This is the ticket summary");
            assertEquals(ts1[0].getTicketName(), "Demo Ticket 1");
            assertEquals(ts1[1].getTicketName(), "Demo Ticket 2");
            assertEquals(ts1[2].getTicketName(), "Demo Ticket 3");
            assertEquals(ts2[0].getTicketName(), "Demo Ticket 4");
            assertNotEquals(ts1[0], ts1[1]);
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

            Database.putProject(session, "join1", "Test1", "testadmin");
            Database.putProject(session, "join2", "Test2", "testadmin");

            Database.joinProject(session, "testaa", "join1");
            Database.joinProject(session, "testaa", "join2");
            Database.joinProject(session, "testbb", "join1");
            Database.joinProject(session, "testbb", "join2");

            User[] expected = new User[]{Database.getUser(session, "testaa"), Database.getUser(session, "testbb")};
            User[] actual1 = Database.getUsersOfProject(session, "testadmin", "join1");
            User[] actual2 = Database.getUsersOfProject(session, "testadmin", "join2");

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

            Database.putProject(session, "chatwithme", "chattest", "testadmin");

            Integer ticketId = Database.putTicket(session, "testadmin", "chatwithme", "Chat Ticket",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR,
                    2);

            Database.putUser(session, "chatuser", "lolrofl",
                    "Chat", "User", "3242342323");

            Database.joinProject(session, "chatuser", "chatwithme");

            String message = "Hello, World!";

            Database.putMessage(session, ticketId, message, "chatuser", "User");
            Message[] messages = Database.listMessages(session, ticketId, "chatuser", "User");

            session.getTransaction().commit();

            assertEquals(message, messages[0].getContent());
        }
    }
}
