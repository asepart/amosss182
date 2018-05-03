package de.fau.cs.osr.amos.asepart;

import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import de.fau.cs.osr.amos.asepart.entities.Account;
import de.fau.cs.osr.amos.asepart.entities.Admin;
import de.fau.cs.osr.amos.asepart.entities.Project;
import de.fau.cs.osr.amos.asepart.entities.User;
import de.fau.cs.osr.amos.asepart.relationships.ProjectAccount;
import de.fau.cs.osr.amos.asepart.relationships.ProjectAdmin;
import de.fau.cs.osr.amos.asepart.relationships.ProjectUser;

public class DatabaseController
{
    private static final SessionFactory factory;

    static
    {
        try
        {
            Configuration configuration = new Configuration().configure();

            // Entities
            configuration.addAnnotatedClass(Account.class);
            configuration.addAnnotatedClass(Admin.class);
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Project.class);

            // Relationships
            configuration.addAnnotatedClass(ProjectAccount.class);
            configuration.addAnnotatedClass(ProjectAdmin.class);
            configuration.addAnnotatedClass(ProjectUser.class);

            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
            builder.applySettings(configuration.getProperties());
            ServiceRegistry serviceRegistery = builder.build();

            factory = configuration.buildSessionFactory(serviceRegistery);
        }

        catch (Throwable ex)
        {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session newSession()
    {
        return factory.openSession();
    }
}
