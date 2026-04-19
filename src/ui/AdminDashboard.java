package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AdminDashboard {

    private BorderPane view = new BorderPane();

    public AdminDashboard(Stage stage) {

        // Header
        Label title = new Label("ADMIN DASHBOARD");
        title.getStyleClass().add("header-text");
        title.setStyle("-fx-text-fill: white;");

        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("top-header");

        // Content Action Board
        VBox contentBox = new VBox(30);
        contentBox.setPadding(new Insets(40));
        contentBox.setAlignment(Pos.TOP_CENTER);

        // Card container for actions
        VBox actionCard = new VBox(20);
        actionCard.getStyleClass().add("card");
        actionCard.setMaxWidth(400);
        actionCard.setAlignment(Pos.CENTER);

        Label prompt = new Label("Quick Actions");
        prompt.getStyleClass().add("subheader-text");

        Button generateId = new Button("Generate New Voter ID");
        generateId.getStyleClass().add("button-primary");
        generateId.setPrefWidth(Double.MAX_VALUE);
        generateId.setOnAction(e -> new Alert(Alert.AlertType.INFORMATION, "Generated sample ID: VOTER-" + (int)(Math.random() * 10000)).show());

        Button viewResults = new Button("View Live Results");
        viewResults.getStyleClass().add("button-primary");
        viewResults.setPrefWidth(Double.MAX_VALUE);

        viewResults.setOnAction(e -> {
            stage.getScene().setRoot(new ResultWindow(stage).getView());
        });

        actionCard.getChildren().addAll(prompt, generateId, viewResults);

        // Logout
        Button logout = new Button("Logout");
        logout.getStyleClass().add("button-secondary");
        logout.setOnAction(e -> stage.getScene().setRoot(new LoginView(stage).getView()));

        contentBox.getChildren().addAll(actionCard, logout);

        view.setTop(header);
        view.setCenter(contentBox);
    }

    public BorderPane getView() {
        return view;
    }
}