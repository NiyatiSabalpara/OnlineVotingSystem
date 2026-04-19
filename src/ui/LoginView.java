package ui;

import javafx.scene.Scene;
import service.SmsService;
import service.VoteService;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import service.SmsService;

public class LoginView {

    private BorderPane root = new BorderPane();

    public LoginView(Stage stage) {

        // HEADER
        Label header = new Label("ONLINE VOTING SYSTEM");
        header.getStyleClass().add("header-text");
        header.setStyle("-fx-text-fill: white;"); // Override just the text fill for the dark header

        HBox headerBox = new HBox(header);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.getStyleClass().add("top-header");

        // LOGIN FORM
        Label idLabel = new Label("User ID (e.g., admin/cand_alice/V101)");
        idLabel.getStyleClass().add("label");
        TextField id = new TextField();
        id.setPromptText("Enter User ID");
        id.getStyleClass().add("text-field");

        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("label");
        PasswordField pass = new PasswordField();
        pass.setPromptText("Enter Password");
        pass.getStyleClass().add("text-field");

        Button login = new Button("Login");
        login.getStyleClass().add("button-primary");
        login.setPrefWidth(Double.MAX_VALUE);

        Hyperlink registerLink = new Hyperlink("New voter? Register here");
        registerLink.setOnAction(e -> {
            Scene newScene = new Scene(new RegisterView(stage).getView(), 900, 600);
            newScene.getStylesheets().add("file:src/ui/style.css");
            stage.setScene(newScene);
        });

        login.setOnAction(e -> {
            String userId = id.getText();
            String password = pass.getText();

            if(userId.isEmpty() || password.isEmpty()){
                new Alert(Alert.AlertType.ERROR,"Please enter ID and Password").show();
                return;
            }

            String r = VoteService.getInstance().authenticate(userId, password);

            if(r == null) {
                new Alert(Alert.AlertType.ERROR, "Invalid Credentials!").show();
                return;
            }

            SmsService.send("9999999999","Login successful as " + r);

            if(r.equals("VOTER")){
                Scene newScene = new Scene(new VoterDashboard(stage).getView(), 900, 600);
                newScene.getStylesheets().add("file:src/ui/style.css");
                stage.setScene(newScene);
            }
            else if(r.equals("CANDIDATE")){
                Scene newScene = new Scene(new CandidateDashboard(stage).getView(), 900, 600);
                newScene.getStylesheets().add("file:src/ui/style.css");
                stage.setScene(newScene);
            }
            else if(r.equals("ADMIN")){
                Scene newScene = new Scene(new AdminDashboard(stage).getView(), 900, 600);
                newScene.getStylesheets().add("file:src/ui/style.css");
                stage.setScene(newScene);
            }
        });

        VBox form = new VBox(15,
                idLabel, id,
                passLabel, pass,
                new Label(""), // simple spacer
                login,
                registerLink
        );

        form.setAlignment(Pos.CENTER_LEFT);
        form.setMaxWidth(350);
        form.getStyleClass().add("card");

        StackPane centerPane = new StackPane(form);
        centerPane.setStyle("-fx-background-color: #f0f4f8;"); // Match root just in case

        root.setTop(headerBox);
        root.setCenter(centerPane);
    }

    public BorderPane getView(){
        return root;
    }
}