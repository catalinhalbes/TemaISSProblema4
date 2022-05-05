package controllers;

import business.Service;
import domain.Worker;
import exceptions.ProgramException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class AdminController {
    @FXML
    private TableView<Worker> workersTable;
    @FXML
    private TableColumn<Worker, String> usernameColumn;
    @FXML
    private TableColumn<Worker, String> nameColumn;
    @FXML
    private TableColumn<Worker, String> roleColumn;
    @FXML
    public ComboBox<String> roleSelect;
    @FXML
    private Label welcomeLabel;
    @FXML
    private TextField userField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField passField;

    private final ObservableList<Worker> workers = FXCollections.observableArrayList();
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
    }

    private void loadWorkerData() {
        userField.setText(selectedWorker.getUsername());
        nameField.setText(selectedWorker.getName());
        roleSelect.getSelectionModel().select(selectedWorker.getRole());
        passField.setText("");
    }

    private void clearWorkerData() {
        userField.setText("");
        nameField.setText("");
        roleSelect.getSelectionModel().select(0);
        passField.setText("");
    }

    @FXML
    private void onLogoutButtonClick() {
        selfStage.hide();
        previousStage.show();
    }

    @FXML
    private void onAddButtonClick() {
        String user = userField.getText();
        String name = nameField.getText();
        String role = roleSelect.getSelectionModel().getSelectedItem();
        String pass = passField.getText();
        Worker worker = new Worker(user, name, role, pass);
        try {
            service.addWorker(worker);
        } catch (ProgramException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
        refresh();
    }

    @FXML
    private void onUpdateButtonClick() {
        String user = userField.getText();
        String name = nameField.getText();
        String role = roleSelect.getSelectionModel().getSelectedItem();
        String pass = passField.getText();
        Worker worker = new Worker(user, name, role, pass);
        try {
            service.updateWorker(worker);
        } catch (ProgramException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
        refresh();
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
        refresh();
    }

    @FXML
    private void onRefreshButtonClick() {
        refresh();
    }

    @FXML
    private void onClearButtonClick() {
        clearWorkerData();
    }
}
