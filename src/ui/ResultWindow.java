package ui; 

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Candidate;
import service.VoteService;

public class ResultWindow {

    private BorderPane view = new BorderPane();

    public ResultWindow(Stage stage) {

        // Header
        Label title = new Label("ELECTION RESULTS");
        title.getStyleClass().add("header-text");
        title.setStyle("-fx-text-fill: white;");

        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("top-header");

        // Content
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_CENTER);

        // Leaderboard Card
        VBox boardCard = new VBox(15);
        boardCard.getStyleClass().add("card");
        boardCard.setMaxWidth(600);
        boardCard.setAlignment(Pos.CENTER);

        Label subTitle = new Label("Live Vote Distribution");
        subTitle.getStyleClass().add("subheader-text");

        // Configure Pie Chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Candidate c : VoteService.getInstance().getCandidates()) {
            // Only add to chart if they have votes to avoid crowding, or add all. 
            // We'll add all for visibility.
            pieChartData.add(new PieChart.Data(c.getName() + " (" + c.getVoteCount() + ")", c.getVoteCount()));
        }

        PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Votes by Candidate");
        chart.setLabelsVisible(true);
        chart.setLegendVisible(false); // Clean look

        boardCard.getChildren().addAll(subTitle, chart);

        // Back Button
        Button back = new Button("Back to Admin");
        back.getStyleClass().add("button-secondary");
        back.setOnAction(e -> stage.getScene().setRoot(new AdminDashboard(stage).getView()));

        contentBox.getChildren().addAll(boardCard, back);

        view.setTop(header);
        view.setCenter(contentBox);
    }

    public BorderPane getView() {
        return view;
    }
}