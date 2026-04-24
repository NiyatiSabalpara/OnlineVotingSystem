package ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;

public class LandingView {

    private StackPane root = new StackPane();
    private Stage stage;

    public LandingView(Stage stage) {
        this.stage = stage;
        root.getStyleClass().add("landing-root");

        // --- Background particle layer ---
        Pane particleLayer = new Pane();
        particleLayer.setMouseTransparent(true);
        createParticles(particleLayer);

        // Outer scroll-free layout
        BorderPane mainLayout = new BorderPane();

        // ---- Top Navigation Bar ----
        HBox navBar = new HBox();
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(18, 50, 18, 50));
        navBar.setStyle("-fx-background-color: rgba(10,12,24,0.95);");

        HBox brandBox = new HBox(0);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        Label brandLabel = new Label("Pollaroid");
        brandLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #ffffff;");
        Label brandDot = new Label(".");
        brandDot.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #6c63ff;");
        brandBox.getChildren().addAll(brandLabel, brandDot);

        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

        Label navSubtitle = new Label("Secure · Transparent · Democratic");
        navSubtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #3d4466; -fx-font-weight: 600;");
        navBar.getChildren().addAll(brandBox, navSpacer, navSubtitle);

        // ---- Centre Content ----
        VBox centreBox = new VBox(32);
        centreBox.setAlignment(Pos.CENTER);
        centreBox.setPadding(new Insets(40, 60, 40, 60));

        // Hero text
        VBox heroSection = new VBox(10);
        heroSection.setAlignment(Pos.CENTER);

        Label heroTag = new Label("   ONLINE VOTING SYSTEM");
        heroTag.setStyle("-fx-font-size: 11px; -fx-font-weight: 800; -fx-text-fill: #6c63ff; "
                + "-fx-background-color: rgba(108,99,255,0.12); -fx-background-radius: 20; "
                + "-fx-padding: 5 14 5 14; -fx-border-color: rgba(108,99,255,0.2); "
                + "-fx-border-radius: 20; -fx-border-width: 1;");

        Label heroLine1 = new Label("Cast Your Vote. Shape the Future.");
        heroLine1.setStyle("-fx-font-size: 38px; -fx-font-weight: 900; -fx-text-fill: #e8eaf6; "
                + "-fx-text-alignment: center;");
        heroLine1.setWrapText(true);
        heroLine1.setAlignment(Pos.CENTER);

        Label heroSub = new Label("Choose your portal below to get started. Secure, transparent elections for everyone.");
        heroSub.setStyle("-fx-font-size: 14px; -fx-text-fill: #8892b0; -fx-text-alignment: center;");
        heroSub.setAlignment(Pos.CENTER);
        heroSub.setWrapText(true);
        heroSub.setMaxWidth(600);

        heroSection.getChildren().addAll(heroTag, heroLine1, heroSub);

        // ---- Portal Cards Row ----
        HBox cardsRow = new HBox(20);
        cardsRow.setAlignment(Pos.CENTER);

        VBox adminCard   = createPortalCard("🛡️", "Admin Portal",
                "Manage elections, control voter access, monitor live results and system settings.",
                "rgba(108,99,255,0.15)", "#6c63ff", "btn-admin-login", "Enter Admin Portal", "ADMIN");

        VBox candidateCard = createPortalCard("🎯", "Candidate Portal",
                "View your live vote counts, campaign stats and real-time election standings.",
                "rgba(0,212,255,0.12)", "#00d4ff", "btn-candidate-login", "Enter Candidate Portal", "CANDIDATE");

        VBox voterCard = createPortalCard("🗳️", "Voter Portal",
                "Cast your vote securely, view election results and manage your voter profile.",
                "rgba(0,229,160,0.12)", "#00e5a0", "btn-voter-login", "Enter Voter Portal", "VOTER");

        cardsRow.getChildren().addAll(adminCard, candidateCard, voterCard);

        centreBox.getChildren().addAll(heroSection, cardsRow);
        mainLayout.setTop(navBar);
        mainLayout.setCenter(centreBox);

        // Footer
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(12));
        Label footerText = new Label("© 2026 Pollaroid — Built for transparent democracy");
        footerText.setStyle("-fx-font-size: 11px; -fx-text-fill: #3d4466;");
        footer.getChildren().add(footerText);
        mainLayout.setBottom(footer);

        root.getChildren().addAll(particleLayer, mainLayout);

        // Entrance animation
        animateIn(heroSection, 0);
        animateIn(cardsRow, 80);
    }

    private VBox createPortalCard(String iconText, String title, String desc,
                                   String iconBg, String accentColor,
                                   String btnStyleClass, String btnLabel, String role) {
        VBox card = new VBox(16);
        card.setStyle(
            "-fx-background-color: #13172b; -fx-background-radius: 18; "
            + "-fx-border-color: rgba(255,255,255,0.07); -fx-border-width: 1; -fx-border-radius: 18; "
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 24, 0, 0, 8); "
            + "-fx-padding: 28 24 24 24; -fx-cursor: hand;"
        );
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(290);
        card.setMinWidth(240);

        // Icon
        StackPane iconCircle = new StackPane();
        iconCircle.setStyle("-fx-background-color: " + iconBg + "; -fx-background-radius: 12; "
                + "-fx-min-width: 52; -fx-min-height: 52; -fx-max-width: 52; -fx-max-height: 52;");
        Label iconLabel = new Label(iconText);
        iconLabel.setStyle("-fx-font-size: 24px;");
        iconCircle.getChildren().add(iconLabel);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #e8eaf6; -fx-font-size: 18px; -fx-font-weight: 800;");

        Label descLabel = new Label(desc);
        descLabel.setStyle("-fx-text-fill: #8892b0; -fx-font-size: 13px; -fx-line-spacing: 4;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(260);

        Region cardSpacer = new Region();
        VBox.setVgrow(cardSpacer, Priority.ALWAYS);

        // Thin divider
        Region divider = new Region();
        divider.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-min-height: 1; -fx-max-height: 1;");

        Button loginBtn = new Button(btnLabel);
        loginBtn.getStyleClass().add(btnStyleClass);
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        final String finalRole = role;
        loginBtn.setOnAction(e -> navigateToLogin(finalRole));

        // Hover card glow effect
        String hoverBorder = role.equals("ADMIN") ? "rgba(108,99,255,0.4)"
                           : role.equals("CANDIDATE") ? "rgba(0,212,255,0.4)" : "rgba(0,229,160,0.4)";
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle()
                .replace("rgba(255,255,255,0.07)", hoverBorder)));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle()
                .replace(hoverBorder, "rgba(255,255,255,0.07)")));
        card.setOnMouseClicked(e -> navigateToLogin(finalRole));

        card.getChildren().addAll(iconCircle, titleLabel, descLabel, cardSpacer, divider, loginBtn);
        return card;
    }

    private void navigateToLogin(String role) {
        root.getScene().setRoot(new LoginView(stage, role).getView());
    }

    private void createParticles(Pane layer) {
        Random rand = new Random();
        for (int i = 0; i < 40; i++) {
            Circle dot = new Circle(rand.nextInt(2) + 1);
            dot.setFill(Color.web(i % 3 == 0 ? "#6c63ff" : i % 3 == 1 ? "#00d4ff" : "#00e5a0",
                    0.08 + rand.nextDouble() * 0.15));
            dot.setLayoutX(rand.nextInt(1300));
            dot.setLayoutY(rand.nextInt(800));
            layer.getChildren().add(dot);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(rand.nextInt(18) + 14), dot);
            tt.setByX((rand.nextDouble() - 0.5) * 280);
            tt.setByY((rand.nextDouble() - 0.5) * 280);
            tt.setCycleCount(TranslateTransition.INDEFINITE);
            tt.setAutoReverse(true);

            FadeTransition ft = new FadeTransition(Duration.seconds(rand.nextInt(5) + 4), dot);
            ft.setFromValue(0.04); ft.setToValue(0.35);
            ft.setCycleCount(FadeTransition.INDEFINITE); ft.setAutoReverse(true);

            new ParallelTransition(tt, ft).play();
        }
    }

    private void animateIn(javafx.scene.Node node, int delayMs) {
        node.setOpacity(0);
        node.setTranslateY(18);
        FadeTransition ft = new FadeTransition(Duration.millis(650), node);
        ft.setFromValue(0); ft.setToValue(1); ft.setDelay(Duration.millis(delayMs));
        TranslateTransition tt = new TranslateTransition(Duration.millis(650), node);
        tt.setFromY(18); tt.setToY(0); tt.setDelay(Duration.millis(delayMs));
        new ParallelTransition(ft, tt).play();
    }

    public StackPane getView() {
        return root;
    }
}
