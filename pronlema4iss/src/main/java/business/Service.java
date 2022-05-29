package business;

import business.observer.Observable;
import business.observer.Observer;
import domain.Problem;
import domain.Task;
import domain.Worker;
import exceptions.ProgramException;
import exceptions.ValidationException;
import repository.ProblemRepository;
import repository.TaskRepository;
import repository.WorkerRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Service implements Observable {
    private final WorkerRepository workerRepo;
    private final TaskRepository taskRepo;
    private final ProblemRepository problemRepo;
    private final Set<Observer> observers;

    public Service(WorkerRepository workerRepo, TaskRepository taskRepo, ProblemRepository problemRepo) {
        this.workerRepo = workerRepo;
        this.taskRepo = taskRepo;
        this.problemRepo = problemRepo;
        this.observers = new HashSet<>();
    }

    private void validateAddWorker(Worker worker) {
        String err = "";
        if (worker == null)
            err += "Worker cannot be null";
        if(!err.isEmpty())
            throw new ValidationException(err);
        if (worker.getUsername() == null)
            err += "Worker username must not be null\n";
        if (worker.getName() == null)
            err += "Worker name must not be null\n";
        if (worker.getRole() == null)
            err += "Worker role must not be null\n";

        if(!err.isEmpty())
            throw new ValidationException(err);

        if (worker.getUsername().isEmpty())
            err += "Worker username must not be empty\n";
        if (worker.getName().isEmpty())
            err += "Worker name must not be empty\n";
        if (worker.getRole().isEmpty())
            err += "Worker role must not be empty\n";
        if (worker.getPassword() == null || worker.getPassword().isEmpty())
            err += "Worker password must not be empty\n";

        if (!err.isEmpty())
            throw new ValidationException(err);
    }

    private void validateUpdateWorker(Worker worker) {
        if (worker == null)
            throw new ValidationException("Worker cannot be null");
        if (worker.getUsername() != null && worker.getUsername().isEmpty())
            throw new ValidationException("Worker username must not be empty\n");
    }

    private void validateNullTask(Task task) {
        if(task == null) throw new ValidationException("Task cannot be null");
        if(task.getName() == null) throw new ValidationException("Task name cannot be null");
        if(task.getDescription() == null) throw new ValidationException("Task description cannot be null");
        if(task.getDeadline() == null) throw new ValidationException("Task deadline cannot be null");
        if(task.getFinished() == null) throw new ValidationException("Task must have finish state set");
        if(task.getWorker() == null) throw new ValidationException("Task worker cannot be null");
        if(task.getManager() == null) throw new ValidationException("Task manager cannot be null");
    }

    private void validateTask(Task task) {
        String err = "";
        if(task.getName().isEmpty()) err += "Name cannot be empty\n";
        if(task.getDescription().isEmpty()) err += "Description cannot be empty\n";

        if(!err.isEmpty()) throw new ValidationException(err);
    }

    private void validateNullProblem(Problem problem) {
        if(problem == null) throw new ValidationException("Problem cannot be null");
        if(problem.getDescription() == null) throw new ValidationException("Problem description cannot be null");
        if(problem.getTask() == null) throw new ValidationException("Problem task cannot be null");
    }

    private void validateProblem(Problem problem) {
        if(problem.getDescription().isEmpty()) throw new ValidationException("Description cannot be empty");
    }

    public Worker login(String username, String password) {
        if(username == null || password == null || username.isEmpty() || password.isEmpty())
            return null;
        Worker logged = workerRepo.findByCredentials(username, password);
        notifyAllObservers();
        return logged;
    }

    public void logout(String username, String password) {
        if(username == null || password == null || username.isEmpty() || password.isEmpty())
            throw new ProgramException("invalid credentials");
        workerRepo.setOffline(username, password);
    }

    public void addWorker(Worker worker) {
        validateAddWorker(worker);
        workerRepo.save(worker);
        notifyAllObservers();
    }

    public void updateWorker(Worker worker) {
        validateUpdateWorker(worker);
        if (worker.getName() != null && worker.getName().isEmpty())
            worker.setName(null);
        if (worker.getRole() != null && worker.getRole().isEmpty())
            worker.setRole(null);
        if (worker.getPassword() != null && worker.getPassword().isEmpty())
            worker.setPassword(null);
        workerRepo.update(worker);
        notifyAllObservers();
    }

    public void deleteWorker(String username) {
        if(username == null || username.isEmpty())
            return;
        workerRepo.delete(username);
        notifyAllObservers();
    }

    public List<Worker> getWorkers() {
        return workerRepo.findAll();
    }

    public List<Worker> getWorkersByRole(String role) {
        return workerRepo.findByRole(role);
    }

    public List<String> getRoles() {
        return workerRepo.findRoleNames();
    }

    public void finishTask(Task task) {
        Task t = new Task();
        t.setId(task.getId());
        t.setFinished(Boolean.TRUE);
        taskRepo.update(t);
        notifyAllObservers();
    }

    public void reportProblem(Problem problem) {
        problem.setDate(LocalDateTime.now());
        validateNullProblem(problem);
        validateProblem(problem);
        problemRepo.save(problem);
        notifyAllObservers();
    }

    public List<Worker> getManagerWorkers(String managerUsername) {
        return workerRepo.findManagerEmployees(managerUsername);
    }

    public List<Problem> getTaskProblems(Task task) {
        return problemRepo.findOfTask(task);
    }

    public void addTask(Task task) {
        validateNullTask(task);
        validateTask(task);
        taskRepo.save(task);
        notifyAllObservers();
    }

    public void removeTask(Task task) {
        taskRepo.delete(task.getId());
        notifyAllObservers();
    }

    public void removeProblem(Problem problem) {
        problemRepo.delete(problem.getId());
        notifyAllObservers();
    }

    public List<Task> getEmployeeTasks(String employeeUsername) {
        return taskRepo.findOfEmployee(employeeUsername);
    }

    public List<Task> getManagerTasks(String employeeUsername) {
        return taskRepo.findOfManager(employeeUsername);
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyAllObservers() {
        observers.forEach(Observer::update);
    }

    @Override
    public void notifyClose() {
        List<Observer> oldObservers = observers.stream().toList();
        oldObservers.forEach(Observer::close);
    }
}
