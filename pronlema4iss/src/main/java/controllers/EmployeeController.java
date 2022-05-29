package controllers;

import business.Service;
import business.observer.Observer;
import domain.Problem;
import domain.Task;
import domain.Worker;
import exceptions.ProgramException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class EmployeeController implements Observer {
    public TableView<Task> tasksTable;
    public TableColumn<Task, String> nameColumn;
    public TableColumn<Task, String> deadlineColumn;
    public TableColumn<Task, String> descriptionColumn;

    public TextField taskNameField;
    public TextArea problemDescriptionField;

    private Worker logged;
    private Task selectedTask;
    private Service service;
    private Stage selfStage, previousStage;

    private final ObservableList<Task> taskObservableList = FXCollections.observableArrayList();

    public void initialize() {
        tasksTable.setItems(taskObservableList);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("Deadline"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Description"));

        tasksTable.setRowFactory(tv -> {
            TableRow<Task> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (!row.isEmpty() && ev.getButton() == MouseButton.PRIMARY && ev.getClickCount() == 1) {
                    selectedTask = row.getItem();
                    loadTaskData();
                }
            });
            return row;
        });
    }

    private void loadTaskData() {
        taskNameField.setText(selectedTask.getName());
        problemDescriptionField.setText(selectedTask.getDescription());
    }

    public void setLogged(Worker logged) {
        this.logged = logged;
    }

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
    }

    public void setSelfStage(Stage self) {
        this.selfStage = self;
    }

    public void setPreviousStage(Stage prev) {
        this.previousStage = prev;
    }

    public void refresh() {
        selectedTask = null;
        taskObservableList.clear();
        try {
            taskObservableList.addAll(service.getEmployeeTasks(logged.getUsername()));
        } catch (ProgramException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
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

    public void onFinishButtonClick() {
        try {
            if(selectedTask != null) {
                service.finishTask(selectedTask);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("No task selected");
                alert.showAndWait();
            }
        } catch (ProgramException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    public void onReportButtonClick() {
        try {
            if(selectedTask != null) {
                String desc = problemDescriptionField.getText();
                Problem p = new Problem();
                p.setDescription(desc);
                p.setTask(selectedTask);
                service.reportProblem(p);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("No task selected");
                alert.showAndWait();
            }
        } catch (ProgramException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    public void onLogoutButtonClick() {
        logout();
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
