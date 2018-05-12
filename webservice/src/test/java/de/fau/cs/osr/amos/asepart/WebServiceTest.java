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
            
            Integer id = Database.putTicket(session, "Demo Ticket",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR);
            Ticket t = Database.getTicket(session, id);
            Database.addTicketToProject(session, "testadmin", id, "test");
            
            Integer idd = Database.putTicket(session, t);
            Database.addTicketToProject(session, "testadmin", idd, "test");
            
            session.getTransaction().commit();
            
            assertEquals(1, 0);
        }
    		
    		catch (WebApplicationException e) 
    		{
    			assertEquals(1, 1);
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
            
            Integer id = Database.putTicket(session, "Demo Ticket",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR);
            Database.addTicketToProject(session, "testadmin", id, "test1");
            
            Integer id2 = Database.putTicket(session, "Demo Ticket",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR);
            Database.addTicketToProject(session, "testadmin", id2, "test1");
            
            Integer id3 = Database.putTicket(session, "Demo Ticket 3",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR);
            Database.addTicketToProject(session, "testadmin", id3, "test1");
            
            Integer id4 = Database.putTicket(session, "Demo Ticket 4",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR);
            Database.addTicketToProject(session, "testadmin", id4, "test2");
            
            Ticket t = Database.getTicket(session, id);
            Ticket[] ts = Database.getTicketsOfProject(session, "testadmin", "test1");
            Ticket[] ts2 = Database.getTicketsOfProject(session, "testadmin", "test2");
            
            session.getTransaction().commit();
            
            assertEquals(t.getTicketSummary(), "This is the ticket summary");
            assertEquals(ts[0].getTicketName(), "Demo Ticket");
            assertEquals(ts[1].getTicketName(), "Demo Ticket");
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

            Database.putProject(session, "testadmin","Test1", "foo");
            Database.putProject(session, "testadmin","Test2", "bar");

            Database.addUserToProject(session, "testadmin","testaa", "Test1");
            Database.addUserToProject(session, "testadmin","testbb", "Test1");
            Database.addUserToProject(session, "testadmin","testaa", "Test2");
            Database.addUserToProject(session, "testadmin","testbb", "Test2");

            User[] expected = new User[] { Database.getUser(session, "testaa"), Database.getUser(session, "testbb")};
            User[] actual1 = Database.getUsersOfProject(session, "testadmin","Test1");
            User[] actual2 = Database.getUsersOfProject(session, "testadmin","Test2");

            session.getTransaction().commit();

            assertEquals(expected[0].getLoginName(), actual1[0].getLoginName());
            assertEquals(expected[0].getLoginName(), actual2[0].getLoginName());
            assertEquals(expected[1].getLoginName(), actual1[1].getLoginName());
            assertEquals(expected[1].getLoginName(), actual2[1].getLoginName());
        }
    }
}
