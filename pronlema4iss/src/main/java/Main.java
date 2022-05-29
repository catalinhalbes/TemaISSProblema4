import business.Service;
import controllers.LoginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import repository.ProblemRepository;
import repository.TaskRepository;
import repository.WorkerRepository;

import java.io.IOException;

public class Main extends Application {
    static SessionFactory sessionFactory;
    static void initializeSessionFactory() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            System.err.println("\n\nException " + e);
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initializeSessionFactory();

        WorkerRepository workerRepo = new WorkerRepository(sessionFactory);
        TaskRepository taskRepo = new TaskRepository(sessionFactory);
        ProblemRepository problemRepo = new ProblemRepository(sessionFactory);
        Service service = new Service(workerRepo, taskRepo, problemRepo);

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

        login.setOnCloseRequest(event -> {
            service.notifyClose();
            System.exit(0);
        });

        LoginController controller = loader.getController();
        controller.setService(service);
        controller.setSelfStage(login);

        return login;
    }
}
