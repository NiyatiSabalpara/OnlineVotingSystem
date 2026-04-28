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
        root.setStyle("-fx-background-color: " + ThemeManager.bgBase() + ";");

        Pane particleLayer = new Pane();
        particleLayer.setMouseTransparent(true);
        createParticles(particleLayer);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: transparent;");

        // ── Nav bar ─────────────────────────────────────────────
        HBox navBar = new HBox();
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(16, 50, 16, 50));
        navBar.setStyle("-fx-background-color: " + ThemeManager.navBg() + ";");

        HBox brandBox = new HBox(0);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        Label bl = new Label("Votex");
        bl.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label bd = new Label(".");
        bd.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.accent() + ";");
        brandBox.getChildren().addAll(bl, bd);

        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

        Label secLabel = new Label("Secure · Transparent · Democratic");
        secLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ThemeManager.textMuted() + "; -fx-font-weight: 600;");

        // Theme toggle button
        Button themeBtn = makeThemeBtn();
        themeBtn.setOnAction(e -> ThemeManager.applyToggle(root.getScene(),
                () -> new LandingView(stage).getView()));

        navBar.getChildren().addAll(brandBox, navSpacer, secLabel, makeHSpacer(20), themeBtn);

        // ── Hero section ─────────────────────────────────────────
        VBox heroSection = new VBox(10);
        heroSection.setAlignment(Pos.CENTER);

        Label heroTag = new Label("   ONLINE VOTING SYSTEM");
        heroTag.setStyle("-fx-font-size: 11px; -fx-font-weight: 800; -fx-text-fill: " + ThemeManager.accent() + "; "
                + "-fx-background-color: " + ThemeManager.adminAccentBg() + "; -fx-background-radius: 20; "
                + "-fx-padding: 5 14 5 14; -fx-border-color: rgba(108,99,255,0.25); "
                + "-fx-border-radius: 20; -fx-border-width: 1;");

        Label heroLine = new Label("Cast Your Vote. Shape the Future.");
        heroLine.setStyle("-fx-font-size: 38px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + "; "
                + "-fx-text-alignment: center;");
        heroLine.setWrapText(true);
        heroLine.setAlignment(Pos.CENTER);

        Label heroSub = new Label("Choose your portal below to get started. Secure, transparent elections for everyone.");
        heroSub.setStyle("-fx-font-size: 14px; -fx-text-fill: " + ThemeManager.textSecondary() + "; -fx-text-alignment: center;");
        heroSub.setAlignment(Pos.CENTER);
        heroSub.setWrapText(true);
        heroSub.setMaxWidth(600);

        heroSection.getChildren().addAll(heroTag, heroLine, heroSub);

        // ── Portal cards ─────────────────────────────────────────
        HBox cardsRow = new HBox(20);
        cardsRow.setAlignment(Pos.CENTER);

        cardsRow.getChildren().addAll(
            createPortalCard("🛡️", "Admin Portal",
                "Manage elections, control voter access, monitor live results and system settings.",
                ThemeManager.adminAccentBg(), ThemeManager.accent(),
                "btn-admin-login", "Enter Admin Portal", "ADMIN"),
            createPortalCard("🎯", "Candidate Portal",
                "View your live vote counts, campaign stats and real-time election standings.",
                ThemeManager.candidateAccentBg(), ThemeManager.accentCyan(),
                "btn-candidate-login", "Enter Candidate Portal", "CANDIDATE"),
            createPortalCard("🗳️", "Voter Portal",
                "Cast your vote securely, view election results and manage your voter profile.",
                ThemeManager.voterAccentBg(), ThemeManager.accentTeal(),
                "btn-voter-login", "Enter Voter Portal", "VOTER")
        );

        // ── Centre ───────────────────────────────────────────────
        VBox centre = new VBox(32);
        centre.setAlignment(Pos.CENTER);
        centre.setPadding(new Insets(40, 60, 40, 60));
        centre.setStyle("-fx-background-color: transparent;");
        centre.getChildren().addAll(heroSection, cardsRow);

        // ── Footer ───────────────────────────────────────────────
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(12));
        footer.setStyle("-fx-background-color: transparent;");
        Label footerTxt = new Label("© 2026 Votex — Built for transparent democracy");
        footerTxt.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ThemeManager.textMuted() + ";");
        footer.getChildren().add(footerTxt);

        mainLayout.setTop(navBar);
        mainLayout.setCenter(centre);
        mainLayout.setBottom(footer);

        root.getChildren().addAll(particleLayer, mainLayout);
        animateIn(heroSection, 0);
        animateIn(cardsRow, 80);
    }

    private VBox createPortalCard(String iconText, String title, String desc,
                                   String iconBg, String accentColor,
                                   String btnStyle, String btnLabel, String role) {
        VBox card = new VBox(16);
        card.setStyle("-fx-background-color: " + ThemeManager.cardBg() + "; -fx-background-radius: 18; "
                + "-fx-border-color: " + ThemeManager.border() + "; -fx-border-width: 1; -fx-border-radius: 18; "
                + "-fx-effect: dropshadow(gaussian, " + ThemeManager.cardShadow() + ", 24, 0, 0, 8); "
                + "-fx-padding: 28 24 24 24; -fx-cursor: hand;");
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(290); card.setMinWidth(240);

        StackPane iconCircle = new StackPane();
        iconCircle.setStyle("-fx-background-color: " + iconBg + "; -fx-background-radius: 12; "
                + "-fx-min-width: 52; -fx-min-height: 52; -fx-max-width: 52; -fx-max-height: 52;");
        Label iconLabel = new Label(iconText); iconLabel.setStyle("-fx-font-size: 24px;");
        iconCircle.getChildren().add(iconLabel);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: " + ThemeManager.textPrimary() + "; "
                + "-fx-font-size: 18px; -fx-font-weight: 800;");

        Label descLabel = new Label(desc);
        descLabel.setStyle("-fx-text-fill: " + ThemeManager.textSecondary() + "; "
                + "-fx-font-size: 13px; -fx-line-spacing: 4;");
        descLabel.setWrapText(true); descLabel.setMaxWidth(260);

        Region spc = new Region(); VBox.setVgrow(spc, Priority.ALWAYS);
        Region div = new Region();
        div.setStyle("-fx-background-color: " + ThemeManager.divider() + "; -fx-min-height: 1; -fx-max-height: 1;");

        Button loginBtn = new Button(btnLabel);
        loginBtn.getStyleClass().add(btnStyle);
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnAction(e -> navigateToLogin(role));
        card.setOnMouseClicked(e -> navigateToLogin(role));

        card.getChildren().addAll(iconCircle, titleLabel, descLabel, spc, div, loginBtn);
        return card;
    }

    private void navigateToLogin(String role) {
        root.getScene().setRoot(new LoginView(stage, role).getView());
    }

    private void createParticles(Pane layer) {
        Random rand = new Random();
        double opacity = ThemeManager.isDark() ? 0.15 : 0.07;
        for (int i = 0; i < 40; i++) {
            Circle dot = new Circle(rand.nextInt(2) + 1);
            dot.setFill(Color.web(i % 3 == 0 ? "#6c63ff" : i % 3 == 1 ? "#00d4ff" : "#00c98a", opacity));
            dot.setLayoutX(rand.nextInt(1300)); dot.setLayoutY(rand.nextInt(800));
            layer.getChildren().add(dot);
            TranslateTransition tt = new TranslateTransition(Duration.seconds(rand.nextInt(18) + 14), dot);
            tt.setByX((rand.nextDouble() - 0.5) * 260); tt.setByY((rand.nextDouble() - 0.5) * 260);
            tt.setCycleCount(TranslateTransition.INDEFINITE); tt.setAutoReverse(true);
            FadeTransition ft = new FadeTransition(Duration.seconds(rand.nextInt(5) + 4), dot);
            ft.setFromValue(0.03); ft.setToValue(ThemeManager.isDark() ? 0.35 : 0.18);
            ft.setCycleCount(FadeTransition.INDEFINITE); ft.setAutoReverse(true);
            new ParallelTransition(tt, ft).play();
        }
    }

    private void animateIn(javafx.scene.Node node, int delayMs) {
        node.setOpacity(0); node.setTranslateY(18);
        FadeTransition ft = new FadeTransition(Duration.millis(600), node);
        ft.setFromValue(0); ft.setToValue(1); ft.setDelay(Duration.millis(delayMs));
        TranslateTransition tt = new TranslateTransition(Duration.millis(600), node);
        tt.setFromY(18); tt.setToY(0); tt.setDelay(Duration.millis(delayMs));
        new ParallelTransition(ft, tt).play();
    }

    private Button makeThemeBtn() {
        Button btn = new Button(ThemeManager.toggleLabel());
        btn.setStyle("-fx-background-color: " + ThemeManager.inputBg() + "; "
                + "-fx-text-fill: " + ThemeManager.textSecondary() + "; "
                + "-fx-font-size: 12px; -fx-font-weight: 700; -fx-background-radius: 20; "
                + "-fx-border-color: " + ThemeManager.border() + "; -fx-border-radius: 20; "
                + "-fx-border-width: 1; -fx-padding: 7 16 7 16; -fx-cursor: hand;");
        return btn;
    }

    private Region makeHSpacer(double width) {
        Region r = new Region(); r.setMinWidth(width); r.setMaxWidth(width);
        return r;
    }

    public StackPane getView() { return root; }
}
