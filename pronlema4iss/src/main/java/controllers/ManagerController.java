package controllers;

import business.Service;
import business.observer.Observer;
import domain.Problem;
import domain.Task;
import domain.Worker;
import exceptions.ProgramException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ManagerController implements Observer {
    public TableView<Worker> employeesTable;
    public TableColumn<Worker, String> employeeUsernameColumn;
    public TableColumn<Worker, String> employeeOnlineStatus;
    public TableColumn<Worker, String> employeeLastLoginColumn;
    public TableColumn<Worker, String> employeeNameColumn;

    private final ObservableList<Worker> workers = FXCollections.observableArrayList();

    public TextField taskNameField;
    public TextArea descriptionField;
    public DatePicker dateField;

    public TableView<Task> tasksTable;
    public TableColumn<Task, String> taskNameColumn;
    public TableColumn<Task, String> taskStatusColumn;
    public TableColumn<Task, String> taskDeadlineColumn;

    private final ObservableList<Task> workerTasks = FXCollections.observableArrayList();

    public TableView<Problem> problemsTable;
    public TableColumn<Problem, String> problemDateColumn;
    public TableColumn<Problem, String> problemDescriptionColumn;

    private final ObservableList<Problem> taskProblems = FXCollections.observableArrayList();

    private Worker logged;
    private Worker selectedWorker;
    private Task selectedTask;
    private Problem selectedProblem;
    private Service service;
    private Stage selfStage, previousStage;

    public void setLogged(Worker logged) {
        this.logged = logged;
    }

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
    }

    public void initialize() {
        employeesTable.setItems(workers);
        employeeUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("Username"));
        employeeOnlineStatus.setCellValueFactory(data -> {
            boolean logged = data.getValue().getLogged();
            if(logged) return new SimpleStringProperty("Online");
            return new SimpleStringProperty("Offline");
        });
        employeeLastLoginColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastLogin().format(DateTimeFormatter.ofPattern("yy/MM/dd hh:mm"))));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));

        employeesTable.setRowFactory(tv -> {
            TableRow<Worker> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (!row.isEmpty() && ev.getButton() == MouseButton.PRIMARY && ev.getClickCount() == 1) {
                    selectedWorker = row.getItem();
                    loadEmployeeTasks();
                }
            });
            return row;
        });

        tasksTable.setItems(workerTasks);
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        taskStatusColumn.setCellValueFactory(data -> {
            boolean finished = data.getValue().getFinished();
            if(finished) return new SimpleStringProperty("Finished");
            return new SimpleStringProperty("In progress");
        });
        taskDeadlineColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDeadline().format(DateTimeFormatter.ofPattern("yy/MM/dd hh:mm"))));

        tasksTable.setRowFactory(tv -> {
            TableRow<Task> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (!row.isEmpty() && ev.getButton() == MouseButton.PRIMARY && ev.getClickCount() == 1) {
                    selectedTask = row.getItem();
                    loadTaskProblems();
                }
            });
            return row;
        });

        problemsTable.setItems(taskProblems);
        problemDateColumn.setCellValueFactory(new PropertyValueFactory<>("Date"));
        problemDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Description"));

        problemsTable.setRowFactory(tv -> {
            TableRow<Problem> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (!row.isEmpty() && ev.getButton() == MouseButton.PRIMARY && ev.getClickCount() == 1) {
                    selectedProblem = row.getItem();
                    loadProblem();
                }
            });
            return row;
        });
    }

    private void loadEmployeeTasks() {
        if(selectedWorker != null) {
            try {
                workerTasks.clear();
                workerTasks.addAll(service.getManagerTasks(selectedWorker.getUsername()));
                selectedTask = null;
                selectedProblem = null;
                taskProblems.clear();
            } catch (ProgramException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void loadTaskProblems() {
        if(selectedTask != null) {
            selectedProblem = null;
            taskNameField.setText(selectedTask.getName());
            descriptionField.setText(selectedTask.getDescription());
            dateField.setChronology(selectedTask.getDeadline().getChronology());
            try {
                taskProblems.clear();
                taskProblems.addAll(service.getTaskProblems(selectedTask));
            } catch (ProgramException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void loadProblem() {
        if(selectedProblem != null) {
            taskNameField.setText("");
            descriptionField.setText(selectedProblem.getDescription());
            taskDeadlineColumn.setText("");
            try {
                taskProblems.clear();
                taskProblems.addAll(service.getTaskProblems(selectedTask));
            } catch (ProgramException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    public void refresh() {
        workers.clear();
        try {
            workers.addAll(service.getManagerWorkers(logged.getUsername()));
        } catch (ProgramException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
        workerTasks.clear();
        try {
            workerTasks.addAll(service.getManagerTasks(logged.getUsername()));
        } catch (ProgramException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }

        taskProblems.clear();
    }

    public void logout() {
        service.removeObserver(this);
        try {
            service.logout(logged.getUsername(), logged.getPassword());
        } catch (ProgramException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
        selfStage.hide();
        previousStage.show();
    }

    public void setSelfStage(Stage self) {
        this.selfStage = self;
    }

    public void setPreviousStage(Stage prev) {
        this.previousStage = prev;
    }

    public void onLogoutButtonClick() {
        logout();
    }

    public void onAddTaskButtonClick() {
        if(selectedWorker != null) {
            Task task = new Task();
            task.setName(taskNameField.getText());
            task.setDescription(descriptionField.getText());
            if(dateField.getValue() != null) task.setDeadline(dateField.getValue().atStartOfDay());
            task.setManager(logged);
            task.setWorker(selectedWorker);

            try {
                service.addTask(task);
            } catch (ProgramException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Employee not selected");
            alert.showAndWait();
        }
    }

    public void onDeleteTaskButtonClick() {
        if(selectedTask != null) {
            try {
                service.removeTask(selectedTask);
            } catch (ProgramException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Task not selected");
            alert.showAndWait();
        }
    }

    public void onDeleteProblemButtonClick() {
        if(selectedProblem != null) {
            try {
                service.removeProblem(selectedProblem);
            } catch (ProgramException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Problem not selected");
            alert.showAndWait();
        }
    }

    @Override
    public void update() {
        refresh();
    }

    @Override
    public void close() {
        logout();
    }
}
