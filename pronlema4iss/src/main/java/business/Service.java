package business;

import business.observer.Observable;
import business.observer.Observer;
import domain.Worker;
import exceptions.ProgramException;
import exceptions.ValidationException;
import repository.WorkerRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Service implements Observable {
    private final WorkerRepository workerRepo;
    private final Set<Observer> observers;

    public Service(WorkerRepository workerRepo) {
        this.workerRepo = workerRepo;
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
        observers.forEach(Observer::close);
    }
}
