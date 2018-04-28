package de.fau.cs.osr.amos.asepart;

import de.fau.cs.osr.amos.asepart.entities.KeyValueEntry;
import org.hibernate.Session;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebServiceTest
{
    @Test
    public void checkCounter()
    {
        WebService service = new WebService();
        service.get();
        service.get();
        service.get();

        Session session = DatabaseController.newSession();
        session.beginTransaction();
        KeyValueEntry demo = session.get(KeyValueEntry.class, "demo");
        session.getTransaction().commit();
        session.close();

        assertNotNull(demo);
        assertEquals(3, (int) demo.getValue());
    }
}
