import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.LandingView;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        LandingView landing = new LandingView(stage);

        Scene scene = new Scene(landing.getView(), 1100, 700);
        String cssPath = new java.io.File("src/ui/style.css").exists() ? "file:src/ui/style.css" : "file:ui/style.css";
        scene.getStylesheets().add(cssPath);
        stage.setTitle("Online Voting System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}