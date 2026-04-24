import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.LandingView;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        LandingView landing = new LandingView(stage);

        Scene scene = new Scene(landing.getView(), 1200, 750);
        String cssPath = new java.io.File("src/ui/style.css").exists()
                ? "file:src/ui/style.css" : "file:ui/style.css";
        scene.getStylesheets().add(cssPath);
        stage.setTitle("Pollaroid — Online Voting System");
        stage.setMinWidth(1100);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}