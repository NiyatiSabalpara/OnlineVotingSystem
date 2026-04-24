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
        root.setStyle("-fx-background-color: #0d0f1a;");
        buildUI();
    }

    private void buildUI() {
        VBox centerBox = new VBox(0);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(40));

        VBox card = new VBox(20);
        card.getStyleClass().add("register-card");
        card.setMaxWidth(440);
        card.setAlignment(Pos.TOP_LEFT);

        // Header
        Label badge = new Label("🗳  VOTER REGISTRATION");
        badge.setStyle("-fx-font-size: 10px; -fx-font-weight: 800; -fx-text-fill: #00e5a0; "
                + "-fx-background-color: rgba(0,229,160,0.1); -fx-background-radius: 20; "
                + "-fx-padding: 4 12 4 12; -fx-border-color: rgba(0,229,160,0.2); "
                + "-fx-border-radius: 20; -fx-border-width: 1;");

        Label title = new Label("Create your account");
        title.getStyleClass().add("register-title");

        Label subtitle = new Label("Register to participate in the democratic process");
        subtitle.getStyleClass().add("register-subtitle");

        // Divider
        Region div1 = new Region();
        div1.getStyleClass().add("divider-line");

        // Fields
        VBox nameBox = makeField("Full Name", "Enter your full name", false);
        TextField nameFld = (TextField) nameBox.getChildren().get(1);

        VBox emailBox = makeField("Email Address", "your@email.com", false);
        TextField emailFld = (TextField) emailBox.getChildren().get(1);

        VBox mobileBox = makeField("Mobile Number", "10-digit phone number", false);
        TextField mobileFld = (TextField) mobileBox.getChildren().get(1);

        VBox passBox = makeField("Password", "Create a strong password", true);
        PasswordField passFld = (PasswordField) passBox.getChildren().get(1);

        // Error label
        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        // Register Button
        Button registerBtn = new Button("Create Voter Account");
        registerBtn.getStyleClass().add("btn-primary-teal");
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        registerBtn.setOnAction(e -> handleRegister(
                nameFld.getText(), mobileFld.getText(), emailFld.getText(), passFld.getText(), errorLabel
        ));

        // Back link
        Button backBtn = new Button("← Back to Login");
        backBtn.getStyleClass().add("btn-ghost");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> root.getScene().setRoot(new LoginView(stage, "VOTER").getView()));

        card.getChildren().addAll(badge, title, subtitle, div1,
                nameBox, emailBox, mobileBox, passBox,
                errorLabel, registerBtn, backBtn);

        centerBox.getChildren().add(card);
        root.getChildren().add(centerBox);

        // Fade in
        card.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(500), card);
        ft.setFromValue(0); ft.setToValue(1);
        ft.play();
    }

    private VBox makeField(String labelText, String prompt, boolean isPassword) {
        VBox box = new VBox(8);
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("form-label");
        Control field = isPassword ? new PasswordField() : new TextField();
        if (field instanceof TextField) ((TextField) field).setPromptText(prompt);
        else ((PasswordField) field).setPromptText(prompt);
        field.getStyleClass().addAll("form-field", "form-field-teal");
        box.getChildren().addAll(lbl, field);
        return box;
    }

    private void handleRegister(String name, String mobile, String email, String password, Label errorLabel) {
        if (name.isEmpty() || mobile.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError(errorLabel, "Please fill in all fields.");
            return;
        }
        if (mobile.length() < 10) {
            showError(errorLabel, "Please enter a valid 10-digit mobile number.");
            return;
        }
        if (password.length() < 4) {
            showError(errorLabel, "Password must be at least 4 characters.");
            return;
        }

        String newId = VoteService.getInstance().registerVoter(name, mobile, email, password);

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Registration Successful");
        success.setHeaderText("Welcome, " + name + "! 🎉");
        success.setContentText("Your Voter ID is: " + newId + "\n\nPlease remember your ID to log in.");
        success.showAndWait();

        root.getScene().setRoot(new LoginView(stage, "VOTER").getView());
    }

    private void showError(Label errorLabel, String message) {
        errorLabel.setText("⚠  " + message);
        errorLabel.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(300), errorLabel);
        ft.setFromValue(0); ft.setToValue(1);
        ft.play();
    }

    public StackPane getView() {
        return root;
    }
}
