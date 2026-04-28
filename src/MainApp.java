import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ui.LandingView;
import ui.ThemeManager;

import java.io.InputStream;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        LandingView landing = new LandingView(stage);

        Scene scene = new Scene(landing.getView(), 1200, 750);
        scene.getStylesheets().add(ThemeManager.getCssUrl());
        stage.setTitle("Votex — Online Voting System");
        stage.setMinWidth(1100);
        stage.setMinHeight(680);

        // Set application icon (shown in title bar and taskbar)
        try {
            InputStream iconStream = getClass().getResourceAsStream("/assets/logo.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception e) {
            System.err.println("[MainApp] Could not load app icon: " + e.getMessage());
        }

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}