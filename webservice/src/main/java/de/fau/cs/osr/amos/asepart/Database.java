package de.fau.cs.osr.amos.asepart;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.WebApplicationException;

import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import org.mindrot.jbcrypt.BCrypt;

import de.fau.cs.osr.amos.asepart.entities.*;
import de.fau.cs.osr.amos.asepart.relationships.*;

public class Database
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
            ServiceRegistry serviceRegistry = builder.build();

            factory = configuration.buildSessionFactory(serviceRegistry);

            // Create default admin for testing
            try (Session session = Database.openSession())
            {
                session.beginTransaction();
                Database.putAdmin(session, "admin", "admin", "Default", "Admin");
                Database.putUser(session, "user", "user", "Default", "User", "+4917123456");
                session.getTransaction().commit();
            }
        }

        catch (Throwable ex)
        {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session openSession()
    {
        return factory.openSession();
    }

    private static String hashPassword(String password)
    {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static boolean checkPassword(String password, String hash)
    {
        return BCrypt.checkpw(password, hash);
    }

    public static void putProject(Session session, String projectName, String entryKey)
    {
        Project project = new Project();
        project.setProjectName(projectName);
        project.setEntryKey(entryKey);

        session.save(project);
    }

    public static void putProject(Session session, Project project)
    {
        session.save(project);
    }

    public static boolean isProject(Session session, String projectName)
    {
        Project project = session.get(Project.class, projectName);
        return project != null;
    }

    public static Project getProject(Session session, String projectName)
    {
        return session.get(Project.class, projectName);
    }

    public static Project[] listProjects(Session session)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Project> criteria = builder.createQuery(Project.class);
        criteria.from(Project.class);

        List<Project> projectList = session.createQuery(criteria).getResultList();
        Project[] projects = new Project[projectList.size()];
        projects = projectList.toArray(projects);

        return projects;
    }

    public static void addUsersToProject(Session session, String user, String project)
    {
        if (!isUser(session, user))
            throw new WebApplicationException("User does not exist.");

        if (!isProject(session, project))
            throw new WebApplicationException("Project does not exist.");

        if (isUserMemberOfProject(session, user, project))
            throw new WebApplicationException("User already member of project.");

        ProjectUser pu = new ProjectUser();
        pu.setLoginName(user);
        pu.setProjectName(project);

        session.save(pu);
    }

    public static boolean isUserMemberOfProject(Session session, String user, String project)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ProjectUser> criteria = builder.createQuery(ProjectUser.class);

        Root<ProjectUser> columns = criteria.from(ProjectUser.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(columns.get("projectName"), project));
        predicates.add(builder.equal(columns.get("loginName"), user));
        criteria.select(columns).where(predicates.toArray(new Predicate[]{}));

        List<ProjectUser> resultList = session.createQuery(criteria).getResultList();

        if (resultList.size() == 0)
            return false;

        else return true;
    }

    public static User[] getUsersOfProject(Session session, String projectName)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ProjectUser> criteria = builder.createQuery(ProjectUser.class);

        Root<ProjectUser> columns = criteria.from(ProjectUser.class);
        criteria.where(builder.equal(columns.get("projectName"), projectName));
        List<ProjectUser> resultList = session.createQuery(criteria).getResultList();

        User[] users = new User[resultList.size()];
        int index = 0;

        for (ProjectUser pu : resultList)
        {
            users[index] = session.get(User.class, pu.getLoginName());
            ++index;
        }

        return users;
    }

    public static void putUser(Session session, String loginName, String password, String firstName, String lastName, String phone)
    {
        User newUser = new User();
        newUser.setLoginName(loginName);
        newUser.setPassword(hashPassword(password));
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setPhone(phone);

        session.save(newUser);
    }

    public static void putUser(Session session, User user)
    {
        String password = user.getPassword();
        user.setPassword(hashPassword(password));

        session.save(user);
    }

    public static User getUser(Session session, String loginName)
    {
        return session.get(User.class, loginName);
    }

    public static boolean isUser(Session session, String loginName)
    {
        User user = session.get(User.class, loginName);
        return user != null;
    }

    public static void putAdmin(Session session, String loginName, String password, String firstName, String lastName)
    {
        Admin newAdmin = new Admin();
        newAdmin.setLoginName(loginName);
        newAdmin.setPassword(hashPassword(password));
        newAdmin.setFirstName(firstName);
        newAdmin.setLastName(lastName);

        session.save(newAdmin);
    }

    public static void putAdmin(Session session, Admin admin)
    {
        String password = admin.getPassword();
        admin.setPassword(hashPassword(password));

        session.save(admin);
    }

    public static Admin getAdmin(Session session, String loginName)
    {
        return session.get(Admin.class, loginName);
    }

    public static boolean isAdmin(Session session, String loginName)
    {
        Admin admin = session.get(Admin.class, loginName);
        return admin != null;
    }

    public static boolean authenticate(Session session, String loginName, String password, Class<?> type)
    {
        Account account = (Account) session.get(type, loginName);

        if (account != null)
        {
            String hash = account.getPassword();
            return checkPassword(password, hash);
        }

        return false;
    }
}
