package ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.VoteService;

public class RegisterView {

    private StackPane root = new StackPane();
    private Stage stage;

    public RegisterView(Stage stage) {
        this.stage = stage;
        root.setStyle("-fx-background-color: " + ThemeManager.bgBase() + ";");
        buildUI();
    }

    private void buildUI() {
        VBox center = new VBox(0);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(36));

        VBox card = new VBox(18);
        card.setStyle(ThemeManager.glassCard() + " -fx-max-width: 440;");
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(440);

        Label badge = new Label("🗳  VOTER REGISTRATION");
        badge.setStyle("-fx-font-size: 10px; -fx-font-weight: 800; -fx-text-fill: " + ThemeManager.accentTeal() + "; "
                + "-fx-background-color: " + ThemeManager.voterAccentBg() + "; -fx-background-radius: 20; "
                + "-fx-padding: 4 12 4 12; -fx-border-color: rgba(0,201,138,0.25); "
                + "-fx-border-radius: 20; -fx-border-width: 1;");

        Label title = new Label("Create your account");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");

        Label subtitle = new Label("Register to participate in the democratic process");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");

        Region div = makeDivider();

        VBox nameBox  = makeField("Full Name",      "Enter your full name",     false);
        VBox emailBox = makeField("Email Address",  "your@email.com",           false);
        VBox mobBox   = makeField("Mobile Number",  "10-digit phone number",    false);
        VBox passBox  = makeField("Password",       "Create a strong password", true);

        TextField   nameFld  = (TextField)   nameBox.getChildren().get(1);
        TextField   emailFld = (TextField)   emailBox.getChildren().get(1);
        TextField   mobFld   = (TextField)   mobBox.getChildren().get(1);
        PasswordField passFld = (PasswordField) passBox.getChildren().get(1);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ff5470; -fx-font-weight: 700;");
        errorLabel.setVisible(false);

        Button regBtn = new Button("Create Voter Account");
        regBtn.setStyle("-fx-background-color: " + ThemeManager.accentTeal() + "; "
                + "-fx-text-fill: #0d0f1a; -fx-font-size: 14px; -fx-font-weight: 800; "
                + "-fx-background-radius: 12; -fx-padding: 12 0 12 0; -fx-cursor: hand; "
                + "-fx-border-color: transparent;");
        regBtn.setMaxWidth(Double.MAX_VALUE);
        regBtn.setOnAction(e -> handleRegister(
                nameFld.getText(), mobFld.getText(), emailFld.getText(), passFld.getText(), errorLabel));

        Button backBtn = new Button("← Back to Login");
        backBtn.setStyle(ThemeManager.navNormal() + "-fx-max-width: 9999;");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> root.getScene().setRoot(new LoginView(stage, "VOTER").getView()));

        card.getChildren().addAll(badge, title, subtitle, div,
                nameBox, emailBox, mobBox, passBox, errorLabel, regBtn, backBtn);
        center.getChildren().add(card);
        root.getChildren().add(center);

        card.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(450), card);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    private VBox makeField(String label, String prompt, boolean isPassword) {
        VBox box = new VBox(8);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: " + ThemeManager.textSecondary() + ";");
        Control field = isPassword ? new PasswordField() : new TextField();
        if (field instanceof TextField) ((TextField) field).setPromptText(prompt);
        else ((PasswordField) field).setPromptText(prompt);
        field.setStyle("-fx-pref-height: 42px; -fx-background-color: " + ThemeManager.inputBg() + "; "
                + "-fx-background-radius: 11; -fx-border-color: " + ThemeManager.inputBorder() + "; "
                + "-fx-border-radius: 11; -fx-border-width: 1; -fx-padding: 0 12 0 12; "
                + "-fx-font-size: 14px; -fx-text-fill: " + ThemeManager.textPrimary() + "; "
                + "-fx-prompt-text-fill: " + ThemeManager.textMuted() + ";");
        box.getChildren().addAll(lbl, field);
        return box;
    }

    private Region makeDivider() {
        Region r = new Region();
        r.setStyle("-fx-background-color: " + ThemeManager.divider() + "; -fx-min-height: 1; -fx-max-height: 1;");
        return r;
    }

    private void handleRegister(String name, String mobile, String email, String pass, Label err) {
        if (name.isBlank() || mobile.isBlank() || email.isBlank() || pass.isBlank()) {
            showError(err, "Please fill in all fields."); return;
        }
        if (mobile.length() < 10) { showError(err, "Enter a valid 10-digit mobile number."); return; }
        if (pass.length() < 4)    { showError(err, "Password must be at least 4 characters."); return; }

        String newId = VoteService.getInstance().registerVoter(name, mobile, email, pass);
        Alert ok = new Alert(Alert.AlertType.INFORMATION);
        ok.setTitle("Registration Successful"); ok.setHeaderText("Welcome, " + name + "! 🎉");
        ok.setContentText("Your Voter ID is: " + newId + "\n\nPlease remember your ID to log in.");
        ok.showAndWait();
        root.getScene().setRoot(new LoginView(stage, "VOTER").getView());
    }

    private void showError(Label lbl, String msg) {
        lbl.setText("⚠  " + msg); lbl.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(280), lbl);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    public StackPane getView() { return root; }
}
