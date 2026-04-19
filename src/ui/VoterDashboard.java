package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import service.SmsService;
import service.VoteService;
import model.Candidate;
import model.User;

public class VoterDashboard {

    private BorderPane view = new BorderPane();

    public VoterDashboard(Stage stage) {

        User currentUser = VoteService.getInstance().getCurrentUser();
        boolean hasVoted = currentUser != null && currentUser.hasVoted();

        // Header
        Label title = new Label("VOTER DASHBOARD");
        title.getStyleClass().add("header-text");
        title.setStyle("-fx-text-fill: white;");

        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("top-header");

        // Content
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(30));
        contentBox.setAlignment(Pos.TOP_CENTER);

        Label prompt = new Label("Select your preferred candidate:");
        prompt.getStyleClass().add("subheader-text");
        
        VBox candidatesBox = new VBox(15);
        candidatesBox.getStyleClass().add("card");
        candidatesBox.setMaxWidth(500);

        ToggleGroup group = new ToggleGroup();

        for (Candidate c : VoteService.getInstance().getCandidates()) {
            RadioButton rb = new RadioButton(c.getName());
            rb.setUserData(c.getCandidateId());
            rb.setToggleGroup(group);
            rb.getStyleClass().add("label");
            if (hasVoted) rb.setDisable(true);
            candidatesBox.getChildren().add(rb);
        }

        Button vote = new Button(hasVoted ? "Already Voted" : "Cast Vote");
        vote.getStyleClass().add("button-primary");
        vote.setPrefWidth(200);
        if (hasVoted) vote.setDisable(true);

        vote.setOnAction(e -> {
            Toggle selected = group.getSelectedToggle();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Please select a candidate before casting your vote.").show();
                return;
            }

            int candidateId = (int) selected.getUserData();
            boolean success = VoteService.getInstance().castVote(candidateId);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Vote Cast Successfully!");
                alert.setHeaderText(null);
                alert.show();
                
                if (currentUser != null) {
                    SmsService.send(currentUser.getMobile(), "Your vote has been cast. Thank you!");
                }

                vote.setDisable(true);
                vote.setText("Already Voted");
                for (Toggle t : group.getToggles()) {
                    ((RadioButton) t).setDisable(true);
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Vote casting failed! You might have already voted.").show();
            }
        });

        // Logout Button
        Button logout = new Button("Logout");
        logout.getStyleClass().add("button-secondary");
        logout.setOnAction(e -> {
            VoteService.getInstance().logout();
            stage.getScene().setRoot(new LoginView(stage).getView());
        });

        HBox actions = new HBox(15, vote, logout);
        actions.setAlignment(Pos.CENTER);

        contentBox.getChildren().addAll(prompt, candidatesBox, actions);

        view.setTop(header);
        view.setCenter(contentBox);
    }

    public BorderPane getView() {
        return view;
    }
}