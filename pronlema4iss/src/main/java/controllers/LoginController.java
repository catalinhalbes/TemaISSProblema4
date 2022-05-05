package controllers;

import business.Service;
import domain.Worker;
import exceptions.ProgramException;
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

        admin.show();
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

            switch (worker.getRole()) {
                case "admin" -> startAdminView(worker);
                default -> {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setContentText("Functionality for role '" + worker.getRole() + "' is not added");
                    a.showAndWait();
                    return;
                }
            }

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("add dropdown to role name");
            a.showAndWait();

            selfStage.hide();
        } catch (ProgramException | IOException ex) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(ex.getMessage());
            a.showAndWait();
        }
    }
}
