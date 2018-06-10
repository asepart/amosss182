package de.fau.cs.osr.amos.asepart;

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

import org.postgresql.ds.PGSimpleDataSource;

class DBClient implements AutoCloseable
{
    private static DataSource createDataSource()
    {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName("localhost");
        ds.setUser("postgres");
        ds.setPassword("asepart");
        ds.setDatabaseName("asepartdb");
        ds.setApplicationName("ASEPART Web Service");

        return ds;
    }

    private static final DataSource ds = createDataSource();
    private final Connection cn;

    DBClient() throws SQLException
    {
        cn = ds.getConnection();
    }

    @Override
    public void close() throws Exception
    {
        cn.close();
    }

    void wipe() throws SQLException
    {
        try (Statement stmt = cn.createStatement())
        {
            stmt.executeUpdate("delete from account"); // will cascade through whole database
        }
    }

    boolean authenticate(String loginName, String password, String role) throws SQLException
    {
        boolean usernameAndPasswordCorrect;

        try (PreparedStatement stmt = cn.prepareStatement("select count(login_name) from account where login_name = ? and password = crypt(?, password);"))
        {
            stmt.setString(1, loginName);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                usernameAndPasswordCorrect = (rs.getInt(1) == 1);
            }
        }

        if ("Admin".equals(role))
        {
            if (usernameAndPasswordCorrect && !isAdmin(loginName))
                throw new IllegalArgumentException("Account is not an admin account.");
        }

        else if ("User".equals(role))
        {
            if (usernameAndPasswordCorrect && !isUser(loginName))
             throw new IllegalArgumentException("Account is not an user account.");
        }

        else throw new IllegalArgumentException("Unknown role: " + role);

        return usernameAndPasswordCorrect;
    }

    void insertUser(String loginName, String password, String firstName, String lastName, String phoneNumber) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("insert into user_account values (?, crypt(?, gen_salt('bf', 8)), ?, ?, ?);"))
        {
            stmt.setString(1, loginName);
            stmt.setString(2, password);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.setString(5, phoneNumber);

            stmt.execute();
        }
    }

    void updateUser(String loginName, String password, String firstName, String lastName, String phoneNumber) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement(
                "update user_account set password = crypt(?, gen_salt('bf', 8)), first_name = ?, last_name = ?, phone_number = ? where login_name = ?;"))
        {
            stmt.setString(1, password);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, phoneNumber);
            stmt.setString(5, loginName);

            stmt.executeUpdate();
        }
    }

    boolean isUser(String loginName) throws SQLException
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

    /*
    Map<String, String> getUser(String loginName) throws SQLException
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
    */

    List<Map<String, String>> listUsers() throws SQLException
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

    void insertAdmin(String loginName, String password, String firstName, String lastName) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("insert into admin_account values (?, crypt(?, gen_salt('bf', 8)), ?, ?);");)
        {
            stmt.setString(1, loginName);
            stmt.setString(2, password);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);

            stmt.execute();
        }
    }

    void updateAdmin(String loginName, String password, String firstName, String lastName) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement(
                "update admin_account set password = crypt(?, gen_salt('bf', 8)), first_name = ?, last_name = ? where login_name = ?;"))
        {
            stmt.setString(1, password);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, loginName);

            stmt.executeUpdate();
        }
    }

    boolean isAdmin(String loginName) throws SQLException
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

    /*
    Map<String, String> getAdmin(String loginName) throws SQLException
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
    */

    List<Map<String, String>> listAdmins() throws SQLException
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

    int deleteAccount(String loginName) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("delete from account where login_name = ?;");)
        {
            stmt.setString(1, loginName);
            return stmt.executeUpdate();
        }
    }

    void insertProject(String entryKey, String name, String owner) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("insert into project values (?, ?, ?);"))
        {
            stmt.setString(1, entryKey);
            stmt.setString(2, name);
            stmt.setString(3, owner);
            stmt.execute();
        }
    }

    void updateProject(String entryKey, String name, String owner) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement(
                "update project set name = ?, owner = ? where entry_key = ?;"))
        {
            stmt.setString(1, name);
            stmt.setString(2, owner);
            stmt.setString(3, entryKey);

            stmt.executeUpdate();
        }
    }

    boolean isProject(String entryKey) throws SQLException
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

    Map<String, String> getProject(String entryKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select entry_key, name, owner from project where entry_key = ?;"))
        {
            stmt.setString(1, entryKey);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();

                Map<String, String> result = new HashMap<>(3);
                result.put("entryKey", rs.getString(1));
                result.put("name", rs.getString(2));
                result.put("owner", rs.getString(3));

                return result;
            }
        }
    }

    List<Map<String, String>> listProjects(String owner) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("select entry_key, name, owner from project where owner = ?;"))
        {
            stmt.setString(1, owner);

            try (ResultSet rs = stmt.executeQuery())
            {
                List<Map<String, String>> result = new LinkedList<>();

                while (rs.next())
                {
                    Map<String, String> row = new HashMap<>(3);
                    row.put("entryKey", rs.getString(1));
                    row.put("name", rs.getString(2));
                    row.put("owner", rs.getString(3));

                    result.add(row);
                }

                return result;
            }
        }
    }

    int deleteProject(String entryKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("delete from project where entry_key = ?;"))
        {
            stmt.setString(1, entryKey);
            return stmt.executeUpdate();
        }
    }

    List<Map<String, String>> getUsersOfProject(String projectKey) throws SQLException
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

    boolean isUserMemberOfProject(String loginName, String projectKey) throws SQLException
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

    boolean isAdminOwnerOfProject(String loginName, String projectKey) throws SQLException
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

    int joinProject(String loginName, String entryKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("insert into membership values (?, ?);"))
        {
            stmt.setString(1, entryKey);
            stmt.setString(2, loginName);

            return stmt.executeUpdate();
        }
    }

    int leaveProject(String loginName, String entryKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("delete from membership where login_name = ? and project_key = ?;"))
        {
            stmt.setString(1, loginName);
            stmt.setString(2, entryKey);

            return stmt.executeUpdate();
        }
    }

    int insertTicket(String name, String summary, String description, String category, int requiredObservations, String projectKey) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("insert into ticket(name, summary, description, category, required_obversations, project_key) values (?, ?, ?, cast(? as ticket_category), ?, ?);"))
        {
            stmt.setString(1, name);
            stmt.setString(2, summary);
            stmt.setString(3, description);
            stmt.setString(4, category);
            stmt.setInt(5, requiredObservations);
            stmt.setString(6, projectKey);

            return stmt.executeUpdate();
        }
    }

    void updateTicket(int id, String name, String summary, String description, String category, int requiredObservations) throws SQLException
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

    boolean isTicket(int id) throws SQLException
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

    Map<String, String> getTicket(int id) throws SQLException
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

    /*
    List<Map<String, String>> listTickets() throws SQLException
    {
        try (Statement stmt = cn.createStatement();
             ResultSet rs = stmt.executeQuery("select id, name, summary, description, category, status, required_obversations, project_key from ticket;"))
        {
            List<Map<String, String>> result = new LinkedList<>();

            while (rs.next())
            {
                Map<String, String> row = new HashMap<>(12);
                int id = rs.getInt(1);

                row.put("id", String.valueOf(id));
                row.put("name", rs.getString(2));
                row.put("summary", rs.getString(3));
                row.put("description", rs.getString(4));
                row.put("category", rs.getString(5));
                row.put("status", rs.getString(6));
                row.put("requiredObservations", String.valueOf(rs.getInt(7)));
                row.put("projectKey", rs.getString(8));

                row.put("U", String.valueOf(acceptanceCount(id)));
                row.put("UP", String.valueOf(userCountWithPositiveObservations(id)));
                row.put("OP", String.valueOf(positiveObservationCount(id)));
                row.put("ON", String.valueOf(negativeObservationCount(id)));

                result.add(row);
            }

            return result;
        }
    }
    */

    List<Map<String, String>> getTicketsOfProject(String projectKey) throws SQLException
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

    int deleteTicket(int id) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("delete from ticket where id = ?;"))
        {
            stmt.setInt(1, id);
            return stmt.executeUpdate();
        }
    }

    int acceptTicket(String loginName, int id) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("insert into assignment values (?, ?);"))
        {
            stmt.setInt(1, id);
            stmt.setString(2, loginName);

            return stmt.executeUpdate();
        }
    }

    boolean hasUserAcceptedTicket(String loginName, int id) throws SQLException
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

    int observationCount(String loginName, int ticketId) throws SQLException
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
             PreparedStatement stmt = connection.prepareStatement("select count(*) from observation where ticket_id = ? and outcome = 'positive';"))
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
        try (PreparedStatement stmt = cn.prepareStatement("select count(*) from observation where ticket_id = ? and outcome = 'negative';"))
        {
            stmt.setInt(1, ticketId);

            try (ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    void submitObservation(String loginName, int ticketId, String outcome, int quantity) throws SQLException
    {
        int quantity_sum = 0;
        int required_observations = -1;

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

    List<Map<String, String>> listObservations(int ticketId) throws SQLException
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

    int sendMessage(String sender, String content, int ticketId) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement("insert into message(sender, content, ticket_id) values (?, ?, ?);"))
        {
            stmt.setString(1, sender);
            stmt.setString(2, content);
            stmt.setInt(3, ticketId);

            return stmt.executeUpdate();
        }
    }

    List<Map<String, String>> listMessages(int ticketId) throws SQLException
    {
        try (PreparedStatement stmt = cn.prepareStatement(
                     "select id, sender, content, ticket_id from message where ticket_id = ?;"))
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
                    row.put("content", rs.getString(3));
                    row.put("ticketId", String.valueOf(rs.getInt(4)));
                    result.add(row);
                }

                return result;
            }
        }
    }
}
