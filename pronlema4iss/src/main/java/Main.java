import business.Service;
import controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.WorkerDBRepository;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("db.properties"));
        } catch (IOException ex){
            System.err.println("Cannot find db.properties " + ex);
            return;
        }

        WorkerDBRepository workerRepo = new WorkerDBRepository(properties);
        Service service = new Service(workerRepo);

        Stage login = getLoginStage(service);
        login.show();
    }

    private Stage getLoginStage(Service service) throws IOException {
        Stage login = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/loginView.fxml"));

        Parent base = loader.load();
        login.setScene(new Scene(base));
        login.setTitle("Login");

        LoginController controller = loader.getController();
        controller.setService(service);
        controller.setSelfStage(login);

        return login;
    }
}
