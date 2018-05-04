package de.fau.cs.osr.amos.asepart;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.Session;

import de.fau.cs.osr.amos.asepart.entities.*;

public class WebServiceTest
{
    @Test
    public void testGetUsersOfProject() // TODO: add more test cases
    {
        try (Session session = Database.openSession())
        {
            Database.putUser(session, "testaa", "password12345",
                    "TestFirstNameP1", "TestLastNameP1", "01601111111");
            Database.putUser(session, "testab", "supergeheim",
                    "TestFirstNameP2", "TestLastNameP2", "01702222222");

            Database.putProject(session, "Test1", "foo");
            Database.putProject(session, "Test2", "bar");

            Database.addUsersToProject(session, "testaa", "Test1");
            Database.addUsersToProject(session, "testbb", "Test1");
            Database.addUsersToProject(session, "testbb", "Test2");
            Database.addUsersToProject(session, "testaa", "Test2");

            User[] expected = new User[] { Database.getUser(session, "testaa"), Database.getUser(session, "testbb")};
            User[] actual1 = Database.getUsersOfProject(session, "Test1");
            User[] actual2 = Database.getUsersOfProject(session, "Test2");

            assertArrayEquals(expected, actual1);
            assertArrayEquals(expected, actual2);
        }
    }
}
