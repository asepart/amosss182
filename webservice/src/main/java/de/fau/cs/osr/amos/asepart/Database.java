package de.fau.cs.osr.amos.asepart;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

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
            configuration.addAnnotatedClass(Ticket.class);

            // Relationships
            configuration.addAnnotatedClass(ProjectAccount.class);
            configuration.addAnnotatedClass(ProjectAdmin.class);
            configuration.addAnnotatedClass(ProjectUser.class);

            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
            builder.applySettings(configuration.getProperties());
            ServiceRegistry serviceRegistry = builder.build();

            factory = configuration.buildSessionFactory(serviceRegistry);

            // Create default admin and user for testing
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

    public static boolean authenticate(Session session, String loginName, String password, Class<? extends Account> type)
    {
        Account account = session.get(type, loginName);

        if (account != null)
        {
            String hash = account.getPassword();
            return checkPassword(password, hash);
        }

        return false;
    }

    public static void putTicket(Session session, String ticketName, String ticketSummary,
                                 String ticketDescription, TicketCategory ticketCategory)
    {
        Ticket ticket = new Ticket();
        ticket.setTicketName(ticketName);
        ticket.setTicketSummary(ticketSummary);
        ticket.setTicketDescription(ticketDescription);
        ticket.setTicketCategory(ticketCategory);

        putTicket(session, ticket);
    }

    public static void putTicket(Session session, Ticket ticket)
    {
        session.save(ticket);
    }
    
    public static boolean isTicket(Session session, String ticketName)
    {
        Ticket ticket = session.get(Ticket.class, ticketName);
        return ticket != null;
    }
    
    public static Ticket getTicket(Session session, String ticketName)
    {
        if (!isTicket(session, ticketName))
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Ticket not found.").build());
        }

        Ticket ticket = session.get(Ticket.class, ticketName);

        return ticket;
    }
    
    public static void addTicketToProject(Session session, String admin, String ticket, String project)
    {
        if (!isTicket(session, ticket))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Ticket does not exist.").build());

        if (!isProject(session, project))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Project does not exist.").build());

        if (isTicketPartOfProject(session, ProjectTicket.class, ticket, project))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Ticket already added to project.").build());

        if (!isAccountPartOfProject(session, ProjectAdmin.class, admin, project))
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("You are not an admin of this project.").build());

        ProjectTicket pt = new ProjectTicket();
        pt.setTicketName(ticket);
        pt.setProjectName(project);

        session.save(pt);
    }
    
    public static Ticket[] getTicketsOfProject(Session session, String adminName, String projectName)
    {
        if (!Database.isProject(session, projectName))
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Project not found.").build());
        }

        if (!Database.isAccountPartOfProject(session, ProjectAdmin.class, adminName, projectName))
        {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("You are not allowed to view that project.").build());
        }

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ProjectTicket> criteria = builder.createQuery(ProjectTicket.class);

        Root<ProjectTicket> columns = criteria.from(ProjectTicket.class);
        criteria.where(builder.equal(columns.get("projectName"), projectName));
        List<ProjectTicket> resultList = session.createQuery(criteria).getResultList();

        Ticket[] tickets = new Ticket[resultList.size()];
        tickets = resultList.toArray(tickets);
        int index = 0;
        
        for (ProjectTicket pt : resultList)
        {
            tickets[index] = getTicket(session, pt.getTicketName());
            ++index;
        }

        return tickets;
    }

    public static void putProject(Session session, String adminName, String projectName, String entryKey)
    {
        Project project = new Project();
        project.setProjectName(projectName);
        project.setEntryKey(entryKey);

        putProject(session, adminName, project);
    }

    public static void putProject(Session session, String adminName, Project project)
    {
        session.save(project);
        addAdminToProject(session, adminName, project.getProjectName());
    }

    public static boolean isProject(Session session, String projectName)
    {
        Project project = session.get(Project.class, projectName);
        return project != null;
    }

    public static Project getProject(Session session, String adminName, String projectName)
    {
        if (!isProject(session, projectName))
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Project not found.").build());
        }

        if (!isAccountPartOfProject(session, ProjectAdmin.class, adminName, projectName))
        {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("You are not allowed to view that project.").build());
        }

        return session.get(Project.class, projectName);
    }

    public static Project[] listProjects(Session session, String adminName)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Project> criteria = builder.createQuery(Project.class);
        criteria.from(Project.class);

        List<Project> projectList = session.createQuery(criteria).getResultList();
        ArrayList<Project> filteredProjectList = new ArrayList<>(projectList.size());

        for (Project p : projectList)
        {
            if (isAccountPartOfProject(session, ProjectAdmin.class, adminName, p.getProjectName()))
                filteredProjectList.add(p);
        }

        Project[] projects = new Project[filteredProjectList.size()];
        projects = filteredProjectList.toArray(projects);

        return projects;
    }

    public static void addUserToProject(Session session, String admin, String user, String project)
    {
        if (!isUser(session, user))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("User does not exist.").build());

        if (!isProject(session, project))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Project does not exist.").build());

        if (isAccountPartOfProject(session, ProjectUser.class, user, project))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("User already member of project.").build());

        if (!isAccountPartOfProject(session, ProjectAdmin.class, admin, project))
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("You are not an admin of this project.").build());

        ProjectUser pu = new ProjectUser();
        pu.setLoginName(user);
        pu.setProjectName(project);

        session.save(pu);
    }

    public static void addAdminToProject(Session session, String admin, String project)
    {
        if (!isAdmin(session, admin))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Admin does not exist.").build());

        if (!isProject(session, project))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Project does not exist.").build());

        if (isAccountPartOfProject(session, ProjectAdmin.class, admin, project))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Admin already member of project.").build());

        ProjectAdmin pa = new ProjectAdmin();
        pa.setLoginName(admin);
        pa.setProjectName(project);

        session.save(pa);
    }

    public static User[] getUsersOfProject(Session session, String adminName, String projectName)
    {
        if (!Database.isProject(session, projectName))
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Project not found.").build());
        }

        if (!Database.isAccountPartOfProject(session, ProjectAdmin.class, adminName, projectName))
        {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("You are not allowed to view that project.").build());
        }

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ProjectUser> criteria = builder.createQuery(ProjectUser.class);

        Root<ProjectUser> columns = criteria.from(ProjectUser.class);
        criteria.where(builder.equal(columns.get("projectName"), projectName));
        List<ProjectUser> resultList = session.createQuery(criteria).getResultList();

        User[] users = new User[resultList.size()];
        int index = 0;

        for (ProjectUser pu : resultList)
        {
            users[index] = getUser(session, pu.getLoginName());
            ++index;
        }

        return users;
    }

    public static boolean isAccountPartOfProject(Session session, Class<? extends ProjectAccount> relationship, String loginName, String project)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();

        @SuppressWarnings("unchecked")
        CriteriaQuery<ProjectAccount> criteria = (CriteriaQuery<ProjectAccount>) builder.createQuery(relationship);

        @SuppressWarnings("unchecked")
        Root<ProjectAccount> columns = (Root<ProjectAccount>) criteria.from(relationship);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(columns.get("projectName"), project));
        predicates.add(builder.equal(columns.get("loginName"), loginName));
        criteria.select(columns).where(predicates.toArray(new Predicate[]{}));

        List<ProjectAccount> resultList = session.createQuery(criteria).getResultList();

        if (resultList.size() == 0)
            return false;

        else return true;
    }
    
    public static boolean isTicketPartOfProject(Session session, Class<? extends ProjectTicket> relationship, String ticketName, String project)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();

        @SuppressWarnings("unchecked")
        CriteriaQuery<ProjectTicket> criteria = (CriteriaQuery<ProjectTicket>) builder.createQuery(relationship);

        @SuppressWarnings("unchecked")
        Root<ProjectTicket> columns = (Root<ProjectTicket>) criteria.from(relationship);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(columns.get("projectName"), project));
        predicates.add(builder.equal(columns.get("ticketName"), ticketName));
        criteria.select(columns).where(predicates.toArray(new Predicate[]{}));

        List<ProjectTicket> resultList = session.createQuery(criteria).getResultList();

        if (resultList.size() == 0)
            return false;

        else return true;
    }

    public static void putUser(Session session, String loginName, String password, String firstName, String lastName, String phone)
    {
        User newUser = new User();
        newUser.setLoginName(loginName);
        newUser.setPassword(password);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setPhone(phone);

        putUser(session, newUser);
    }

    public static void putUser(Session session, User user)
    {
        String password = user.getPassword();
        user.setPassword(hashPassword(password));

        session.save(user);
    }

    public static User getUser(Session session, String loginName)
    {
        if (!isUser(session, loginName))
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("User not found.").build());
        }

        User user = session.get(User.class, loginName);
        user.setPassword(null);

        return user;
    }

    public static boolean isUser(Session session, String loginName)
    {
        User user = session.get(User.class, loginName);
        return user != null;
    }

    public static User[] listUsers(Session session)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        criteria.from(User.class);

        List<User> userList = session.createQuery(criteria).getResultList();
        User[] users = new User[userList.size()];
        users = userList.toArray(users);

        for (int i = 0; i < users.length; i++)
        {
            users[i].setPassword(null);
        }

        return users;
    }

    public static void putAdmin(Session session, String loginName, String password, String firstName, String lastName)
    {
        Admin newAdmin = new Admin();
        newAdmin.setLoginName(loginName);
        newAdmin.setPassword(password);
        newAdmin.setFirstName(firstName);
        newAdmin.setLastName(lastName);

        putAdmin(session, newAdmin);
    }

    public static void putAdmin(Session session, Admin admin)
    {
        String password = admin.getPassword();
        admin.setPassword(hashPassword(password));

        session.save(admin);
    }

    public static Admin getAdmin(Session session, String loginName)
    {
        if (!isAdmin(session, loginName))
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Admin not found.").build());
        }

        Admin admin = session.get(Admin.class, loginName);
        admin.setPassword(null);

        return admin;
    }

    public static boolean isAdmin(Session session, String loginName)
    {
        Admin admin = session.get(Admin.class, loginName);
        return admin != null;
    }

    public static Admin[] listAdmins(Session session)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Admin> criteria = builder.createQuery(Admin.class);
        criteria.from(Admin.class);

        List<Admin> adminList = session.createQuery(criteria).getResultList();
        Admin[] admins = new Admin[adminList.size()];
        admins = adminList.toArray(admins);

        for (int i = 0; i < admins.length; i++)
        {
            admins[i].setPassword(null);
        }

        return admins;
    }
}
