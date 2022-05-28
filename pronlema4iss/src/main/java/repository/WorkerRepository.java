package repository;

import domain.Role;
import domain.Worker;
import exceptions.ProgramException;
import exceptions.RepoException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkerRepository {
    private final SessionFactory sf;

    public WorkerRepository(SessionFactory sessionFactory) {
        this.sf = sessionFactory;
    }

    /**
     * Saves in the DB the worker
     * @param worker, the new worker
     * @throws RepoException, if an SQL exception occurs
     */
    public String save(Worker worker) {
        String id;
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            session.save(worker);
            tx.commit();
            id = worker.getUsername();
        } catch (Exception ex) {
            System.out.println("save: " + ex.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            throw new RepoException("Error executing save: " + ex.getMessage(), ex);
        }
        return id;
    }

    /**
     * Updates the worker with the new data from the parameter
     * @param worker, the Worker instance that contains the new values, skips from updating null values
     * @throws RepoException, if an SQL exception occurs
     */
    public void update(Worker worker) {
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            Worker loaded = session.load(Worker.class, worker.getUsername());
            if(worker.getName() != null) loaded.setName(worker.getName());
            if(worker.getRole() != null) loaded.setRole(worker.getRole());
            if(worker.getPassword() != null) loaded.setPassword(worker.getPassword());
            if(worker.getManager() != null) loaded.setManager(worker.getManager());
            tx.commit();
        } catch (Exception ex) {
            System.out.println("update: " + ex.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            throw new RepoException("Error executing update: " + ex.getMessage(), ex);
        }
    }

    /**
     * Deletes the worker with the same username
     * @param username, the username of the worker to be deleted
     * @throws RepoException, if an SQL exception occurs
     */
    public void delete(String username) {
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            Worker loaded = session.load(Worker.class, username);
            session.remove(loaded);
            tx.commit();
        } catch (Exception ex) {
            System.out.println("delete: " + ex.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            throw new RepoException("Error executing delete: " + ex.getMessage(), ex);
        }
    }

    /**
     * Returns a list wit all the users in DB
     * @return a List of workers
     * @throws RepoException, if an SQL exception occurs
     */
    public List<Worker> findAll() {
        List<Worker> rez;
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            rez = session.createQuery("from Worker as w", Worker.class).list();
            tx.commit();
        } catch (Exception ex) {
            System.out.println("findAll: " + ex.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            throw new RepoException("Error executing findAll: " + ex.getMessage(), ex);
        }
        return hidePass(rez);
    }

    /**
     * Returns the worker with the same username
     * @param username, the username of the worker to be returned
     * @return Worker, if the worker id found, null otherwise
     * @throws RepoException, if an SQL exception occurs
     */
    public Worker findOne(String username) {
        Worker rez;
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            rez = session.createQuery("from Worker as w where w.username = :usr", Worker.class).
                    setParameter("usr", username).
                    getSingleResult();
            tx.commit();
        } catch (Exception ex) {
            System.out.println("findOne: " + ex.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            throw new RepoException("Error executing findOne: " + ex.getMessage(), ex);
        }
        return hidePass(rez);
    }

    /**
     * Returns a list of workers filtered by their role
     * @param role, string the role of the workers
     * @return List of workers
     * @throws RepoException, if an SQL exception occurs
     */
    public List<Worker> findByRole(String role) {
        List<Worker> rez;
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            rez = session.createQuery("from Worker as w where w.role = :role", Worker.class).
                    setParameter("role", role).
                    list();
            tx.commit();
        } catch (Exception ex) {
            System.out.println("findByRole: " + ex.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            throw new RepoException("Error executing findByRole: " + ex.getMessage(), ex);
        }
        return hidePass(rez);
    }

    /**
     * Returns the worker with the same username if the password matches
     * @param username, the username of the worker
     * @param password, the password of the worker
     * @return Worker, if the worker is found and the password matches, null otherwise
     * @throws RepoException, if an SQL exception occurs
     */
    public Worker findByCredentials(String username, String password) {
        Worker rez = null;
        Transaction tx = null;
        boolean ok = false;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            List<Worker> workers = session.createQuery("from Worker as w where w.username = :usr and w.password = :pas", Worker.class).
                    setParameter("usr", username).
                    setParameter("pas", password).
                    list();
            if(!workers.isEmpty()) {
                if(!workers.get(0).getLogged()) {
                    workers.get(0).setLastLogin(LocalDateTime.now());
                    workers.get(0).setLogged(true);
                    rez = workers.get(0);
                    ok = true;
                }
            }
            tx.commit();
        } catch (Exception ex) {
            System.out.println("findByCredentials: " + ex.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            throw new RepoException("Error executing findByCredentials: " + ex.getMessage(), ex);
        }
        if(!ok) throw new RepoException("User already logged");
        return hidePass(rez);
    }

    public void setOffline(String username, String password) {
        Transaction tx = null;
        boolean ok = false;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            List<Worker> workers = session.createQuery("from Worker as w where w.username = :usr and w.password = :pas", Worker.class).
                    setParameter("usr", username).
                    setParameter("pas", password).
                    list();
            if(!workers.isEmpty()) {
                workers.get(0).setLogged(false);
                ok = true;
            }
            tx.commit();
        } catch (Exception ex) {
            System.out.println("findByCredentials: " + ex.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            throw new RepoException("Error executing findByCredentials: " + ex.getMessage(), ex);
        }

        if(!ok) {
            throw new RepoException("invalid credentials");
        }
    }

    public List<String> findRoleNames() {
        List<String> rez = new ArrayList<>();

        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            List<Role> roles = session.createQuery("from Role", Role.class).list();
            roles.forEach(role -> rez.add(role.getRoleName()));
            tx.commit();
        } catch (Exception ex) {
            System.out.println("findRoleNames: " + ex.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            throw new RepoException("Error executing FindRoleNames: " + ex.getMessage(), ex);
        }

        return rez;
    }

    public List<Worker> findManagerEmployees(String managerUsername) {
        List<Worker> rez;
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            rez = session.createQuery("from Worker as w where w.manager.username = :name", Worker.class).
                    setParameter("name", managerUsername).
                    list();
            tx.commit();
        } catch (Exception ex) {
            System.out.println("findManagerEmployees: " + ex.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            throw new RepoException("Error executing findManagerEmployees: " + ex.getMessage(), ex);
        }
        return hidePass(rez);
    }

    private Worker hidePass(Worker worker) {
        if(worker == null) return null;
        worker.setPassword("***");
        return worker;
    }

    private List<Worker> hidePass(List<Worker> workers) {
        if(workers == null) return null;
        workers.forEach(w -> w.setPassword("***"));
        return workers;
    }
}
