package de.fau.cs.osr.amos.asepart;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import de.fau.cs.osr.amos.asepart.entities.*;

public class WebServiceTest
{
    @Test
    public void testGetUsersOfProject() // TODO: add more test cases
    {
        try (DatabaseClient client = new DatabaseClient())
        {
            client.putUser("testaa", "password12345",
                    "TestFirstNameP1", "TestLastNameP1", "01601111111");
            client.putUser("testab", "supergeheim",
                    "TestFirstNameP2", "TestLastNameP2", "01702222222");

            client.putProject("Test1", "foo");
            client.putProject("Test2", "bar");

            client.addUsersToProject("testaa", "Test1");
            client.addUsersToProject("testbb", "Test1");
            client.addUsersToProject("testbb", "Test2");
            client.addUsersToProject("testaa", "Test2");

            User[] expected = new User[] { client.getUser("testaa"), client.getUser("testbb")};
            User[] actual1 = client.getUsersOfProject("Test1");
            User[] actual2 = client.getUsersOfProject("Test2");

            assertArrayEquals(expected, actual1);
            assertArrayEquals(expected, actual2);
        }
    }
}
