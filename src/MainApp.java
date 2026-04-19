import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.LoginView;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        LoginView login = new LoginView(stage);

        Scene scene = new Scene(login.getView(), 900, 600);
        scene.getStylesheets().add("file:src/ui/style.css");
        stage.setTitle("Online Voting System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}