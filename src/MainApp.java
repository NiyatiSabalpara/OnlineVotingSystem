import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.LandingView;
import ui.ThemeManager;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        LandingView landing = new LandingView(stage);

        Scene scene = new Scene(landing.getView(), 1200, 750);
        scene.getStylesheets().add(ThemeManager.getCssUrl());
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