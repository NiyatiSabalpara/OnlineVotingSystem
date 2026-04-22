package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import service.VoteService;

public class RegisterView {

    private BorderPane root = new BorderPane();

    public RegisterView(Stage stage) {
        // HEADER
        Label header = new Label("ONLINE VOTING SYSTEM - REGISTER");
        header.getStyleClass().add("header-text");
        header.setStyle("-fx-text-fill: white;");

        HBox headerBox = new HBox(header);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.getStyleClass().add("top-header");

        // REGISTRATION FORM
        Label nameLabel = new Label("Full Name");
        nameLabel.getStyleClass().add("label");
        TextField name = new TextField();
        name.setPromptText("Enter Full Name");
        name.getStyleClass().add("text-field");

        Label mobileLabel = new Label("Mobile Number");
        mobileLabel.getStyleClass().add("label");
        TextField mobile = new TextField();
        mobile.setPromptText("Enter Mobile");
        mobile.getStyleClass().add("text-field");

        Label emailLabel = new Label("Email Address");
        emailLabel.getStyleClass().add("label");
        TextField email = new TextField();
        email.setPromptText("Enter Email");
        email.getStyleClass().add("text-field");

        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("label");
        PasswordField pass = new PasswordField();
        pass.setPromptText("Create Password");
        pass.getStyleClass().add("text-field");
        
        Button register = new Button("Register");
        register.getStyleClass().add("button-primary");
        register.setPrefWidth(Double.MAX_VALUE);

        register.setOnAction(e -> {
            if (name.getText().isEmpty() || mobile.getText().isEmpty() || email.getText().isEmpty() || pass.getText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please fill all fields!").show();
                return;
            }

            String newId = VoteService.getInstance().registerVoter(name.getText(), mobile.getText(), email.getText(), pass.getText());
            
            Alert success = new Alert(Alert.AlertType.INFORMATION, "Registration Successful!\nYour new Voter ID is: " + newId + "\nPlease memorize this to login.");
            success.setHeaderText("Welcome!");
            success.showAndWait();

            stage.getScene().setRoot(new LoginView(stage).getView());
        });

        Button back = new Button("Back to Login");
        back.getStyleClass().add("button-secondary");
        back.setPrefWidth(Double.MAX_VALUE);
        back.setOnAction(e -> stage.getScene().setRoot(new LoginView(stage).getView()));

        VBox form = new VBox(15,
                nameLabel, name,
                mobileLabel, mobile,
                emailLabel, email,
                passLabel, pass,
                new Label(""),
                register,
                back
        );

        form.setAlignment(Pos.CENTER_LEFT);
        form.setMaxWidth(350);
        form.getStyleClass().add("card");

        StackPane centerPane = new StackPane(form);
        centerPane.setStyle("-fx-background-color: #f0f4f8;");

        root.setTop(headerBox);
        root.setCenter(centerPane);
    }

    public BorderPane getView() {
        return root;
    }
}
