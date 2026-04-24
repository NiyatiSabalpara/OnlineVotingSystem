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
    private String role; // "ADMIN", "CANDIDATE", "VOTER"

    public LoginView(Stage stage, String role) {
        this.stage = stage;
        this.role = role;
        root.setStyle("-fx-background-color: #0d0f1a;");
        buildUI();
    }

    // Legacy constructor (defaults to VOTER)
    public LoginView(Stage stage) {
        this(stage, "VOTER");
    }

    private void buildUI() {
        // ---- LEFT BRANDING PANEL ----
        VBox leftPanel = new VBox(30);
        leftPanel.getStyleClass().add("login-left-panel");
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setPrefWidth(380);

        String iconText, accentColor, portalName, tagLine, hintText;
        switch (role) {
            case "ADMIN":
                iconText = "🛡️";
                accentColor = "#6c63ff";
                portalName = "Admin Portal";
                tagLine = "Full system control at\nyour fingertips.";
                hintText = "Credentials:\nID: admin\nPassword: admin123";
                break;
            case "CANDIDATE":
                iconText = "🎯";
                accentColor = "#00d4ff";
                portalName = "Candidate Portal";
                tagLine = "Track your campaign\nin real time.";
                hintText = "Credentials:\ncand_alice / pass1\ncand_bob / pass2\ncand_charlie / pass3";
                break;
            default: // VOTER
                iconText = "🗳️";
                accentColor = "#00e5a0";
                portalName = "Voter Portal";
                tagLine = "Your vote is your\nstrongest voice.";
                hintText = "Credentials:\nV101 / pass\nV102 / pass\n(or register below)";
        }

        // Glow avatar circle
        StackPane avatarGlow = new StackPane();
        avatarGlow.setStyle(
                "-fx-background-color: " + accentColor + "1A; " +
                "-fx-background-radius: 50; " +
                "-fx-min-width: 100; -fx-min-height: 100; -fx-max-width: 100; -fx-max-height: 100; " +
                "-fx-effect: dropshadow(gaussian, " + accentColor + "66, 30, 0, 0, 0);"
        );
        Label avatarIcon = new Label(iconText);
        avatarIcon.setStyle("-fx-font-size: 44px;");
        avatarGlow.getChildren().add(avatarIcon);

        Label portalLabel = new Label(portalName);
        portalLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: 900; -fx-text-fill: #e8eaf6;");

        Label tagLineLabel = new Label(tagLine);
        tagLineLabel.getStyleClass().add("login-left-tagline");

        // Hint box
        VBox hintBox = new VBox(6);
        hintBox.getStyleClass().add("hint-box");
        Label hintHeader = new Label("DEMO CREDENTIALS");
        hintHeader.setStyle("-fx-font-size: 10px; -fx-font-weight: 800; -fx-text-fill: " + accentColor + ";");
        Label hintContent = new Label(hintText);
        hintContent.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892b0; -fx-font-family: 'Courier New';");
        hintBox.getChildren().addAll(hintHeader, hintContent);

        // Back button
        Button backBtn = new Button("← Back to Home");
        backBtn.getStyleClass().add("btn-ghost");
        backBtn.setOnAction(e -> root.getScene().setRoot(new LandingView(stage).getView()));

        Region leftSpacer = new Region();
        VBox.setVgrow(leftSpacer, Priority.ALWAYS);

        // Brand logo
        Label brand = new Label("Pollaroid.");
        brand.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: #3d4466;");

        leftPanel.getChildren().addAll(brand, avatarGlow, portalLabel, tagLineLabel, hintBox, leftSpacer, backBtn);

        // ---- RIGHT FORM PANEL ----
        VBox formPane = new VBox(0);
        formPane.getStyleClass().add("login-form-pane");
        formPane.setAlignment(Pos.CENTER);

        VBox formCard = new VBox(22);
        formCard.setMaxWidth(380);
        formCard.setAlignment(Pos.TOP_LEFT);

        // Role badge
        Label roleBadge = new Label(role + " LOGIN");
        roleBadge.getStyleClass().addAll("role-badge", "role-badge-" + role.toLowerCase());

        Label formTitle = new Label("Welcome back");
        formTitle.getStyleClass().add("login-form-title");

        Label formSubtitle = new Label("Sign in to your " + portalName.toLowerCase() + " to continue");
        formSubtitle.getStyleClass().add("login-form-subtitle");

        // Fields
        VBox idBox = new VBox(8);
        Label idLabel = new Label("User ID");
        idLabel.getStyleClass().add("form-label");
        TextField idField = new TextField();
        idField.setPromptText(role.equals("ADMIN") ? "Enter admin" : role.equals("CANDIDATE") ? "e.g. cand_alice" : "e.g. V101");
        idField.getStyleClass().add("form-field");
        if (!role.equals("ADMIN")) {
            idField.getStyleClass().add(role.equals("CANDIDATE") ? "form-field-cyan" : "form-field-teal");
        }
        idBox.getChildren().addAll(idLabel, idField);

        VBox passBox = new VBox(8);
        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("form-label");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter your password");
        passField.getStyleClass().add("form-field");
        if (!role.equals("ADMIN")) {
            passField.getStyleClass().add(role.equals("CANDIDATE") ? "form-field-cyan" : "form-field-teal");
        }
        passBox.getChildren().addAll(passLabel, passField);

        // Error label
        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        // Login button - pick correct style class
        String btnClass = role.equals("ADMIN") ? "btn-primary-purple" :
                          role.equals("CANDIDATE") ? "btn-primary-cyan" : "btn-primary-teal";
        Button loginBtn = new Button("Sign In to " + portalName);
        loginBtn.getStyleClass().add(btnClass);
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        loginBtn.setOnAction(e -> handleLogin(idField.getText(), passField.getText(), errorLabel));
        passField.setOnAction(e -> handleLogin(idField.getText(), passField.getText(), errorLabel));

        formCard.getChildren().addAll(roleBadge, formTitle, formSubtitle, idBox, passBox, errorLabel, loginBtn);

        // Voter-only: Register link
        if (role.equals("VOTER")) {
            Hyperlink registerLink = new Hyperlink("New voter? Register here →");
            registerLink.getStyleClass().add("hyperlink-text");
            registerLink.setOnAction(e -> root.getScene().setRoot(new RegisterView(stage).getView()));
            formCard.getChildren().add(registerLink);
        }

        formPane.getChildren().add(formCard);

        root.setLeft(leftPanel);
        root.setCenter(formPane);

        // Fade in
        formCard.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(500), formCard);
        ft.setFromValue(0); ft.setToValue(1);
        ft.play();
    }

    private void handleLogin(String id, String password, Label errorLabel) {
        if (id.isEmpty() || password.isEmpty()) {
            showError(errorLabel, "Please enter your ID and password.");
            return;
        }

        String authenticated = VoteService.getInstance().authenticate(id, password);

        if (authenticated == null) {
            showError(errorLabel, "Invalid credentials. Please try again.");
            return;
        }

        // Role mismatch check
        if (!authenticated.equals(role)) {
            showError(errorLabel, "These credentials belong to the " + authenticated + " portal.");
            return;
        }

        // Navigate based on confirmed role
        switch (authenticated) {
            case "ADMIN":
                root.getScene().setRoot(new AdminDashboard(stage).getView());
                break;
            case "CANDIDATE":
                root.getScene().setRoot(new CandidateDashboard(stage).getView());
                break;
            case "VOTER":
                root.getScene().setRoot(new VoterDashboard(stage).getView());
                break;
        }
    }

    private void showError(Label errorLabel, String message) {
        errorLabel.setText("⚠  " + message);
        errorLabel.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(300), errorLabel);
        ft.setFromValue(0); ft.setToValue(1);
        ft.play();
    }

    public BorderPane getView() {
        return root;
    }
}