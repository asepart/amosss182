package de.fau.cs.osr.amos.asepart;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.Session;

import de.fau.cs.osr.amos.asepart.entities.*;

public class WebServiceTest
{
    @Test
    public void testWriteRead()
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            Database.putUser(session, "knowsnothing", "whitespace",
                    "Jon", "Snow", "00000000000");
            User u = Database.getUser(session, "knowsnothing");

            Database.putProject(session, "test", "1234");
            Project p = Database.getProject(session, "test");

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

            Database.putTicket(session, "Demo Ticket",
                    "This is the ticket summary",
                    "Here is the description",
                    TicketCategory.ONE_TIME_ERROR);

            session.getTransaction().commit();
        }

        // TODO read ticket from database and check contents
    }

    @Test
    public void testGetUsersOfProject() // TODO: add more test cases
    {
        try (Session session = Database.openSession())
        {
            session.beginTransaction();

            Database.putUser(session, "testaa", "password12345",
                    "TestFirstNameP1", "TestLastNameP1", "01601111111");
            Database.putUser(session, "testbb", "supergeheim",
                    "TestFirstNameP2", "TestLastNameP2", "01702222222");

            Database.putProject(session, "Test1", "foo");
            Database.putProject(session, "Test2", "bar");

            Database.addUsersToProject(session, "testaa", "Test1");
            Database.addUsersToProject(session, "testbb", "Test1");
            Database.addUsersToProject(session, "testaa", "Test2");
            Database.addUsersToProject(session, "testbb", "Test2");

            User[] expected = new User[] { Database.getUser(session, "testaa"), Database.getUser(session, "testbb")};
            User[] actual1 = Database.getUsersOfProject(session, "Test1");
            User[] actual2 = Database.getUsersOfProject(session, "Test2");

            session.getTransaction().commit();

            assertEquals(expected[0].getLoginName(), actual1[0].getLoginName());
            assertEquals(expected[0].getLoginName(), actual2[0].getLoginName());
            assertEquals(expected[1].getLoginName(), actual1[1].getLoginName());
            assertEquals(expected[1].getLoginName(), actual2[1].getLoginName());
        }
    }
}
