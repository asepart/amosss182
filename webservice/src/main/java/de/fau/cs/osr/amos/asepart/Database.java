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
            configuration.addAnnotatedClass(Message.class);

            // Relationships
            configuration.addAnnotatedClass(ProjectAccount.class);
            configuration.addAnnotatedClass(ProjectAdmin.class);
            configuration.addAnnotatedClass(ProjectUser.class);
            configuration.addAnnotatedClass(ProjectTicket.class);

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

                Database.putProject(session, "admin", "testproject", "pizza");
                Database.addUserToProject(session, "admin", "user", "testproject");

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

    static Integer putTicket(Session session, String ticketName, String ticketSummary, String ticketDescription, TicketCategory ticketCategory, Integer requiredObservations)
    {
        Ticket ticket = new Ticket();
        ticket.setTicketName(ticketName);
        ticket.setTicketSummary(ticketSummary);
        ticket.setTicketDescription(ticketDescription);
        ticket.setTicketCategory(ticketCategory);
        ticket.setRequiredObservations(requiredObservations);

        return putTicket(session, ticket);
    }

    static Integer putTicket(Session session, Ticket ticket)
    {
        Integer id = (Integer) session.save(ticket);
        return id;
    }

    private static boolean isTicket(Session session, Integer ticketId)
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

        Ticket ticket = session.get(Ticket.class, ticketId);

        return ticket;
    }

    static void addTicketToProject(Session session, String admin, Integer ticketId, String project)
    {
        if (!isTicket(session, ticketId))
        { throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Ticket does not exist.").build()); }

        if (!isProject(session, project))
        { throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Project does not exist.").build()); }

        if (isTicketPartOfProject(session, ticketId, project))
        { throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Ticket already added to project.").build()); }

        if (!isAccountPartOfProject(session, ProjectAdmin.class, admin, project))
        { throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("You are not an admin of this project.").build()); }

        ProjectTicket pt = new ProjectTicket();
        pt.setTicketId(ticketId);
        pt.setProjectName(project);

        session.save(pt);
    }

    static Ticket[] getTicketsOfProject(Session session, String adminName, String projectName)
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
        int index = 0;

        for (ProjectTicket pt : resultList)
        {
            tickets[index] = getTicket(session, pt.getRelId());
            ++index;
        }

        return tickets;
    }

    static void putProject(Session session, String adminName, String projectName, String entryKey)
    {
        Project project = new Project();
        project.setProjectName(projectName);
        project.setEntryKey(entryKey);

        putProject(session, adminName, project);
    }

    static void putProject(Session session, String adminName, Project project)
    {
        session.save(project);
        addAdminToProject(session, adminName, project.getProjectName());
    }

    private static boolean isProject(Session session, String projectName)
    {
        Project project = session.get(Project.class, projectName);
        return project != null;
    }

    static Project getProject(Session session, String adminName, String projectName)
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

    static Project getProjectByKey(Session session, String entryKey)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Project> criteria = builder.createQuery(Project.class);

        Root<Project> columns = criteria.from(Project.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(columns.get("entryKey"), entryKey));
        criteria.select(columns).where(predicates.toArray(new Predicate[]{}));

        List<Project> resultList = session.createQuery(criteria).getResultList();

        if (resultList.size() == 0)
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Key not valid.").build());
        }

        if (resultList.size() >= 2)
        {
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error.").build());
        }

        return resultList.get(0);
    }

    static Project[] listProjects(Session session, String adminName)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Project> criteria = builder.createQuery(Project.class);
        criteria.from(Project.class);

        List<Project> projectList = session.createQuery(criteria).getResultList();
        ArrayList<Project> filteredProjectList = new ArrayList<>(projectList.size());

        for (Project p : projectList)
        {
            if (isAccountPartOfProject(session, ProjectAdmin.class, adminName, p.getProjectName()))
            { filteredProjectList.add(p); }
        }

        Project[] projects = new Project[filteredProjectList.size()];
        projects = filteredProjectList.toArray(projects);

        return projects;
    }

    static void addUserToProject(Session session, String admin, String user, String project)
    {
        if (!isUser(session, user))
        { throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("User does not exist.").build()); }

        if (!isProject(session, project))
        { throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Project does not exist.").build()); }

        if (isAccountPartOfProject(session, ProjectUser.class, user, project))
        { throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("User already member of project.").build()); }

        if (!isAccountPartOfProject(session, ProjectAdmin.class, admin, project))
        { throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("You are not an admin of this project.").build()); }

        ProjectUser pu = new ProjectUser();
        pu.setLoginName(user);
        pu.setProjectName(project);

        session.save(pu);
    }

    private static void addAdminToProject(Session session, String admin, String project)
    {
        if (!isAdmin(session, admin))
        { throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Admin does not exist.").build()); }

        if (!isProject(session, project))
        { throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Project does not exist.").build()); }

        if (isAccountPartOfProject(session, ProjectAdmin.class, admin, project))
        { throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Admin already member of project.").build()); }

        ProjectAdmin pa = new ProjectAdmin();
        pa.setLoginName(admin);
        pa.setProjectName(project);

        session.save(pa);
    }

    static User[] getUsersOfProject(Session session, String adminName, String projectName)
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

    static boolean isAccountPartOfProject(Session session, Class<? extends ProjectAccount> relationship, String loginName, String project)
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
        { return false; }

        else { return true; }
    }

    static boolean isTicketPartOfProject(Session session, Integer ticketId, String project)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ProjectTicket> criteria = builder.createQuery(ProjectTicket.class);
        Root<ProjectTicket> columns = criteria.from(ProjectTicket.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(columns.get("projectName"), project));
        predicates.add(builder.equal(columns.get("ticketId"), ticketId));
        criteria.select(columns).where(predicates.toArray(new Predicate[]{}));

        List<ProjectTicket> resultList = session.createQuery(criteria).getResultList();

        if (resultList.size() == 0)
        { return false; }

        else { return true; }
    }

    private static Project getProjectOfTicket(Session session, Integer ticketId)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ProjectTicket> criteria = builder.createQuery(ProjectTicket.class);
        Root<ProjectTicket> columns = criteria.from(ProjectTicket.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(columns.get("ticketId"), ticketId));
        criteria.select(columns).where(predicates.toArray(new Predicate[]{}));

        List<ProjectTicket> resultList = session.createQuery(criteria).getResultList();

        if (resultList.size() == 0)
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Ticket not found.").build());
        }

        if (resultList.size() >= 2)
        {
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error.").build());
        }

        String projectName = resultList.get(0).getProjectName();
        return session.get(Project.class, projectName);
    }

    static String joinProject(Session session, String userName, String entryKey)
    {
        Project project = getProjectByKey(session, entryKey);
        String projectName = project.getProjectName();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ProjectUser> criteria = builder.createQuery(ProjectUser.class);

        Root<ProjectUser> columns = criteria.from(ProjectUser.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(columns.get("projectName"), projectName));
        predicates.add(builder.equal(columns.get("loginName"), userName));

        criteria.select(columns).where(predicates.toArray(new Predicate[]{}));
        List<ProjectUser> resultList = session.createQuery(criteria).getResultList();

        if (resultList.size() == 0)
        {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("You are not allowed to join this project.").build());
        }

        if (resultList.size() >= 2)
        {
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error.").build());
        }

        ProjectUser pu = resultList.get(0);

        if (pu.getJoined())
        {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("You have already joined the project.").build());
        }

        pu.setJoined(true);

        session.save(pu);
        return projectName;
    }

    static void putMessage(Session session, Integer ticketId, String message, String sender, String role)
    {
        Ticket ticket = getTicket(session, ticketId);
        Project project = getProjectOfTicket(session, ticket.getId());

        Class<? extends ProjectAccount> relationshipType = ProjectUser.class;

        if (role.equals("Admin"))
            relationshipType = ProjectAdmin.class;

        if (!isAccountPartOfProject(session, relationshipType, sender, project.getProjectName()))
        {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("You are not part of that project.").build());
        }

        Message newMessage = new Message();
        newMessage.setTicketId(ticketId);
        newMessage.setSender(sender);
        newMessage.setContent(message);

        session.save(newMessage);
    }

    static Message[] listMessages(Session session, Integer ticketId, String receiver, String role)
    {
        Ticket ticket = getTicket(session, ticketId);
        Project project = getProjectOfTicket(session, ticket.getId());

        Class<? extends ProjectAccount> relationshipType = ProjectUser.class;

        if (role.equals("Admin"))
            relationshipType = ProjectAdmin.class;

        if (!isAccountPartOfProject(session, relationshipType, receiver, project.getProjectName()))
        {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("You are not part of that project.").build());
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
        String password = user.getPassword();
        user.setPassword(hashPassword(password));

        session.save(user);
    }

    static User getUser(Session session, String loginName)
    {
        if (!isUser(session, loginName))
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("User not found.").build());
        }

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
