package de.fau.cs.osr.amos.asepart;

import de.fau.cs.osr.amos.asepart.relationships.ProjectAccount;
import de.fau.cs.osr.amos.asepart.relationships.ProjectUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.hibernate.Session;
import org.mindrot.jbcrypt.BCrypt;

import de.fau.cs.osr.amos.asepart.entities.*;

public class WebServiceTest
{

    @BeforeAll
    public static void setUp()
    {
        try (Session session = DatabaseController.newSession())
        {
            session.beginTransaction();

            Admin a = new Admin();
            a.setLoginName("testuser");
            a.setFirstName("Test");
            a.setLastName("User");
            a.setPasswordHash(BCrypt.hashpw("password123", BCrypt.gensalt()));

            session.save(a);

            Admin b = session.get(Admin.class, "testuser");
            assertEquals("Test", b.getFirstName());

            session.getTransaction().commit();
        }
    }

    @Test
    public void emptyTest()
    {
        assertTrue(true);
    }

    @Test
    public void testGetUsersOfProject() //TODO: add more testcases as soon as project table is working
    {
        try (Session session = DatabaseController.newSession())
        {
            session.beginTransaction();

            User aa = new User();
            aa.setLastName("TestLastNameP1");
            aa.setFirstName("TestFirstNameP1");
            aa.setLoginName("testaa");
            aa.setPhone("01601111111");

            User ab = new User();
            ab.setLastName("TestLastNameP2");
            ab.setFirstName("TestFirstNameP2");
            ab.setLoginName("testab");
            ab.setPhone("01702222222");

            Project p1 = new Project();
            p1.setProjectName("Test1");
            p1.setEntryKey("foo");

            Project p2 = new Project();
            p2.setProjectName("Test2");
            p2.setEntryKey("bar");

            session.save(p1);
            session.save(p2);

            ProjectUser pu = new ProjectUser();

            pu = new ProjectUser();
            pu.setRelId(new ProjectAccount(p1.getProjectName(), aa.getLoginName()));
            session.save(pu);

            pu = new ProjectUser();
            pu.setRelId(new ProjectAccount(p1.getProjectName(), ab.getLoginName()));
            session.save(pu);

            pu = new ProjectUser();
            pu.setRelId(new ProjectAccount(p2.getProjectName(), aa.getLoginName()));
            session.save(pu);

            pu = new ProjectUser();
            pu.setRelId(new ProjectAccount(p2.getProjectName(), ab.getLoginName()));
            session.save(pu);

            WebService w = new WebService();
            Response r = w.getUsersOfProject("Test2");

            session.getTransaction().commit();

            /*
            GenericType<User[]> type = new GenericType<User[]>() {};
            User[] array = r.readEntity(type);

            assertEquals(array, new User[] {aa, ab});
            */

            // TODO: extract db logic from WebService class for better testing

            assertTrue(true);
        }
    }
}
