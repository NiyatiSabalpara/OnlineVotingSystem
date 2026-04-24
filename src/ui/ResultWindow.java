package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Candidate;
import service.VoteService;

public class ResultWindow {

    private BorderPane view = new BorderPane();

    public ResultWindow(Stage stage) {
        view.setStyle("-fx-background-color: #0d0f1a;");

        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(35, 40, 35, 40));

        // Header
        HBox headerRow = new HBox(15);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Live Election Results");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: #e8eaf6;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button backBtn = new Button("← Back to Admin");
        backBtn.getStyleClass().add("btn-ghost");
        backBtn.setOnAction(e -> stage.getScene().setRoot(new AdminDashboard(stage).getView()));
        headerRow.getChildren().addAll(title, spacer, backBtn);

        VoteService vs = VoteService.getInstance();
        Candidate winner = vs.getWinner();

        // Winner card
        if (winner != null && winner.getVoteCount() > 0) {
            HBox winnerCard = new HBox(20);
            winnerCard.getStyleClass().add("winner-card");
            winnerCard.setAlignment(Pos.CENTER_LEFT);
            Label wTrophy = new Label("🏆"); wTrophy.setStyle("-fx-font-size: 40px;");
            VBox wInfo = new VBox(5);
            Label wLabel = new Label("LEADING CANDIDATE");
            wLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: 800; -fx-text-fill: #ffc107;");
            Label wName = new Label(winner.getName());
            wName.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #ffc107;");
            Label wSub = new Label(winner.getVoteCount() + " votes · " + winner.getParty());
            wSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #8892b0;");
            wInfo.getChildren().addAll(wLabel, wName, wSub);
            Label winnerBadge = new Label("🏆  WINNING");
            winnerBadge.getStyleClass().add("badge-winner");
            Region wSpacer = new Region(); HBox.setHgrow(wSpacer, Priority.ALWAYS);
            winnerCard.getChildren().addAll(wTrophy, wInfo, wSpacer, winnerBadge);
            mainContent.getChildren().addAll(headerRow, winnerCard);
        } else {
            mainContent.getChildren().add(headerRow);
        }

        // Charts
        HBox chartsRow = new HBox(20);

        // Pie Chart
        VBox pieCard = new VBox(15);
        pieCard.getStyleClass().add("glass-card");
        HBox.setHgrow(pieCard, Priority.ALWAYS);
        Label pieTitle = new Label("Vote Distribution");
        pieTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Candidate c : vs.getCandidates())
            pieData.add(new PieChart.Data(c.getName() + " (" + c.getVoteCount() + ")", Math.max(c.getVoteCount(), 1)));
        PieChart pieChart = new PieChart(pieData);
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);
        pieChart.setLegendSide(javafx.geometry.Side.BOTTOM);
        pieChart.setMinHeight(280);
        pieCard.getChildren().addAll(pieTitle, pieChart);

        // Leaderboard Card
        VBox lbCard = new VBox(15);
        lbCard.getStyleClass().add("glass-card");
        lbCard.setPrefWidth(320);
        Label lbTitle = new Label("Leaderboard");
        lbTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");
        lbCard.getChildren().add(lbTitle);

        int rank = 1;
        int total = vs.getTotalVotesCast();
        String[] lbColors = {"#ffc107", "#6c63ff", "#00d4ff"};
        String[] pbClasses = {"vote-progress-bar", "vote-progress-bar-cyan", "vote-progress-bar-teal"};

        for (Candidate c : vs.getCandidates()) {
            VBox rowBox = new VBox(6);
            rowBox.setPadding(new Insets(8, 0, 8, 0));

            HBox topRow = new HBox(12);
            topRow.setAlignment(Pos.CENTER_LEFT);
            Label rnk = new Label("#" + rank);
            rnk.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + lbColors[Math.min(rank-1,2)] + "; -fx-min-width: 35;");
            VBox info = new VBox(3); HBox.setHgrow(info, Priority.ALWAYS);
            Label cn = new Label(c.getName()); cn.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: #e8eaf6;");
            Label cp = new Label(c.getParty()); cp.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892b0;");
            info.getChildren().addAll(cn, cp);
            Label vt = new Label(String.valueOf(c.getVoteCount()));
            vt.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: " + lbColors[Math.min(rank-1,2)] + ";");
            topRow.getChildren().addAll(rnk, info, vt);

            ProgressBar pb = new ProgressBar(total > 0 ? (double)c.getVoteCount()/total : 0);
            pb.setMaxWidth(Double.MAX_VALUE); pb.setPrefHeight(8);
            pb.getStyleClass().add(pbClasses[Math.min(rank-1, 2)]);

            rowBox.getChildren().addAll(topRow, pb);
            lbCard.getChildren().add(rowBox);
            rank++;
        }

        chartsRow.getChildren().addAll(pieCard, lbCard);
        mainContent.getChildren().add(chartsRow);

        ScrollPane scroll = new ScrollPane(mainContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: #0d0f1a;");

        view.setCenter(scroll);
    }

    public BorderPane getView() {
        return view;
    }
}