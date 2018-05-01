package de.fau.cs.osr.amos.asepart;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
