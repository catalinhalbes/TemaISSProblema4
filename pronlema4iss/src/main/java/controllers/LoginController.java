package controllers;

import business.Service;
import domain.Worker;
import exceptions.ProgramException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    private Service service;
    private Stage selfStage;
    @FXML
    private PasswordField passField;
    @FXML
    private TextField userField;

    public void setService(Service service) {
        this.service = service;
    }

    public void setSelfStage(Stage stage) {
        this.selfStage = stage;
    }

    private void startAdminView(Worker worker) throws IOException {
        Stage admin = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/adminView.fxml"));

        Parent parent = loader.load();
        admin.setScene(new Scene(parent));
        admin.setTitle("Admin: " + worker.getUsername());

        AdminController controller = loader.getController();
        controller.setLogged(worker);
        controller.setSelfStage(admin);
        controller.setPreviousStage(selfStage);
        controller.setService(service);
        controller.refresh();

        admin.setOnCloseRequest(event -> {
            controller.logout();
        });

        admin.show();
    }

    private void startManagerView(Worker worker) throws IOException {
        Stage manager = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/managerView.fxml"));

        Parent parent = loader.load();
        manager.setScene(new Scene(parent));
        manager.setTitle("Manager: " + worker.getUsername());

        ManagerController controller = loader.getController();
        controller.setLogged(worker);
        controller.setSelfStage(manager);
        controller.setPreviousStage(selfStage);
        controller.setService(service);
        controller.refresh();

        manager.setOnCloseRequest(event -> {
            controller.logout();
        });

        manager.show();
    }

    private void startEmployeeView(Worker worker) throws IOException {
        Stage employee = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/employeeView.fxml"));

        Parent parent = loader.load();
        employee.setScene(new Scene(parent));
        employee.setTitle("Employee: " + worker.getUsername());

        EmployeeController controller = loader.getController();
        controller.setLogged(worker);
        controller.setSelfStage(employee);
        controller.setPreviousStage(selfStage);
        controller.setService(service);
        controller.refresh();

        employee.setOnCloseRequest(event -> {
            controller.logout();
        });

        employee.show();
    }

    @FXML
    private void onLoginButtonClick() {
        String username = userField.getText();
        String password = passField.getText();

        try {
            Worker worker = service.login(username, password);
            if (worker == null) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Invalid credentials");
                a.showAndWait();
                return;
            }

            worker.setPassword(password);

            switch (worker.getRole()) {
                case "admin" -> startAdminView(worker);
                case "employee" -> startEmployeeView(worker);
                case "manager" -> startManagerView(worker);
                default -> {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setContentText("Functionality for role '" + worker.getRole() + "' is not added");
                    a.showAndWait();
                }
            }
        } catch (ProgramException | IOException ex) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(ex.getMessage());
            a.showAndWait();
        }
    }
}
