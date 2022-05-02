package repository;

import domain.Worker;
import exceptions.RepoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WorkerDBRepository {
    private Connection conn;
    private final Properties properties;

    /**
     * Initializes the connection
     */
    private void initializeConnection(){
        try {
            if (conn == null || conn.isClosed()) {
                String url = properties.getProperty("jdbc.url");
                String user = properties.getProperty("jdbc.user");
                String pass = properties.getProperty("jdbc.pass");

                if (user != null && pass != null) {
                    conn = DriverManager.getConnection(url, user, pass);
                } else {
                    conn = DriverManager.getConnection(url);
                }

            }
        } catch (SQLException ex) {
            System.err.println("Error getting connection " + ex);
            throw new RepoException("Error getting connection: " + ex.getMessage(), ex);
        }
    }

    public WorkerDBRepository(Properties properties) {
        this.properties = properties;
        initializeConnection();
    }

    /**
     * Saves in the DB the worker
     * @param worker, the new worker
     * @throws RepoException, if an SQL exception occurs
     */
    public void save(Worker worker) {
        String sql = "insert into workers(username, name, role, password) values (?,?,?,?);";

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, worker.getUsername());
            ps.setString(2, worker.getName());
            ps.setString(3, worker.getRole());
            ps.setString(4, worker.getPassword());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RepoException("Error executing save: " + ex.getMessage(), ex);
        }
    }

    /**
     * Updates the worker with the new data from the parameter
     * @param worker, the Worker instance that contains the new values, skips from updating null values
     * @throws RepoException, if an SQL exception occurs
     */
    public void update(Worker worker) {
        String sqlName = "update workers set name=? where username=?;";
        String sqlRole = "update workers set role=? where username=?;";
        String sqlPass = "update workers set password=? where username=?;";
        try (PreparedStatement psName = conn.prepareStatement(sqlName);
             PreparedStatement psRole = conn.prepareStatement(sqlRole);
             PreparedStatement psPass = conn.prepareStatement(sqlPass)) {
            if(worker.getName() != null) {
                psName.setString(1, worker.getName());
                psName.setString(2, worker.getUsername());
                psName.executeUpdate();
            }
            if(worker.getRole() != null) {
                psRole.setString(1, worker.getRole());
                psRole.setString(2, worker.getUsername());
                psRole.executeUpdate();
            }
            if(worker.getPassword() != null) {
                psPass.setString(1, worker.getPassword());
                psPass.setString(2, worker.getUsername());
                psPass.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RepoException("Error executing update: " + ex.getMessage(), ex);
        }
    }

    /**
     * Deletes the worker with the same username
     * @param username, the username of the worker to be deleted
     * @throws RepoException, if an SQL exception occurs
     */
    public void delete(String username) {
        String sql = "delete from workers where username=?;";

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RepoException("Error executing delete: " + ex.getMessage(), ex);
        }
    }

    /**
     * Returns a list wit all the users in DB
     * @return a List of workers
     * @throws RepoException, if an SQL exception occurs
     */
    public List<Worker> findAll() {
        String sql = "select * from workers;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            return getWorkersDataFromPreparedStatement(ps);
        } catch (SQLException ex) {
            throw new RepoException("Error executing findAll: " + ex.getMessage(), ex);
        }
    }

    /**
     * Returns the worker with the same username
     * @param username, the username of the worker to be returned
     * @return Worker, if the worker id found, null otherwise
     * @throws RepoException, if an SQL exception occurs
     */
    public Worker findOne(String username) {
        String sql = "select * from workers where username=?;";
        List<Worker> workers;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            workers = getWorkersDataFromPreparedStatement(ps);
        } catch (SQLException ex) {
            throw new RepoException("Error executing findOne: " + ex.getMessage(), ex);
        }

        if(workers.isEmpty()) {
            return null;
        } else {
            return workers.get(0);
        }
    }

    /**
     * Returns a list of workers filtered by their role
     * @param role, string the role of the workers
     * @return List of workers
     * @throws RepoException, if an SQL exception occurs
     */
    public List<Worker> findByRole(String role) {
        String sql = "select * from workers where role=?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            return getWorkersDataFromPreparedStatement(ps);
        } catch (SQLException ex) {
            throw new RepoException("Error executing findByRole: " + ex.getMessage(), ex);
        }
    }

    /**
     * Returns the worker with the same username if the password matches
     * @param username, the username of the worker
     * @param password, the password of the worker
     * @return Worker, if the worker is found and the password matches, null otherwise
     * @throws RepoException, if an SQL exception occurs
     */
    public Worker findByCredentials(String username, String password) {
        String sql = "select * from workers where username=? and password=?;";
        List<Worker> workers;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            workers = getWorkersDataFromPreparedStatement(ps);
        } catch (SQLException ex) {
            throw new RepoException("Error executing findByCredentials: " + ex.getMessage(), ex);
        }

        if(workers.isEmpty()) {
            return null;
        } else {
            return workers.get(0);
        }
    }

    /**
     * Executes the prepared statement and returns a list with all the workers contained in the result set
     * @param ps, the prepared statement to be executed, the result set must contain the columns that compose a worker, except the password
     * @return List of workers
     * @throws RepoException, if an SQL exception occurs
     */
    private List<Worker> getWorkersDataFromPreparedStatement(PreparedStatement ps) {
        List<Worker> rez = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String username = rs.getString("username");
                String name = rs.getString("name");
                String role = rs.getString("role");
                Worker worker = new Worker(username, name, role);
                rez.add(worker);
            }
        } catch (SQLException ex) {
            throw new RepoException("Error reading Worker data: " + ex.getMessage(), ex);
        }
        return rez;
    }
}
