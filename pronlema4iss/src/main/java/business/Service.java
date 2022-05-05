package business;

import domain.Worker;
import exceptions.ValidationException;
import repository.WorkerDBRepository;

import java.util.List;

public class Service {
    private final WorkerDBRepository workerRepo;

    public Service(WorkerDBRepository workerRepo) {
        this.workerRepo = workerRepo;
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
        return workerRepo.findByCredentials(username, password);
    }

    public void addWorker(Worker worker) {
        validateAddWorker(worker);
        workerRepo.save(worker);
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
    }

    public void deleteWorker(String username) {
        if(username == null || username.isEmpty())
            return;
        workerRepo.delete(username);
    }

    public List<Worker> getWorkers() {
        return workerRepo.findAll();
    }

    public List<String> getRoles() {
        return workerRepo.findRoleNames();
    }
}
