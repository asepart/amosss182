package de.fau.cs.osr.amos.asepart;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
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
            configuration.addAnnotatedClass(Message.class);

            // Relationships
            configuration.addAnnotatedClass(Membership.class);

            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
            builder.applySettings(configuration.getProperties());
            ServiceRegistry serviceRegistry = builder.build();

            factory = configuration.buildSessionFactory(serviceRegistry);

            // Create sample data for testing
            try (Session session = Database.openSession())
            {
                session.beginTransaction();

                Database.putAdmin(session, "admin", "admin", "Default", "Admin");
                Database.putUser(session, "user", "user", "Default", "User", "+4917123456");

                Database.putProject(session, "pizza", "testproject", "admin");
                
                Database.putTicket(session, "pizza", "Name",
                        "Summary",
                        "Description",
                        TicketCategory.ONE_TIME_ERROR,
                        13);

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

    static void putProject(Session session, String key, String name, String owner)
    {
        Project project = new Project();
        project.setEntryKey(key);
        project.setProjectName(name);
        project.setOwner(owner);

        putProject(session, project);
    }

    static void putProject(Session session, Project project)
    {
        session.save(project);
    }

    static void deleteProject(Session session, String key)
    {
        Project project = getProject(session, key);

        Query ticketQuery = session.createQuery("select id from Ticket where projectKey = :projectKey");
        ticketQuery.setParameter("projectKey", key);
        List<Integer> ticketIds = ticketQuery.getResultList();

        for (Integer id : ticketIds)
        {
            deleteTicket(session, id);
        }

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Membership> criteria = builder.createQuery(Membership.class);

        Root<Membership> columns = criteria.from(Membership.class);
        criteria.where(builder.equal(columns.get("projectKey"), key));
        List<Membership> resultList = session.createQuery(criteria).getResultList();

        for (Membership member : resultList)
        {
            session.delete(member);
        }

        session.delete(project);
    }

    static boolean isProject(Session session, String key)
    {
        Project project = session.get(Project.class, key);
        return project != null;
    }

    static Project getProject(Session session, String key)
    {
        return session.get(Project.class, key);
    }

    static Project[] listProjects(Session session, String owner)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Project> criteria = builder.createQuery(Project.class);
        criteria.from(Project.class);

        List<Project> projectList = session.createQuery(criteria).getResultList();
        ArrayList<Project> filteredProjectList = new ArrayList<>(projectList.size());

        for (Project p : projectList)
        {
            if (p.getOwner().equals(owner))
                filteredProjectList.add(p);
        }

        Project[] projects = new Project[filteredProjectList.size()];
        projects = filteredProjectList.toArray(projects);

        return projects;
    }

    static User[] getUsersOfProject(Session session, String projectKey)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Membership> criteria = builder.createQuery(Membership.class);

        Root<Membership> columns = criteria.from(Membership.class);
        criteria.where(builder.equal(columns.get("projectKey"), projectKey));
        List<Membership> resultList = session.createQuery(criteria).getResultList();

        User[] users = new User[resultList.size()];
        int index = 0;

        for (Membership pu : resultList)
        {
            users[index] = getUser(session, pu.getLoginName());
            ++index;
        }

        return users;
    }

    static boolean isUserMemberOfProject(Session session, String userName, String projectKey)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Membership> criteria = builder.createQuery(Membership.class);
        Root<Membership> columns = criteria.from(Membership.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(columns.get("projectKey"), projectKey));
        predicates.add(builder.equal(columns.get("loginName"), userName));
        criteria.select(columns).where(predicates.toArray(new Predicate[]{}));

        List<Membership> resultList = session.createQuery(criteria).getResultList();

        return resultList.size() != 0;
    }

    static boolean isAdminOfProject(Session session, String adminName, String projectKey)
    {
        Project project = getProject(session, projectKey);
        return project != null && project.getOwner().equals(adminName);
    }

    static void joinProject(Session session, String userName, String entryKey)
    {
        if (!isUser(session, userName))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("User does not exist.").build());

        if (!isProject(session, entryKey))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Project does not exist.").build());

        if (isUserMemberOfProject(session, userName, entryKey))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("User already member of project.").build());

        Membership m = new Membership();
        m.setLoginName(userName);
        m.setProjectKey(entryKey);

        session.save(m);
    }

    static void leaveProject(Session session, String userName, String entryKey)
    {
        if (!isUser(session, userName))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("User does not exist.").build());

        if (!isProject(session, entryKey))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Project does not exist.").build());

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Membership> criteria = builder.createQuery(Membership.class);
        Root<Membership> columns = criteria.from(Membership.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(columns.get("projectKey"), entryKey));
        predicates.add(builder.equal(columns.get("loginName"), userName));
        criteria.select(columns).where(predicates.toArray(new Predicate[]{}));

        List<Membership> resultList = session.createQuery(criteria).getResultList();

        if (resultList.size() == 0)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("User not member of project.").build());

        for (Membership member : resultList)
        {
            session.delete(member);
        }
    }

    static Integer putTicket(Session session, String projectKey, String ticketName, String ticketSummary, String ticketDescription, TicketCategory ticketCategory, Integer requiredObservations)
    {
        Ticket ticket = new Ticket();
        ticket.setTicketName(ticketName);
        ticket.setTicketSummary(ticketSummary);
        ticket.setTicketDescription(ticketDescription);
        ticket.setTicketCategory(ticketCategory);
        ticket.setRequiredObservations(requiredObservations);
        ticket.setProjectKey(projectKey);

        return putTicket(session, ticket);
    }

    static Integer putTicket(Session session, Ticket ticket)
    {
        return (Integer) session.save(ticket);
    }

    static void deleteTicket(Session session, Integer ticketId)
    {
        Ticket oldTicket = getTicket(session, ticketId);

        Query msgQuery = session.createQuery("delete from Message where ticketId = :ticketId");
        msgQuery.setParameter("ticketId", ticketId);
        msgQuery.executeUpdate();

        session.delete(oldTicket);
    }

    static boolean isTicket(Session session, Integer ticketId)
    {
        Ticket ticket = session.get(Ticket.class, ticketId);
        return ticket != null;
    }

    static Ticket getTicket(Session session, Integer ticketId)
    {
        if (!isTicket(session, ticketId))
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Ticket not found.").build());
        }

        return session.get(Ticket.class, ticketId);
    }

    static Ticket[] getTicketsOfProject(Session session, String loginName, String role, String projectKey)
    {
        Project project = getProject(session, projectKey);

        if (role.equals("Admin") && !project.getOwner().equals(loginName))
        {
            throw new WebApplicationException("You are not the admin of this project.");
        }

        else if (role.equals("User") && !isUserMemberOfProject(session, loginName, projectKey))
        {
            throw new WebApplicationException("You have not joined that project.");
        }

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Ticket> criteria = builder.createQuery(Ticket.class);
        Root<Ticket> columns = criteria.from(Ticket.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(columns.get("projectKey"), projectKey));
        criteria.select(columns).where(predicates.toArray(new Predicate[]{}));

        List<Ticket> ticketList = session.createQuery(criteria).getResultList();
        Ticket[] tickets = new Ticket[ticketList.size()];
        tickets = ticketList.toArray(tickets);

        return tickets;
    }

    static void putMessage(Session session, Integer ticketId, String message, String sender, String role)
    {
        Ticket ticket = getTicket(session, ticketId);
        Project project = getProject(session, ticket.getProjectKey());

        if (role.equals("Admin") && !project.getOwner().equals(sender))
        {
            throw new WebApplicationException("You are not the admin of this project.");
        }

        if (role.equals("User") && !isUserMemberOfProject(session, sender, ticket.getProjectKey()))
        {
            throw new WebApplicationException("You have not joined that project.");
        }

        Message m = new Message();
        m.setTicketId(ticketId);
        m.setSender(sender);
        m.setContent(message);

        session.save(m);
    }

    static Message[] listMessages(Session session, Integer ticketId, String receiver, String role)
    {
        Ticket ticket = getTicket(session, ticketId);
        Project project = getProject(session, ticket.getProjectKey());

        if (role.equals("Admin") && !project.getOwner().equals(receiver))
        {
            throw new WebApplicationException("You are not the admin of this project.");
        }

        if (role.equals("User") && !isUserMemberOfProject(session, receiver, ticket.getProjectKey()))
        {
            throw new WebApplicationException("You have not joined that project.");
        }

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Message> criteria = builder.createQuery(Message.class);
        Root<Message> columns = criteria.from(Message.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(columns.get("ticketId"), ticketId));
        criteria.select(columns).where(predicates.toArray(new Predicate[]{}));
        List<Message> messageList = session.createQuery(criteria).getResultList();

        Message[] messages = new Message[messageList.size()];
        messages = messageList.toArray(messages);

        return messages;
    }

    static void putUser(Session session, String loginName, String password, String firstName, String lastName, String phone)
    {
        User newUser = new User();
        newUser.setLoginName(loginName);
        newUser.setPassword(password);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setPhone(phone);

        putUser(session, newUser);
    }

    static void putUser(Session session, User user)
    {
        if (isAdmin(session, user.getLoginName()))
            throw new WebApplicationException("Admin with same login name already exists.");

        String password = user.getPassword();
        user.setPassword(hashPassword(password));

        session.save(user);
    }

    static User getUser(Session session, String loginName)
    {
        User user = session.get(User.class, loginName);
        user.setPassword(null);

        return user;
    }

    static boolean isUser(Session session, String loginName)
    {
        User user = session.get(User.class, loginName);
        return user != null;
    }

    static User[] listUsers(Session session)
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

    static void putAdmin(Session session, String loginName, String password, String firstName, String lastName)
    {
        Admin newAdmin = new Admin();
        newAdmin.setLoginName(loginName);
        newAdmin.setPassword(password);
        newAdmin.setFirstName(firstName);
        newAdmin.setLastName(lastName);

        putAdmin(session, newAdmin);
    }

    static void putAdmin(Session session, Admin admin)
    {
        if (isUser(session, admin.getLoginName()))
            throw new WebApplicationException("User with same login name already exists.");

        String password = admin.getPassword();
        admin.setPassword(hashPassword(password));

        session.save(admin);
    }

    static Admin getAdmin(Session session, String loginName)
    {
        Admin admin = session.get(Admin.class, loginName);
        admin.setPassword(null);

        return admin;
    }

    static boolean isAdmin(Session session, String loginName)
    {
        Admin admin = session.get(Admin.class, loginName);
        return admin != null;
    }

    static Admin[] listAdmins(Session session)
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
