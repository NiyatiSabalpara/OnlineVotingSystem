package ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.VoteService;

public class LoginView {

    private BorderPane root = new BorderPane();
    private Stage stage;
    private String role;

    public LoginView(Stage stage, String role) {
        this.stage = stage;
        this.role  = role;
        root.setStyle("-fx-background-color: " + ThemeManager.bgBase() + ";");
        buildUI();
    }

    public LoginView(Stage stage) { this(stage, "VOTER"); }

    private void buildUI() {
        String accentHex = role.equals("ADMIN") ? ThemeManager.accent()
                         : role.equals("CANDIDATE") ? ThemeManager.accentCyan()
                         : ThemeManager.accentTeal();
        String iconText  = role.equals("ADMIN") ? "🛡️" : role.equals("CANDIDATE") ? "🎯" : "🗳️";
        String portal    = role.equals("ADMIN") ? "Admin Portal"
                         : role.equals("CANDIDATE") ? "Candidate Portal" : "Voter Portal";
        String tagLine   = role.equals("ADMIN")     ? "Full system control at\nyour fingertips."
                         : role.equals("CANDIDATE") ? "Track your campaign\nin real time."
                         :                            "Your vote is your\nstrongest voice.";
        String hint      = role.equals("ADMIN")     ? "ID: admin\nPassword: admin123"
                         : role.equals("CANDIDATE") ? "Demo IDs (pre-seeded):\ncand_alice / pass1\ncand_bob / pass2\ncand_charlie / pass3"
                         :                            "V101 / pass\nV102 / pass\n(or register)";

        // ── LEFT PANEL ───────────────────────────────────────────
        VBox leftPanel = new VBox(28);
        leftPanel.setStyle("-fx-background-color: " + ThemeManager.bgSurface() + "; -fx-padding: 52 44 52 44;");
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setPrefWidth(360);

        Label brand = new Label("Votex.");
        brand.setStyle("-fx-font-size: 17px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textMuted() + ";");

        StackPane avatarGlow = new StackPane();
        avatarGlow.setStyle("-fx-background-color: " + accentHex + "18; -fx-background-radius: 50; "
                + "-fx-min-width: 96; -fx-min-height: 96; -fx-max-width: 96; -fx-max-height: 96; "
                + "-fx-effect: dropshadow(gaussian, " + accentHex + "55, 28, 0, 0, 0);");
        Label avaIco = new Label(iconText); avaIco.setStyle("-fx-font-size: 42px;");
        avatarGlow.getChildren().add(avaIco);

        Label portalLabel = new Label(portal);
        portalLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");

        Label tagLabel = new Label(tagLine);
        tagLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + ThemeManager.textSecondary() + "; -fx-line-spacing: 5;");

        // Hint box
        VBox hintBox = new VBox(6);
        hintBox.setStyle("-fx-background-color: " + ThemeManager.inputBg() + "; -fx-background-radius: 12; "
                + "-fx-border-color: " + ThemeManager.inputBorder() + "; -fx-border-radius: 12; "
                + "-fx-border-width: 1; -fx-padding: 14 16 14 16;");
        Label hintHdr = new Label("DEMO CREDENTIALS");
        hintHdr.setStyle("-fx-font-size: 10px; -fx-font-weight: 800; -fx-text-fill: " + accentHex + ";");
        Label hintTxt = new Label(hint);
        hintTxt.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ThemeManager.textSecondary()
                + "; -fx-font-family: 'Courier New';");
        hintBox.getChildren().addAll(hintHdr, hintTxt);

        // Theme toggle
        Button themeBtn = makeThemeBtn();
        themeBtn.setOnAction(e -> ThemeManager.applyToggle(root.getScene(),
                () -> new LoginView(stage, role).getView()));

        Button backBtn = new Button("← Back to Home");
        backBtn.setStyle(ThemeManager.navNormal());
        backBtn.setOnAction(e -> root.getScene().setRoot(new LandingView(stage).getView()));

        Region lSpacer = new Region(); VBox.setVgrow(lSpacer, Priority.ALWAYS);
        leftPanel.getChildren().addAll(brand, avatarGlow, portalLabel, tagLabel, hintBox, lSpacer, themeBtn, backBtn);

        // ── RIGHT FORM PANEL ────────────────────────────────────
        VBox formPane = new VBox(0);
        formPane.setStyle("-fx-background-color: " + ThemeManager.bgBase() + "; -fx-padding: 56 64 56 64;");
        formPane.setAlignment(Pos.CENTER);

        VBox formCard = new VBox(20);
        formCard.setMaxWidth(370); formCard.setAlignment(Pos.TOP_LEFT);

        // Role badge
        Label badge = new Label(role + " LOGIN");
        badge.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: "
                + (role.equals("VOTER") || role.equals("CANDIDATE") ? "#0d0f1a" : "white") + "; "
                + "-fx-background-color: " + accentHex + "; -fx-background-radius: 20; -fx-padding: 4 14 4 14;");

        Label formTitle = new Label("Welcome back");
        formTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");

        Label formSub = new Label("Sign in to your " + portal.toLowerCase() + " to continue");
        formSub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");

        // Fields
        VBox idBox = fieldBox("User ID",
                role.equals("ADMIN") ? "admin" : role.equals("CANDIDATE") ? "cand_alice" : "V101",
                false, accentHex);
        TextField idField = (TextField) idBox.getChildren().get(1);

        VBox passBox = fieldBox("Password", "Enter your password", true, accentHex);
        PasswordField passField = (PasswordField) passBox.getChildren().get(1);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ff5470; -fx-font-weight: 700;");
        errorLabel.setVisible(false);

        // Primary button
        Button loginBtn = new Button("Sign In to " + portal);
        loginBtn.setStyle("-fx-background-color: " + accentHex + "; "
                + "-fx-text-fill: " + (role.equals("ADMIN") ? "white" : "#0d0f1a") + "; "
                + "-fx-font-size: 14px; -fx-font-weight: 800; -fx-background-radius: 12; "
                + "-fx-padding: 13 0 13 0; -fx-cursor: hand; -fx-border-color: transparent;");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnAction(e -> handleLogin(idField.getText(), passField.getText(), errorLabel));
        passField.setOnAction(e -> handleLogin(idField.getText(), passField.getText(), errorLabel));

        formCard.getChildren().addAll(badge, formTitle, formSub, idBox, passBox, errorLabel, loginBtn);

        if (role.equals("VOTER")) {
            Hyperlink rl = new Hyperlink("New voter? Register here →");
            rl.setStyle("-fx-text-fill: " + ThemeManager.accent() + "; -fx-font-size: 13px; "
                    + "-fx-font-weight: 600; -fx-border-color: transparent;");
            rl.setOnAction(e -> root.getScene().setRoot(new RegisterView(stage).getView()));
            formCard.getChildren().add(rl);
        }

        formPane.getChildren().add(formCard);

        root.setLeft(leftPanel);
        root.setCenter(formPane);

        formCard.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(450), formCard);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    private VBox fieldBox(String labelText, String prompt, boolean isPassword, String accentHex) {
        VBox box = new VBox(8);
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: " + ThemeManager.textSecondary() + ";");
        Control field = isPassword ? new PasswordField() : new TextField();
        if (field instanceof TextField) ((TextField) field).setPromptText(prompt);
        else ((PasswordField) field).setPromptText(prompt);
        field.setStyle("-fx-pref-height: 44px; -fx-background-color: " + ThemeManager.inputBg() + "; "
                + "-fx-background-radius: 12; -fx-border-color: " + ThemeManager.inputBorder() + "; "
                + "-fx-border-radius: 12; -fx-border-width: 1; -fx-padding: 0 14 0 14; "
                + "-fx-font-size: 14px; -fx-text-fill: " + ThemeManager.textPrimary() + "; "
                + "-fx-prompt-text-fill: " + ThemeManager.textMuted() + ";");
        box.getChildren().addAll(lbl, field);
        return box;
    }

    private void handleLogin(String id, String password, Label errorLabel) {
        if (id.isBlank() || password.isBlank()) { showError(errorLabel, "Please enter your ID and password."); return; }
        String auth = VoteService.getInstance().authenticate(id, password);
        if (auth == null) { showError(errorLabel, "Invalid credentials. Please try again."); return; }
        if (!auth.equals(role)) { showError(errorLabel, "These credentials belong to the " + auth + " portal."); return; }
        switch (auth) {
            case "ADMIN":     root.getScene().setRoot(new AdminDashboard(stage).getView()); break;
            case "CANDIDATE": root.getScene().setRoot(new CandidateDashboard(stage).getView()); break;
            case "VOTER":     root.getScene().setRoot(new VoterDashboard(stage).getView()); break;
        }
    }

    private void showError(Label lbl, String msg) {
        lbl.setText("⚠  " + msg); lbl.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(280), lbl);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
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

    public BorderPane getView() { return root; }
}