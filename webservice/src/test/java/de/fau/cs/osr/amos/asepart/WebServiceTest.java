package de.fau.cs.osr.amos.asepart;

import de.fau.cs.osr.amos.asepart.relationships.ProjectUser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.Session;

import de.fau.cs.osr.amos.asepart.entities.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.xml.crypto.Data;
import java.util.List;

public class WebServiceTest
{
    @Test
    public void testWriteRead()
    {
        try (Session session = Database.openSession())
        {
            Database.putUser(session, "knowsnothing", "whitespace",
                    "Jon", "Snow", "00000000000");
            User u = Database.getUser(session, "knowsnothing");

            Database.putProject(session, "test", "1234");
            Project p = Database.getProject(session, "test");

            assertEquals("00000000000", u.getPhone());
            assertEquals("1234", p.getEntryKey());
        }
    }

    @Test
    public void testGetUsersOfProject() // TODO: add more test cases
    {
        try (Session session = Database.openSession())
        {
            Database.putUser(session, "testaa", "password12345",
                    "TestFirstNameP1", "TestLastNameP1", "01601111111");
            Database.putUser(session, "testbb", "supergeheim",
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
