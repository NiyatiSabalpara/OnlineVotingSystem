package ui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.Random;

public class LandingView {

    private StackPane root = new StackPane();
    private Stage stage;

    public LandingView(Stage stage) {
        this.stage = stage;
        root.setStyle("-fx-background-color: #EAEEF5;");

        // 1. Background Animation Layer
        Pane animationLayer = new Pane();
        createBackgroundAnimations(animationLayer);

        // 2. Foreground Card
        BorderPane card = new BorderPane();
        card.getStyleClass().add("landing-card");
        card.setMaxSize(900, 500); // Fixed size for the central glass card

        // -- Top Header
        HBox topHeader = new HBox();
        topHeader.setAlignment(Pos.CENTER_LEFT);
        topHeader.setPadding(new Insets(20, 30, 0, 30));

        Label brand = new Label("Pollaroid");
        brand.getStyleClass().add("landing-logo");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button loginBtn = new Button("LOGIN");
        loginBtn.getStyleClass().add("landing-btn-secondary");
        loginBtn.setOnAction(e -> navigateToLogin());

        topHeader.getChildren().addAll(brand, spacer, loginBtn);

        // -- Center Content
        HBox centerContent = new HBox(40);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(40, 40, 40, 40));

        // Let's create the left info box
        VBox leftInfo = new VBox(20);
        leftInfo.setAlignment(Pos.CENTER_LEFT);
        leftInfo.setPrefWidth(400);

        Label title = new Label("Start voting in\nminutes");
        title.getStyleClass().add("landing-title");

        Label subtitle = new Label("Polling made easy for all types\nof events. Manage polls and\noutcomes.");
        subtitle.getStyleClass().add("landing-subtitle");

        Button getStartedBtn = new Button("GET STARTED");
        getStartedBtn.getStyleClass().add("landing-btn-primary");
        getStartedBtn.setOnAction(e -> navigateToRegister());

        HBox socialIcons = new HBox(15);
        socialIcons.setPadding(new Insets(30, 0, 0, 0));
        // Add fake social icon shapes (just small circles as placeholders)
        for (int i = 0; i < 3; i++) {
            Circle sIcon = new Circle(12, Color.web("#8091B3"));
            socialIcons.getChildren().add(sIcon);
        }

        leftInfo.getChildren().addAll(title, subtitle, getStartedBtn, socialIcons);

        // Right Illustration
        VBox rightGraphic = new VBox();
        rightGraphic.setAlignment(Pos.CENTER);
        try {
            String imgPath = new java.io.File("src/assets/voting_illustration.png").exists() ? "file:src/assets/voting_illustration.png" : "file:assets/voting_illustration.png";
            Image img = new Image(imgPath);
            ImageView imgView = new ImageView(img);
            imgView.setPreserveRatio(true);
            imgView.setFitHeight(300);
            rightGraphic.getChildren().add(imgView);
        } catch (Exception e) {
            System.err.println("Could not load image: " + e.getMessage());
        }

        centerContent.getChildren().addAll(leftInfo, rightGraphic);

        card.setTop(topHeader);
        card.setCenter(centerContent);

        // Layering
        root.getChildren().addAll(animationLayer, card);
    }

    private void createBackgroundAnimations(Pane layer) {
        Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            Shape shape;
            if (rand.nextBoolean()) {
                shape = new Circle(rand.nextInt(40) + 10);
            } else {
                Line line = new Line(0, 0, rand.nextInt(100) + 50, rand.nextInt(50));
                line.setStrokeWidth(5);
                shape = line;
            }
            shape.setFill(Color.web("#FFFFFF", 0.3));
            shape.setStroke(Color.web("#FFFFFF", 0.4));
            
            double startX = rand.nextInt(1200);
            double startY = rand.nextInt(800);
            shape.setLayoutX(startX);
            shape.setLayoutY(startY);
            layer.getChildren().add(shape);

            // Animate
            TranslateTransition tt = new TranslateTransition(Duration.seconds(rand.nextInt(15) + 10), shape);
            tt.setByX((rand.nextDouble() - 0.5) * 400);
            tt.setByY((rand.nextDouble() - 0.5) * 400);
            tt.setCycleCount(TranslateTransition.INDEFINITE);
            tt.setAutoReverse(true);
            tt.setInterpolator(Interpolator.EASE_BOTH);

            FadeTransition ft = new FadeTransition(Duration.seconds(rand.nextInt(5) + 5), shape);
            ft.setFromValue(0.1);
            ft.setToValue(0.4);
            ft.setCycleCount(FadeTransition.INDEFINITE);
            ft.setAutoReverse(true);

            ParallelTransition pt = new ParallelTransition(tt, ft);
            pt.play();
        }
    }

    private void navigateToLogin() {
        stage.getScene().setRoot(new LoginView(stage).getView());
    }

    private void navigateToRegister() {
        stage.getScene().setRoot(new RegisterView(stage).getView());
    }

    public StackPane getView() {
        return root;
    }
}
