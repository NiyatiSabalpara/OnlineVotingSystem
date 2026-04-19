package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Candidate;
import service.VoteService;

public class CandidateDashboard {

    private BorderPane view = new BorderPane();

    public CandidateDashboard(Stage stage) {

        // Header
        Label title = new Label("CANDIDATE DASHBOARD");
        title.getStyleClass().add("header-text");
        title.setStyle("-fx-text-fill: white;");

        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("top-header");

        // Content
        VBox contentBox = new VBox(30);
        contentBox.setPadding(new Insets(40));
        contentBox.setAlignment(Pos.TOP_CENTER);

        // Stats Card
        VBox statsCard = new VBox(15);
        statsCard.getStyleClass().add("card");
        statsCard.setAlignment(Pos.CENTER);
        statsCard.setMaxWidth(600);

        Label welcome = new Label("Live Campaign Stats");
        welcome.getStyleClass().add("subheader-text");

        // Chart setup
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Candidates");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Votes");
        yAxis.setTickUnit(1);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Real-Time Election Returns");
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Candidate c : VoteService.getInstance().getCandidates()) {
            series.getData().add(new XYChart.Data<>(c.getName(), c.getVoteCount()));
        }
        barChart.getData().add(series);

        statsCard.getChildren().addAll(welcome, barChart);

        // Logout Button
        Button logout = new Button("Logout");
        logout.getStyleClass().add("button-secondary");
        logout.setOnAction(e -> stage.getScene().setRoot(new LoginView(stage).getView()));

        contentBox.getChildren().addAll(statsCard, logout);

        view.setTop(header);
        view.setCenter(contentBox);
    }

    public BorderPane getView() {
        return view;
    }
}