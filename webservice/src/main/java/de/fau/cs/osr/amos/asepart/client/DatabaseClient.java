package de.fau.cs.osr.amos.asepart.client;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.postgresql.ds.PGSimpleDataSource;

/**
 * This class connects to a PostgreSQL database. The default
 * values are localhost for the hostname, "postgres" for
 * the username and "asepart" for the password. These values
 * can be changed by setting JDBC_DATABASE_URL.
 *
 * If this variable is not present, the hostname can be set
 * using ASEPART_POSTGRES_HOST, which can be useful for
 * connecting Docker containers.
 */

public class DatabaseClient implements AutoCloseable
{
    private static DataSource createDataSource()
    {
        final String jdbcUrl = System.getenv("JDBC_DATABASE_URL");

        PGSimpleDataSource ds = new PGSimpleDataSource();

        if (jdbcUrl != null)
        {
            ds.setUrl(jdbcUrl);
        }

        else
        {
            final String postgresHost = System.getenv("ASEPART_POSTGRES_HOST");

            if (postgresHost != null)
                ds.setServerName(postgresHost);
            else
                ds.setServerName("localhost");

            ds.setUser("postgres");
            ds.setPassword("asepart");
        }

        return ds;
    }

    private static final DataSource ds = createDataSource();
    private final Connection cn;

    public DatabaseClient() throws SQLException
    {
        cn = ds.getConnection();
    }

    @Override
    public void close() throws Exception
    {
        cn.close();
    }

    /**
     * Checks if authentication is valid.
     *
     * @param loginName The account name.
     * @param password The password as plain text.
     * @return true if password is correct, false otherwise.
     * @throws SQLException on database error.
     */

    public boolean authenticate(String loginName, String password) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select count(login_name) from account where login_name = ? and password = crypt(?, password);"))
        {
            stmt.setString(1, loginName);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                return (rs.getInt(1) == 1);
            }
        }
    }

    /**
     * Changes password of user or admin account.
     *
     * Passwords are stored in the database as a salted bcrypt hash.
     *
     * @param loginName The account name.
     * @param password The password as plain text.
     *
     * @throws SQLException on database error.
     * @throws IllegalArgumentException if password is empty.
     */

    public void changePassword(String loginName, String password) throws SQLException
    {
        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("Password must not be empty");

        try (PreparedStatement stmt = cn.prepareStatement("update account set password = crypt(?, gen_salt('bf', 8)) where login_name = ?"))
        {
            stmt.setString(1, password);
            stmt.setString(2, loginName);

            stmt.executeUpdate();
        }
    }

    /**
     * Creates a new user.
     *
     * To enable the user to log in, a password must be set
     * by calling changePassword().
     *
     * @param loginName The account name.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param phoneNumber The user's mobile phone number.
     * @throws SQLException on database error.
     */

    public void insertUser(String loginName, String firstName, String lastName, String phoneNumber) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("insert into user_account(login_name, first_name, last_name, phone_number) values (?, ?, ?, ?);"))
        {
            stmt.setString(1, loginName);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, phoneNumber);

            stmt.execute();
        }
    }

    /**
     * Updates an existing user.
     *
     * @param loginName The account name.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param phoneNumber The user's mobile phone number.
     * @throws SQLException on database error.
     */

    public void updateUser(String loginName, String firstName, String lastName, String phoneNumber) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement(
                "update user_account set first_name = ?, last_name = ?, phone_number = ? where login_name = ?;"))
        {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phoneNumber);
            stmt.setString(4, loginName);

            stmt.executeUpdate();
        }
    }

    /**
     * Checks if an account name matches a user name.
     *
     * @param loginName The account name.
     * @return true if user exists, false if not.
     * @throws SQLException on database error.
     */

    public boolean isUser(String loginName) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select count(login_name) from only user_account where login_name = ?;");)
        {
            stmt.setString(1, loginName);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                int count = rs.getInt(1);

                if (count == 1) return true;
            }
        }
        return false;
    }

    /**
     * Gets a user's account details.
     *
     * @param loginName The account name.
     * @return A map containing the user's details.
     * @throws SQLException on database error.
     */

    public Map<String, String> getUser(String loginName) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select login_name, first_name, last_name, phone_number from only user_account where login_name = ?;");)
        {
            stmt.setString(1, loginName);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();

                Map<String, String> result = new HashMap<>(4);
                result.put("loginName", rs.getString(1));
                result.put("firstName", rs.getString(2));
                result.put("lastName", rs.getString(3));
                result.put("phoneNumber", rs.getString(4));

                return result;
            }
        }
    }

    /**
     * Returns all users.
     *
     * @return List of maps containing each user's details.
     * @throws SQLException on database error.
     */

    public List<Map<String, String>> listUsers() throws SQLException
    {
        try (Statement stmt = cn.createStatement();
             ResultSet rs = stmt.executeQuery("select login_name, first_name, last_name, phone_number from only user_account;"))
        {
            List<Map<String, String>> result = new LinkedList<>();

            while (rs.next())
            {
                Map<String, String> row = new HashMap<>(4);
                row.put("loginName", rs.getString(1));
                row.put("firstName", rs.getString(2));
                row.put("lastName", rs.getString(3));
                row.put("phoneNumber", rs.getString(4));

                result.add(row);
            }

            return result;
        }
    }

    /**
     * Creates a new admin.
     *
     * To enable the admin to log in, a password must be set
     * by calling changePassword().
     *
     * @param loginName The account name.
     * @param firstName The first name of the admin.
     * @param lastName The last name of the admin.
     * @throws SQLException on database error.
     */

    public void insertAdmin(String loginName, String firstName, String lastName) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("insert into admin_account(login_name, first_name, last_name) values (?, ?, ?);");)
        {
            stmt.setString(1, loginName);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);

            stmt.execute();
        }
    }

    /**
     * Updates an existing admin.
     *
     * @param loginName The account name.
     * @param firstName The first name of the admin.
     * @param lastName The last name of the admin.
     * @throws SQLException on database error.
     */

    public void updateAdmin(String loginName, String firstName, String lastName) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement(
                "update admin_account set first_name = ?, last_name = ? where login_name = ?;"))
        {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, loginName);

            stmt.executeUpdate();
        }
    }

    /**
     * Checks if an account name matches an admin name.
     *
     * @param loginName The account name.
     * @return true if admin exists, false if not.
     * @throws SQLException on database error.
     */

    public boolean isAdmin(String loginName) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select count(login_name) from only admin_account where login_name = ?;");)
        {
            stmt.setString(1, loginName);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                int count = rs.getInt(1);

                if (count == 1) return true;
            }
        }
        return false;
    }

    /**
     * Gets an admin's account details.
     *
     * @param loginName The account name.
     * @return A map containing the admin's details.
     * @throws SQLException on database error.
     */

    public Map<String, String> getAdmin(String loginName) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select login_name, first_name, last_name from only admin_account where login_name = ?;");)
        {
            stmt.setString(1, loginName);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();

                Map<String, String> result = new HashMap<>(3);
                result.put("loginName", rs.getString(1));
                result.put("firstName", rs.getString(2));
                result.put("lastName", rs.getString(3));

                return result;
            }
        }
    }

    /**
     * Returns all admins.
     *
     * @return List of maps containing each admin's details.
     * @throws SQLException on database error.
     */

    public List<Map<String, String>> listAdmins() throws SQLException
    {
        try (Statement stmt = cn.createStatement();
             ResultSet rs = stmt.executeQuery("select login_name, first_name, last_name from only admin_account;"))
        {
            List<Map<String, String>> result = new LinkedList<>();

            while (rs.next())
            {
                Map<String, String> row = new HashMap<>(3);
                row.put("loginName", rs.getString(1));
                row.put("firstName", rs.getString(2));
                row.put("lastName", rs.getString(3));

                result.add(row);
            }

            return result;
        }
    }

    /**
     * Delete an account (admin or user).
     *
     * @param loginName The account name.
     * @throws SQLException on database error.
     */

    public void deleteAccount(String loginName) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("delete from account where login_name = ?;");)
        {
            stmt.setString(1, loginName);
            stmt.executeUpdate();
        }
    }

    /**
     * Create a new project.
     *
     * @param entryKey Unique key a user is required to know to join a project.
     * @param name Name of the project.
     * @param owner Name of the admin who owns the project.
     * @throws SQLException on database error.
     */

    public void insertProject(String entryKey, String name, String owner) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("insert into project(entry_key, name, owner) values (?, ?, ?);"))
        {
            stmt.setString(1, entryKey);
            stmt.setString(2, name);
            stmt.setString(3, owner);
            stmt.execute();
        }
    }

    /**
     * Updates an existing project.
     *
     * @param entryKey Unique key a user is required to know to join a project.
     * @param name Name of the project.
     * @param owner Name of the admin who owns the project.
     * @param finished Mark the project as finished or not (finished projects are read-only and not visible to users).
     * @throws SQLException on database error.
     */

    public void updateProject(String entryKey, String name, String owner, boolean finished) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement(
                "update project set name = ?, owner = ?, finished = ? where entry_key = ?;"))
        {
            stmt.setString(1, name);
            stmt.setString(2, owner);
            stmt.setBoolean(3, finished);
            stmt.setString(4, entryKey);

            stmt.executeUpdate();
        }
    }

    /**
     * Checks if a project with given key exists.
     *
     * @param entryKey Unique project key.
     * @return true if project exists, false if not.
     * @throws SQLException on database error.
     */

    public boolean isProject(String entryKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select count(entry_key) from project where entry_key = ?;"))
        {
            stmt.setString(1, entryKey);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                int count = rs.getInt(1);

                if (count == 1) return true;
            }
        }

        return false;
    }

    /**
     * Get project's details.
     *
     * @param entryKey Unique project key.
     * @return A map containing the project's details.
     * @throws SQLException on database error.
     */

    public Map<String, String> getProject(String entryKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select entry_key, name, owner, finished from project where entry_key = ?;"))
        {
            stmt.setString(1, entryKey);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();

                Map<String, String> result = new HashMap<>(4);
                result.put("entryKey", rs.getString(1));
                result.put("name", rs.getString(2));
                result.put("owner", rs.getString(3));
                result.put("finished", String.valueOf(rs.getBoolean(4)));

                return result;
            }
        }
    }

    /**
     * Returns all projects owned by an specific admin.
     *
     * @param owner The account name of the admin.
     * @return List of maps containing each project's details.
     * @throws SQLException on database error.
     */

    public List<Map<String, String>> listProjects(String owner) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select entry_key, name, owner, finished from project where owner = ?;"))
        {
            stmt.setString(1, owner);

            try (ResultSet rs = stmt.executeQuery())
            {
                List<Map<String, String>> result = new LinkedList<>();

                while (rs.next())
                {
                    Map<String, String> row = new HashMap<>(4);
                    row.put("entryKey", rs.getString(1));
                    row.put("name", rs.getString(2));
                    row.put("owner", rs.getString(3));
                    row.put("finished", String.valueOf(rs.getBoolean(4)));

                    result.add(row);
                }

                return result;
            }
        }
    }

    /**
     * Returns all projects a specific user has joined.
     *
     * @param user The account name of the user.
     * @return List of maps containing each project's details.
     * @throws SQLException on database error.
     */

    public List<Map<String, String>> listJoinedProjects(String user) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select p.entry_key, p.name, p.owner, p.finished from project p join membership m on p.entry_key = m.project_key where m.login_name = ?;"))
        {
            stmt.setString(1, user);

            try (ResultSet rs = stmt.executeQuery())
            {
                List<Map<String, String>> result = new LinkedList<>();

                while (rs.next())
                {
                    Map<String, String> row = new HashMap<>(4);
                    row.put("entryKey", rs.getString(1));
                    row.put("name", rs.getString(2));
                    row.put("owner", rs.getString(3));
                    row.put("finished", String.valueOf(rs.getBoolean(4)));

                    result.add(row);
                }

                return result;
            }
        }
    }

    /**
     * Delete a project.
     *
     * Warning: Deleting a project will remove dependent data (tickets, chat messages...).
     *
     * @param entryKey Unique project key.
     * @throws SQLException on database error.
     */

    public void deleteProject(String entryKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("delete from project where entry_key = ?;"))
        {
            stmt.setString(1, entryKey);
            stmt.executeUpdate();
        }
    }

    /**
     * Returns all users that have joined a specific project.
     *
     * @param projectKey Unique key of project.
     * @return List of maps containing each user's details.
     * @throws SQLException on database error.
     */

    public List<Map<String, String>> getUsersOfProject(String projectKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement(
                "select u.login_name, first_name, last_name, phone_number from only user_account u join membership m on u.login_name = m.login_name where m.project_key = ?;"))
        {
            stmt.setString(1, projectKey);

            try (ResultSet rs = stmt.executeQuery())
            {
                List<Map<String, String>> result = new LinkedList<>();

                while (rs.next())
                {
                    Map<String, String> row = new HashMap<>(4);
                    row.put("loginName", rs.getString(1));
                    row.put("firstName", rs.getString(2));
                    row.put("lastName", rs.getString(3));
                    row.put("phoneNumber", rs.getString(4));

                    result.add(row);
                }

                return result;
            }
        }
    }

    /**
     * Checks if a user is member of project.
     *
     * @param loginName The account name of the user.
     * @param projectKey Unique key of project.
     * @return true if user is member of project, false if not.
     * @throws SQLException on database error.
     */

    public boolean isUserMemberOfProject(String loginName, String projectKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select count(*) from membership where login_name = ? and project_key = ?;"))
        {
            stmt.setString(1, loginName);
            stmt.setString(2, projectKey);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                int count = rs.getInt(1);

                if (count == 1) return true;
            }
        }

        return false;
    }

    /**
     * Checks if an admin is owner of project.
     *
     * @param loginName The account name of the admin.
     * @param projectKey Unique key of project.
     * @return true if admin is owner of project, false if not.
     * @throws SQLException on database error.
     */

    public boolean isAdminOwnerOfProject(String loginName, String projectKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select count(*) from project where owner = ? and entry_key = ?;"))
        {
            stmt.setString(1, loginName);
            stmt.setString(2, projectKey);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                int count = rs.getInt(1);

                if (count == 1) return true;
            }
        }

        return false;
    }

    /**
     * Join a project.
     *
     * @param loginName The account name of the user who wants to join.
     * @param entryKey Unique key of project.
     * @throws SQLException on database error.
     */

    public void joinProject(String loginName, String entryKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("insert into membership values (?, ?);"))
        {
            stmt.setString(1, entryKey);
            stmt.setString(2, loginName);

            stmt.executeUpdate();
        }
    }

    /**
     * Leave a project.
     *
     * @param loginName The account name of the user who wants to leave.
     * @param entryKey Unique key of project.
     * @throws SQLException on database error.
     */

    public void leaveProject(String loginName, String entryKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("delete from membership where login_name = ? and project_key = ?;"))
        {
            stmt.setString(1, loginName);
            stmt.setString(2, entryKey);

            stmt.executeUpdate();
        }
    }

    /**
     * Creates a new ticket.
     *
     * @param name Name of ticket.
     * @param summary Ticket summary.
     * @param description Ticket description.
     * @param category Ticket category.
     * @param requiredObservations Number of required observations until a ticket is finished.
     * @param projectKey Unique key of project the ticket relates to.
     * @throws SQLException on database error.
     */

    public void insertTicket(String name, String summary, String description, String category, int requiredObservations, String projectKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("insert into ticket(name, summary, description, category, required_obversations, project_key) values (?, ?, ?, cast(? as ticket_category), ?, ?);"))
        {
            stmt.setString(1, name);
            stmt.setString(2, summary);
            stmt.setString(3, description);
            stmt.setString(4, category);
            stmt.setInt(5, requiredObservations);
            stmt.setString(6, projectKey);

            stmt.executeUpdate();
        }
    }

    /**
     * Updates an existing ticket.
     *
     * @param id Unique id of ticket.
     * @param name Name of ticket.
     * @param summary Ticket summary.
     * @param description Ticket description.
     * @param category Ticket category.
     * @param requiredObservations Number of required observations until a ticket is finished.
     * @throws SQLException on database error.
     */

    public void updateTicket(int id, String name, String summary, String description, String category, int requiredObservations) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement(
                "update ticket set name = ?, summary = ?, description = ?, category = cast(? as ticket_category), required_obversations = ? where id = ?;"))
        {
            stmt.setString(1, name);
            stmt.setString(2, summary);
            stmt.setString(3, description);
            stmt.setString(4, category);
            stmt.setInt(5, requiredObservations);
            stmt.setInt(6, id);

            stmt.executeUpdate();
        }
    }

    /**
     * Checks if ticket id is valid.
     *
     * @param id Unique id of ticket.
     * @return true if valid, false if invalid.
     * @throws SQLException on database error.
     */

    public boolean isTicket(int id) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select count(id) from ticket where id = ?;");)
        {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                int count = rs.getInt(1);

                if (count == 1) return true;
            }
        }

        return false;
    }

    /**
     * Get ticket details.
     *
     * @param id Unique id of ticket.
     * @return A map containing the tickets's details.
     * @throws SQLException on database error.
     */

    public Map<String, String> getTicket(int id) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select id, name, summary, description, category, status, required_obversations, project_key from ticket where id = ?;"))
        {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();

                Map<String, String> result = new HashMap<>(12);
                result.put("id", rs.getString(1));
                result.put("name", rs.getString(2));
                result.put("summary", rs.getString(3));
                result.put("description", rs.getString(4));
                result.put("category", rs.getString(5));
                result.put("status", rs.getString(6));
                result.put("requiredObservations", String.valueOf(rs.getInt(7)));
                result.put("projectKey", rs.getString(8));

                result.put("U", String.valueOf(acceptanceCount(id)));
                result.put("UP", String.valueOf(userCountWithPositiveObservations(id)));
                result.put("OP", String.valueOf(positiveObservationCount(id)));
                result.put("ON", String.valueOf(negativeObservationCount(id)));

                return result;
            }
        }
    }

    /**
     * Gets a lists of all tickets related to a project.
     *
     * @param projectKey Unique project key.
     * @return List of maps containing each ticket's details.
     * @throws SQLException on database error.
     */

    public List<Map<String, String>> getTicketsOfProject(String projectKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement(
                "select id, name, summary, description, category, status, required_obversations from ticket where project_key = ?;"))
        {
            stmt.setString(1, projectKey);

            try (ResultSet rs = stmt.executeQuery())
            {
                List<Map<String, String>> result = new LinkedList<>();

                while (rs.next())
                {
                    Map<String, String> row = new HashMap<>(8);
                    int id = rs.getInt(1);

                    row.put("id", String.valueOf(id));
                    row.put("name", rs.getString(2));
                    row.put("summary", rs.getString(3));
                    row.put("description", rs.getString(4));
                    row.put("category", rs.getString(5));
                    row.put("status", rs.getString(6));
                    row.put("requiredObservations", String.valueOf(rs.getInt(7)));
                    row.put("projectKey", projectKey);

                    row.put("U", String.valueOf(acceptanceCount(id)));
                    row.put("UP", String.valueOf(userCountWithPositiveObservations(id)));
                    row.put("OP", String.valueOf(positiveObservationCount(id)));
                    row.put("ON", String.valueOf(negativeObservationCount(id)));

                    result.add(row);
                }

                return result;
            }
        }
    }

    /**
     * Deletes a ticket.
     *
     * @param id Unique ticket id.
     * @throws SQLException on database error.
     */

    public void deleteTicket(int id) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("delete from ticket where id = ?;"))
        {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Accept a ticket.
     *
     * @param loginName The account name of the user wishing to accept.
     * @param id Unique ticket id.
     * @throws SQLException on database error.
     */

    public void acceptTicket(String loginName, int id) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("insert into assignment values (?, ?);"))
        {
            stmt.setInt(1, id);
            stmt.setString(2, loginName);

            stmt.executeUpdate();
        }
    }

    /**
     * Checks if a user has accepted the ticket.
     *
     * @param loginName The account name of the user.
     * @param id Unique ticket id.
     * @return true if user has accepted ticket, false if not.
     * @throws SQLException on database error.
     */

    public boolean hasUserAcceptedTicket(String loginName, int id) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select count(*) from assignment where login_name = ? and ticket_id = ?;"))
        {
            stmt.setString(1, loginName);
            stmt.setInt(2, id);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                int count = rs.getInt(1);

                if (count == 1) return true;
            }
        }

        return false;
    }

    /**
     * The number of observations a user has submitted about a ticket.
     *
     * @param loginName The account name of the user.
     * @param ticketId Unique ticket id.
     * @return Number of observations.
     * @throws SQLException on database error.
     */

    public int observationCount(String loginName, int ticketId) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select count(*) from observation where login_name = ? and ticket_id = ?;"))
        {
            stmt.setString(1, loginName);
            stmt.setInt(2, ticketId);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private int acceptanceCount(int ticketId) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select count(*) from assignment where ticket_id = ?;"))
        {
            stmt.setInt(1, ticketId);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private int userCountWithPositiveObservations(int ticketId) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select count(*) from (select distinct login_name from observation where ticket_id = ? and outcome = 'positive') as users;"))
        {
            stmt.setInt(1, ticketId);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private int positiveObservationCount(int ticketId) throws SQLException
    {
        try (Connection connection = ds.getConnection();
             PreparedStatement stmt = connection.prepareStatement("select sum(quantity) from observation where ticket_id = ? and outcome = 'positive';"))
        {
            stmt.setInt(1, ticketId);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private int negativeObservationCount(int ticketId) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select sum(quantity) from observation where ticket_id = ? and outcome = 'negative';"))
        {
            stmt.setInt(1, ticketId);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /**
     * Submit an obervation.
     *
     * @param loginName User that submits the observation.
     * @param ticketId Ticket the observation relates to.
     * @param outcome 'positive' or 'negative'
     * @param quantity How many time the observations has been made.
     * @throws SQLException on database error.
     */

    public void submitObservation(String loginName, int ticketId, String outcome, int quantity) throws SQLException
    {
        int quantity_sum;
        int required_observations;

        try (PreparedStatement stmt = cn.prepareStatement("insert into observation(ticket_id, login_name, outcome, quantity) values (?, ?, cast(? as observation_outcome), ?);"))
        {
            stmt.setInt(1, ticketId);
            stmt.setString(2, loginName);
            stmt.setString(3, outcome);
            stmt.setInt(4, quantity);

            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = cn.prepareStatement("select sum(quantity) from observation where ticket_id = ?;"))
        {
            stmt.setInt(1, ticketId);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                quantity_sum = rs.getInt(1);
            }
        }

        try (PreparedStatement stmt = cn.prepareStatement("select required_obversations from ticket where id = ?"))
        {
            stmt.setInt(1, ticketId);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                required_observations = rs.getInt(1);
            }

        }

        if (quantity_sum >= required_observations)
        {
            try (PreparedStatement stmt = cn.prepareStatement("update ticket set status = 'finished' where id = ?"))
            {
                stmt.setInt(1, ticketId);
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Returns all observations of one ticket.
     *
     * @param ticketId Unique ticket id.
     * @return List of maps containing each observation's details.
     * @throws SQLException on database error.
     */

    public List<Map<String, String>> listObservations(int ticketId) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select id, login_name, outcome, quantity from observation where ticket_id = ?;"))
        {
            List<Map<String, String>> result = new LinkedList<>();

            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                Map<String, String> row = new HashMap<>(4);
                row.put("id", String.valueOf(rs.getInt(1)));
                row.put("login_name", rs.getString(2));
                row.put("outcome", rs.getString(3));
                row.put("quantity", rs.getString(4));

                result.add(row);
            }

            return result;
        }
    }

    /**
     * Sends a chat message.
     *
     * @param sender Name of sender (user or admin)
     * @param content Message text.
     * @param attachment File name, must be present on external file server already.
     * @param ticketId Unique ticket id.
     * @throws SQLException on database error.
     */

    public void sendMessage(String sender, String content, String attachment, int ticketId) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("insert into message(sender, content, attachment, ticket_id) values (?, ?, ?, ?);"))
        {
            stmt.setString(1, sender);
            stmt.setString(2, content);
            stmt.setString(3, attachment);
            stmt.setInt(4, ticketId);

            stmt.executeUpdate();
        }

        try (Statement stmt = cn.createStatement())
        {
            stmt.execute(String.format("notify ticket_%d;", ticketId));
        }
    }

    /**
     * Returns all messages of one ticket.
     *
     * @param ticketId Unique ticket id.
     * @return List of maps containing each message's details.
     * @throws SQLException on database error.
     */

    public List<Map<String, String>> listMessages(int ticketId) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement(
                     "select id, sender, timestamp, content, attachment, ticket_id from message where ticket_id = ?;"))
        {
            stmt.setInt(1, ticketId);

            try (ResultSet rs = stmt.executeQuery())
            {
                List<Map<String, String>> result = new LinkedList<>();

                while (rs.next())
                {
                    Map<String, String> row = new HashMap<>(4);
                    row.put("id", String.valueOf(rs.getInt(1)));
                    row.put("sender", rs.getString(2));
                    row.put("timestamp", String.valueOf(rs.getTimestamp(3).getTime()));
                    row.put("content", rs.getString(4));
                    row.put("attachment", rs.getString(5));
                    result.add(row);
                }

                return result;
            }
        }
    }

    /**
     * Waits until new messages are available for ticket
     * and then returns them.
     *
     * @param ticketId Unique ticket id.
     * @return List of maps containing each message's details.
     * @throws SQLException on database error.
     */

    public List<Map<String, String>> listenChannel(int ticketId) throws Exception
    {
        try (Statement stmt = cn.createStatement())
        {
            stmt.execute(String.format("listen ticket_%d", ticketId));
        }

        final PGConnection pgcn = cn.unwrap(PGConnection.class);
        PGNotification[] notifications;

        do
        {
            notifications = pgcn.getNotifications();
            Thread.sleep(500);
        }
        while (notifications == null);

        for (PGNotification notification : notifications)
        {
            System.err.println("Received chat notification for ticket channel: " + notification.getName());
        }

        try (Statement stmt = cn.createStatement())
        {
            stmt.execute(String.format("unlisten ticket_%d", ticketId));
        }

        return listMessages(ticketId);
    }
}
