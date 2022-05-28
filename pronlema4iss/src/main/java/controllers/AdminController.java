package controllers;

import business.Service;
import business.observer.Observer;
import domain.Worker;
import exceptions.ProgramException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.util.List;

public class AdminController implements Observer {
    @FXML
    private TableView<Worker> workersTable;
    @FXML
    private TableColumn<Worker, String> usernameColumn;
    @FXML
    private TableColumn<Worker, String> nameColumn;
    @FXML
    private TableColumn<Worker, String> roleColumn;
    @FXML
    private TableColumn<Worker, String> managerColumn;
    @FXML
    private ComboBox<String> roleSelect;
    @FXML
    private ComboBox<String> managerSelect;
    @FXML
    private Label welcomeLabel;
    @FXML
    private TextField userField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField passField;

    private final ObservableList<Worker> workers = FXCollections.observableArrayList();
    private final ObservableList<String> managers = FXCollections.observableArrayList();
    private final ObservableList<String> roles = FXCollections.observableArrayList();
    private Worker selectedWorker;

    private Worker logged;
    private Service service;
    private Stage selfStage, previousStage;

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

    public void initialize() {
        workersTable.setItems(workers);
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("Username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("Role"));
        managerColumn.setCellValueFactory(cellData -> {
            Worker manager = cellData.getValue().getManager();
            if(manager == null){
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty(manager.getUsername());
        });

        workersTable.setRowFactory(tv -> {
            TableRow<Worker> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (!row.isEmpty() && ev.getButton() == MouseButton.PRIMARY && ev.getClickCount() == 1) {
                    selectedWorker = row.getItem();
                    loadWorkerData();
                }
            });

            return row;
        });

        roleSelect.setItems(roles);
        managerSelect.setItems(managers);
    }

    public void refresh() {
        welcomeLabel.setText("Welcome, " + logged.getName() + "!");
        workers.clear();
        try {
            workers.addAll(service.getWorkers());
        } catch (ProgramException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }

        String selected = roleSelect.getSelectionModel().getSelectedItem();
        roles.clear();
        try {
            roles.add("");
            roles.addAll(service.getRoles());
            roleSelect.getSelectionModel().select(selected);
        } catch (ProgramException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }

        managers.clear();
        try {
            managers.add("");
            managerSelect.getSelectionModel().select(0);
            List<Worker> w = service.getWorkersByRole("manager");
            w.forEach(worker -> managers.add(worker.getUsername()));
            roleSelect.getSelectionModel().select(selected);
        } catch (ProgramException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    private void loadWorkerData() {
        userField.setText(selectedWorker.getUsername());
        nameField.setText(selectedWorker.getName());
        roleSelect.getSelectionModel().select(selectedWorker.getRole());
        if(selectedWorker.getManager() != null)
            managerSelect.getSelectionModel().select(selectedWorker.getManager().getUsername());
        else
            managerSelect.getSelectionModel().select(0);
        passField.setText("");
    }

    private void clearWorkerData() {
        userField.setText("");
        nameField.setText("");
        roleSelect.getSelectionModel().select(0);
        managerSelect.getSelectionModel().select(0);
        passField.setText("");
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

    @FXML
    private void onLogoutButtonClick() {
        logout();
    }

    @FXML
    private void onAddButtonClick() {
        String user = userField.getText();
        String name = nameField.getText();
        String role = roleSelect.getSelectionModel().getSelectedItem();
        String pass = passField.getText();
        Worker manager = new Worker();
        if (managerSelect.getSelectionModel().getSelectedIndex() != 0)
            manager.setUsername(managerSelect.getSelectionModel().getSelectedItem());
        else
            manager = null;
        Worker worker = new Worker(user, name, role, manager, pass);
        try {
            service.addWorker(worker);
        } catch (ProgramException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onUpdateButtonClick() {
        String user = userField.getText();
        String name = nameField.getText();
        String role = roleSelect.getSelectionModel().getSelectedItem();
        String pass = passField.getText();
        Worker manager = new Worker();
        if (managerSelect.getSelectionModel().getSelectedIndex() != 0)
            manager.setUsername(managerSelect.getSelectionModel().getSelectedItem());
        else
            manager = null;
        Worker worker = new Worker(user, name, role, manager, pass);
        try {
            service.updateWorker(worker);
        } catch (ProgramException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onRemoveButtonClick() {
        String user = userField.getText();
        try {
            service.deleteWorker(user);
        } catch (ProgramException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onRefreshButtonClick() {
        refresh();
    }

    @FXML
    private void onClearButtonClick() {
        clearWorkerData();
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
