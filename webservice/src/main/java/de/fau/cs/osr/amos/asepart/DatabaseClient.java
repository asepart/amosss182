package de.fau.cs.osr.amos.asepart;

import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import org.mindrot.jbcrypt.BCrypt;

import de.fau.cs.osr.amos.asepart.entities.Account;
import de.fau.cs.osr.amos.asepart.entities.Admin;
import de.fau.cs.osr.amos.asepart.entities.Project;
import de.fau.cs.osr.amos.asepart.entities.User;
import de.fau.cs.osr.amos.asepart.relationships.ProjectAccount;
import de.fau.cs.osr.amos.asepart.relationships.ProjectAdmin;
import de.fau.cs.osr.amos.asepart.relationships.ProjectUser;

import javax.ws.rs.WebApplicationException;
import java.util.List;

public class DatabaseClient implements AutoCloseable
{
    private static final SessionFactory factory;
    private Session session;

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
            ServiceRegistry serviceRegistry = builder.build();

            factory = configuration.buildSessionFactory(serviceRegistry);
        }

        catch (Throwable ex)
        {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static String hashPassword(String password)
    {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static boolean checkPassword(String password, String hash)
    {
        return BCrypt.checkpw(password, hash);
    }

    public DatabaseClient()
    {
        session = factory.openSession();
    }

    @Override
    public void close()
    {
        session.close();
    }

    public void putProject(String projectName, String entryKey)
    {
        session.beginTransaction();
        Project project = new Project();
        project.setProjectName(projectName);
        project.setEntryKey(entryKey);

        session.save(project);
        session.getTransaction().commit();
    }

    public void putProject(Project project)
    {
        session.beginTransaction();
        session.save(project);
        session.getTransaction().commit();
    }

    public boolean isProject(String projectName)
    {
        Project project = session.get(Project.class, projectName);
        return project != null;
    }

    public void addUsersToProject(String user, String project)
    {
        session.beginTransaction();

        /*
        if (!isUser(user))
            throw new WebApplicationException("User does not exist.");
        if (!isProject(project))
            throw new WebApplicationException("Project does not exist.");
        if (isUserMemberOfProject(user, project))
            throw new WebApplicationException("User already member of project.");
            */

        ProjectUser pu = new ProjectUser();
        pu.setRel(new ProjectAccount(project, user));

        session.save(pu);
        session.getTransaction().commit();
    }

    public boolean isUserMemberOfProject(String user, String project)
    {
        Query query = session.createQuery("select pu.rel.loginName from ProjectUser pu where pu.rel.projectName = :projectParam and pu.rel.loginName = :userParam");
        query.setParameter("projectParam", project);
        query.setParameter("userParam", user);

        List result = query.list();

        if (result.size() == 0)
            return false;
        else return true;
    }

    public User[] getUsersOfProject(String projectName)
    {
        Query userQuery = session.createQuery("select pu.rel.loginName from ProjectUser pu where pu.rel.projectName = :param");
        userQuery.setParameter("param", projectName);

        List resultList = userQuery.list();
        User[] users = new User[resultList.size()];
        int index = 0;

        for (Object row: resultList)
        {
            users[index] = session.get(User.class, row.toString());
            ++index;
        }

        return users;
    }

    public User getUser(String loginName)
    {
        User user = session.get(User.class, loginName);
        return user;
    }

    public void putUser(String loginName, String password, String firstName, String lastName, String phone)
    {
        session.beginTransaction();

        User newUser = new User();
        newUser.setLoginName(loginName);
        newUser.setPasswordHash(hashPassword(password));
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setPhone(phone);

        session.save(newUser);
        session.getTransaction().commit();
    }

    public void putUser(User user)
    {
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
    }

    public boolean isUser(String loginName)
    {
        User user = session.get(User.class, loginName);
        return user != null;
    }

    public Admin getAdmin(String loginName)
    {
        Admin admin = session.get(Admin.class, loginName);
        return admin;
    }


    public void putAdmin(String loginName, String password, String firstName, String lastName)
    {
        session.beginTransaction();

        Admin newAdmin = new Admin();
        newAdmin.setLoginName(loginName);
        newAdmin.setPasswordHash(hashPassword(password));
        newAdmin.setFirstName(firstName);
        newAdmin.setLastName(lastName);

        session.save(newAdmin);
        session.getTransaction().commit();
    }

    public void putAdmin(Admin admin)
    {
        session.beginTransaction();
        session.save(admin);
        session.getTransaction().commit();
    }


    public boolean isAdmin(String loginName)
    {
        Admin admin = session.get(Admin.class, loginName);
        return admin != null;
    }

    public boolean authenticate(String loginName, String password, Class<?> type)
    {
        try
        {
            session.beginTransaction();

            Account account = (Account) session.get(type, loginName);

            if (account != null)
            {
                String hash = account.getPasswordHash();
                return checkPassword(password, hash);
            }

            return false;
        }

        finally
        {
            session.getTransaction().commit();
        }
    }
}
