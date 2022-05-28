package repository;

import domain.Problem;
import domain.Task;
import exceptions.ProgramException;
import exceptions.RepoException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class ProblemRepository {
    private final SessionFactory sf;

    public ProblemRepository(SessionFactory sf) {
        this.sf = sf;
    }

    public Long save(Problem problem) {
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            session.save(problem);
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
            throw new RepoException("Error executing save: " + ex.getMessage(), ex);
        }
        return problem.getId();
    }

    public void delete(Long problemId) {
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            Problem p = session.load(Problem.class, problemId);
            session.remove(p);
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

    public List<Problem> findOfTask(Task task) {
        List<Problem> rez;
        Transaction tx = null;
        try (Session session = sf.openSession()){
            tx = session.beginTransaction();
            rez = session.createQuery("from Problem as p where p.task = :tk", Problem.class).
                    setParameter("tk", task).list();
            tx.commit();
        } catch (Exception ex) {
            System.out.println("findOfTask: " + ex.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            throw new RepoException("Error executing findOfTask: " + ex.getMessage(), ex);
        }
        return rez;
    }
}
