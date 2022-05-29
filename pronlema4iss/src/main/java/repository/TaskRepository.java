package repository;

import domain.Task;
import exceptions.RepoException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class TaskRepository {
    private final SessionFactory sf;

    public TaskRepository(SessionFactory sf) {
        this.sf = sf;
    }

    public Long save(Task task) {
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            session.save(task);
            tx.commit();
        } catch (Exception ex) {
            System.out.println("save: " + ex.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            throw new RepoException("Error executing task: " + ex.getMessage(), ex);
        }
        return task.getId();
    }

    public void delete(Long taskId) {
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            Task t = session.load(Task.class, taskId);
            session.remove(t);
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

    public void update(Task task) {
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            Task loaded = session.load(Task.class, task.getId());

            if(task.getName() != null) loaded.setName(task.getName());
            if(task.getDeadline() != null) loaded.setDeadline(task.getDeadline());
            if(task.getDescription() != null) loaded.setDescription(task.getDescription());
            if(task.getFinished() != null) loaded.setFinished(task.getFinished());

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

    public List<Task> findAll() {
        List<Task> rez;
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            rez = session.createQuery("from Task as t", Task.class).list();
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
        return rez;
    }

    public List<Task> findOfEmployee(String username) {
        List<Task> rez;
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            rez = session.createQuery("from Task as t where t.worker.username = :nam and t.finished = false", Task.class).
                    setParameter("nam", username).
                    list();
            tx.commit();
        } catch (Exception ex) {
            System.out.println("findOfEmployee: " + ex.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            throw new RepoException("Error executing findOfEmployee: " + ex.getMessage(), ex);
        }
        return rez;
    }

    public List<Task> findOfManager(String username) {
        List<Task> rez;
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            rez = session.createQuery("from Task as t where t.worker.username = :nam", Task.class).
                    setParameter("nam", username).
                    list();
            tx.commit();
        } catch (Exception ex) {
            System.out.println("findOfManager: " + ex.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            throw new RepoException("Error executing findOfManager: " + ex.getMessage(), ex);
        }
        return rez;
    }
}
