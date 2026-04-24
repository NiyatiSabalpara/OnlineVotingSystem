package ui;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Candidate;
import model.User;
import service.VoteService;

public class CandidateDashboard {

    private BorderPane view = new BorderPane();
    private Stage stage;
    private ScrollPane contentScroll;
    private Button[] navBtns;

    // Identify the current candidate by name
    private String candidateName = "Unknown";
    private Candidate myCandidate = null;

    public CandidateDashboard(Stage stage) {
        this.stage = stage;
        view.setStyle("-fx-background-color: #0d0f1a;");
        detectCandidate();
        buildSidebar();
        showPanel(buildMyStatsPanel());
    }

    private void detectCandidate() {
        User u = VoteService.getInstance().getCurrentUser();
        if (u != null) {
            candidateName = u.getName();
            for (Candidate c : VoteService.getInstance().getCandidates()) {
                if (c.getName().equals(candidateName)) {
                    myCandidate = c;
                    break;
                }
            }
        }
    }

    private void buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(230);
        sidebar.setMinWidth(230);

        // Brand
        HBox brandBox = new HBox(4);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(28, 20, 28, 20));
        Label brandLabel = new Label("Pollaroid");
        brandLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #ffffff;");
        Label brandDot = new Label(".");
        brandDot.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #00d4ff;");
        brandBox.getChildren().addAll(brandLabel, brandDot);

        String[][] navItems = {
            {"📊", "My Stats"},
            {"🏆", "Live Results"},
            {"📋", "Campaign Info"}
        };

        VBox navList = new VBox(2);
        navList.setPadding(new Insets(0, 10, 0, 10));

        Label menuHeader = new Label("CANDIDATE MENU");
        menuHeader.getStyleClass().add("sidebar-section-label");
        navList.getChildren().add(menuHeader);

        navBtns = new Button[navItems.length];
        for (int i = 0; i < navItems.length; i++) {
            final int idx = i;
            Button btn = new Button(navItems[i][0] + "  " + navItems[i][1]);
            btn.getStyleClass().add("sidebar-nav-btn");
            btn.setMaxWidth(Double.MAX_VALUE);
            navBtns[i] = btn;

            btn.setOnAction(e -> {
                setActiveNav(idx);
                switch (idx) {
                    case 0: showPanel(buildMyStatsPanel()); break;
                    case 1: showPanel(buildLiveResultsPanel()); break;
                    case 2: showPanel(buildCampaignInfoPanel()); break;
                }
            });
            navList.getChildren().add(btn);
        }
        setActiveNav(0);

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);

        // Logout
        Button logoutBtn = new Button("🚪  Logout");
        logoutBtn.getStyleClass().add("sidebar-nav-btn");
        logoutBtn.setStyle("-fx-text-fill: #ff5470;");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> {
            VoteService.getInstance().logout();
            view.getScene().setRoot(new LandingView(stage).getView());
        });
        VBox logoutBox = new VBox(logoutBtn);
        logoutBox.setPadding(new Insets(0, 10, 20, 10));

        // User card
        HBox userCard = new HBox(12);
        userCard.setAlignment(Pos.CENTER_LEFT);
        userCard.setPadding(new Insets(15, 20, 15, 20));
        userCard.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 1 0 0 0;");
        StackPane ava = new StackPane();
        ava.setStyle("-fx-background-color: rgba(0,212,255,0.15); -fx-background-radius: 50; -fx-min-width: 36; -fx-min-height: 36; -fx-max-width: 36; -fx-max-height: 36;");
        Label avaIcon = new Label("🎯"); avaIcon.setStyle("-fx-font-size: 16px;");
        ava.getChildren().add(avaIcon);
        VBox userInfo = new VBox(2);
        Label uName = new Label(candidateName); uName.getStyleClass().add("sidebar-user-name"); uName.setWrapText(false);
        Label uRole = new Label("Candidate"); uRole.getStyleClass().add("sidebar-user-role");
        userInfo.getChildren().addAll(uName, uRole);
        userCard.getChildren().addAll(ava, userInfo);

        sidebar.getChildren().addAll(brandBox, navList, spacer, logoutBox, userCard);

        contentScroll = new ScrollPane();
        contentScroll.setFitToWidth(true);
        contentScroll.setStyle("-fx-background-color: transparent; -fx-background: #0d0f1a;");

        view.setLeft(sidebar);
        view.setCenter(contentScroll);
    }

    private void setActiveNav(int activeIdx) {
        if (navBtns == null) return;
        for (int i = 0; i < navBtns.length; i++) {
            navBtns[i].getStyleClass().removeAll("sidebar-nav-btn-active-cyan");
            if (i == activeIdx) navBtns[i].getStyleClass().add("sidebar-nav-btn-active-cyan");
        }
    }

    private void showPanel(VBox panel) {
        contentScroll.setContent(panel);
        panel.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(300), panel);
        ft.setFromValue(0); ft.setToValue(1);
        ft.play();
    }

    // ================================================================
    // PANEL 1: MY STATS
    // ================================================================
    private VBox buildMyStatsPanel() {
        VBox panel = new VBox(28);
        panel.getStyleClass().add("content-area");

        VBox pageHeader = new VBox(6);
        Label title = new Label("My Campaign Stats");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Welcome back, " + candidateName + ". Here's your live election standing.");
        sub.getStyleClass().add("page-subtitle");
        pageHeader.getChildren().addAll(title, sub);

        VoteService vs = VoteService.getInstance();
        int myVotes = myCandidate != null ? myCandidate.getVoteCount() : 0;
        int totalVotes = vs.getTotalVotesCast();
        double myShare = totalVotes > 0 ? (myVotes * 100.0 / totalVotes) : 0;
        int myRank = computeRank();

        // KPIs
        HBox kpiRow = new HBox(15);
        kpiRow.getChildren().addAll(
            makeKpiCard("🗳️", "My Votes", String.valueOf(myVotes), "Live count", true, "rgba(0,212,255,0.12)"),
            makeKpiCard("📊", "Total Cast", String.valueOf(totalVotes), "All candidates", true, "rgba(108,99,255,0.12)"),
            makeKpiCard("🏅", "My Rank", "#" + myRank, "Current position", myRank == 1, "rgba(255,193,7,0.12)"),
            makeKpiCard("📈", "Vote Share", String.format("%.1f%%", myShare), "Of total votes", true, "rgba(0,229,160,0.12)")
        );

        // Chart — Compare all candidates
        VBox chartCard = new VBox(15);
        chartCard.getStyleClass().add("glass-card");

        HBox chartHeader = new HBox(10);
        chartHeader.setAlignment(Pos.CENTER_LEFT);
        Label chartTitle = new Label("Vote Comparison — All Candidates");
        chartTitle.getStyleClass().add("card-title");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label liveTag = new Label("● LIVE");
        liveTag.setStyle("-fx-font-size: 11px; -fx-text-fill: #00e5a0; -fx-font-weight: 800;");
        chartHeader.getChildren().addAll(chartTitle, spacer, liveTag);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        barChart.setMinHeight(240);
        barChart.getStyleClass().add("chart");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Candidate c : vs.getCandidates()) {
            XYChart.Data<String, Number> d = new XYChart.Data<>(c.getName(), c.getVoteCount());
            series.getData().add(d);
        }
        barChart.getData().add(series);

        chartCard.getChildren().addAll(chartHeader, barChart);

        // Progress bars card
        VBox progressCard = new VBox(15);
        progressCard.getStyleClass().add("glass-card");
        Label progTitle = new Label("Vote Progress by Candidate");
        progTitle.getStyleClass().add("card-title");
        progressCard.getChildren().add(progTitle);

        String[] pbColors = {"vote-progress-bar-cyan", "vote-progress-bar", "vote-progress-bar-teal"};
        String[] hexColors = {"#00d4ff", "#6c63ff", "#00e5a0"};
        int ci = 0;
        for (Candidate c : vs.getCandidates()) {
            VBox pRow = new VBox(6);
            HBox labelRow = new HBox(10); labelRow.setAlignment(Pos.CENTER_LEFT);
            Label cn = new Label(c.getName()); cn.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: " + hexColors[ci % 3] + ";");
            Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS);
            Label vt = new Label(c.getVoteCount() + " votes"); vt.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892b0;");
            if (c.equals(myCandidate)) {
                Label you = new Label("You"); you.setStyle("-fx-font-size: 10px; -fx-text-fill: #00d4ff; -fx-font-weight: 800; "
                        + "-fx-background-color: rgba(0,212,255,0.1); -fx-background-radius: 10; -fx-padding: 2 8 2 8;");
                labelRow.getChildren().addAll(cn, you, r, vt);
            } else {
                labelRow.getChildren().addAll(cn, r, vt);
            }
            ProgressBar pb = new ProgressBar(totalVotes > 0 ? (double)c.getVoteCount()/totalVotes : 0);
            pb.setMaxWidth(Double.MAX_VALUE); pb.setPrefHeight(10);
            pb.getStyleClass().add(pbColors[ci % 3]);
            pRow.getChildren().addAll(labelRow, pb);
            progressCard.getChildren().add(pRow);
            ci++;
        }

        panel.getChildren().addAll(pageHeader, kpiRow, chartCard, progressCard);
        return panel;
    }

    // ================================================================
    // PANEL 2: LIVE RESULTS
    // ================================================================
    private VBox buildLiveResultsPanel() {
        VBox panel = new VBox(25);
        panel.getStyleClass().add("content-area");

        Label title = new Label("Live Results");
        title.getStyleClass().add("page-title");

        VoteService vs = VoteService.getInstance();
        Candidate leader = vs.getWinner();

        // Leader card
        if (leader != null && leader.getVoteCount() > 0) {
            HBox winnerCard = new HBox(20);
            winnerCard.getStyleClass().add("winner-card");
            winnerCard.setAlignment(Pos.CENTER_LEFT);
            Label wTrophy = new Label("🏆"); wTrophy.setStyle("-fx-font-size: 36px;");
            VBox wInfo = new VBox(4);
            Label wLabel = new Label("CURRENTLY LEADING"); wLabel.getStyleClass().add("winner-label");
            Label wName = new Label(leader.getName()); wName.getStyleClass().add("winner-name");
            Label wVotes = new Label(leader.getVoteCount() + " votes · " + leader.getParty());
            wVotes.getStyleClass().add("winner-label");
            wInfo.getChildren().addAll(wLabel, wName, wVotes);
            winnerCard.getChildren().addAll(wTrophy, wInfo);
            panel.getChildren().addAll(title, winnerCard);
        } else {
            panel.getChildren().add(title);
        }

        // Pie + Leaderboard
        HBox chartsRow = new HBox(20);

        VBox pieCard = new VBox(15);
        pieCard.getStyleClass().add("glass-card");
        HBox.setHgrow(pieCard, Priority.ALWAYS);
        Label pieTitle = new Label("Vote Distribution");
        pieTitle.getStyleClass().add("card-title");
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Candidate c : vs.getCandidates())
            pieData.add(new PieChart.Data(c.getName() + " (" + c.getVoteCount() + ")", Math.max(c.getVoteCount(), 1)));
        PieChart pie = new PieChart(pieData);
        pie.setLegendVisible(true);
        pie.setLabelsVisible(true);
        pie.setMinHeight(250);
        pieCard.getChildren().addAll(pieTitle, pie);

        VBox lbCard = new VBox(12);
        lbCard.getStyleClass().add("glass-card");
        lbCard.setPrefWidth(300);
        Label lbTitle = new Label("Standings");
        lbTitle.getStyleClass().add("card-title");
        lbCard.getChildren().add(lbTitle);

        int rank = 1; int lbTotal = vs.getTotalVotesCast();
        String[] lbColors = {"#ffc107", "#6c63ff", "#00d4ff"};
        for (Candidate c : vs.getCandidates()) {
            HBox row = new HBox(12); row.setAlignment(Pos.CENTER_LEFT); row.setPadding(new Insets(8, 0, 8, 0));
            Label rnk = new Label("#" + rank);
            rnk.setStyle("-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: " + lbColors[Math.min(rank-1,2)] + "; -fx-min-width: 35;");
            VBox info = new VBox(3); HBox.setHgrow(info, Priority.ALWAYS);
            Label cn = new Label(c.getName()); cn.getStyleClass().add("leaderboard-name");
            boolean isMe = c.equals(myCandidate);
            if (isMe) {
                HBox nameRow = new HBox(8); nameRow.setAlignment(Pos.CENTER_LEFT);
                Label you = new Label("YOU"); you.setStyle("-fx-font-size: 9px; -fx-text-fill: #00d4ff; -fx-font-weight: 800; "
                        +"-fx-background-color: rgba(0,212,255,0.1); -fx-background-radius: 8; -fx-padding: 1 6 1 6;");
                nameRow.getChildren().addAll(cn, you);
                info.getChildren().add(nameRow);
            } else {
                info.getChildren().add(cn);
            }
            Label cp = new Label(c.getParty()); cp.getStyleClass().add("leaderboard-party");
            info.getChildren().add(cp);
            Label vt = new Label(String.valueOf(c.getVoteCount())); vt.getStyleClass().add("leaderboard-votes");
            row.getChildren().addAll(rnk, info, vt);
            lbCard.getChildren().add(row);
            rank++;
        }

        chartsRow.getChildren().addAll(pieCard, lbCard);
        panel.getChildren().add(chartsRow);
        return panel;
    }

    // ================================================================
    // PANEL 3: CAMPAIGN INFO
    // ================================================================
    private VBox buildCampaignInfoPanel() {
        VBox panel = new VBox(25);
        panel.getStyleClass().add("content-area");

        Label title = new Label("Campaign Info");
        title.getStyleClass().add("page-title");

        VBox profileCard = new VBox(20);
        profileCard.getStyleClass().add("glass-card");
        profileCard.setMaxWidth(520);

        StackPane ava = new StackPane();
        ava.setStyle("-fx-background-color: rgba(0,212,255,0.15); -fx-background-radius: 50; "
                + "-fx-min-width: 80; -fx-min-height: 80; -fx-max-width: 80; -fx-max-height: 80;");
        Label avaIcon = new Label("🎯"); avaIcon.setStyle("-fx-font-size: 36px;");
        ava.getChildren().add(avaIcon);

        Label nameLabel = new Label(candidateName);
        nameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #e8eaf6;");
        Label partyLabel = new Label(myCandidate != null ? myCandidate.getParty() : "Independent");
        partyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #00d4ff;");

        Region div = new Region(); div.getStyleClass().add("divider-line");
        Label secTitle = new Label("Candidate Details"); secTitle.getStyleClass().add("card-title");

        VBox fields = new VBox(14);
        fields.getChildren().addAll(
            makeProfileField("Candidate ID", myCandidate != null ? "CAND-00" + myCandidate.getCandidateId() : "-"),
            makeProfileField("Full Name", candidateName),
            makeProfileField("Party Affiliation", myCandidate != null ? myCandidate.getParty() : "Independent"),
            makeProfileField("Current Votes", myCandidate != null ? String.valueOf(myCandidate.getVoteCount()) : "0"),
            makeProfileField("Current Rank", "#" + computeRank()),
            makeProfileField("Election", "General Election 2026")
        );

        profileCard.getChildren().addAll(ava, nameLabel, partyLabel, div, secTitle, fields);
        panel.getChildren().addAll(title, profileCard);
        return panel;
    }

    // ================================================================
    // HELPERS
    // ================================================================
    private int computeRank() {
        if (myCandidate == null) return 0;
        int rank = 1;
        for (Candidate c : VoteService.getInstance().getCandidates()) {
            if (!c.equals(myCandidate) && c.getVoteCount() > myCandidate.getVoteCount()) rank++;
        }
        return rank;
    }

    private VBox makeKpiCard(String icon, String label, String value, String trend, boolean positive, String iconBg) {
        VBox card = new VBox(12);
        card.getStyleClass().add("kpi-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        StackPane iconBox = new StackPane();
        iconBox.setStyle("-fx-background-color: " + iconBg + "; -fx-background-radius: 12; -fx-min-width: 48; -fx-min-height: 48; -fx-max-width: 48; -fx-max-height: 48;");
        Label iconLabel = new Label(icon); iconLabel.setStyle("-fx-font-size: 22px;");
        iconBox.getChildren().add(iconLabel);
        Label lbl = new Label(label); lbl.getStyleClass().add("kpi-label");
        Label val = new Label(value); val.getStyleClass().add("kpi-value");
        Label tr = new Label((positive ? "↑" : "↓") + " " + trend);
        tr.getStyleClass().add(positive ? "kpi-trend-up" : "kpi-trend-down");
        card.getChildren().addAll(iconBox, lbl, val, tr);
        return card;
    }

    private HBox makeProfileField(String label, String value) {
        HBox row = new HBox(20); row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label); lbl.getStyleClass().add("profile-field-label"); lbl.setMinWidth(160);
        Label val = new Label(value); val.getStyleClass().add("profile-field-value");
        row.getChildren().addAll(lbl, val);
        return row;
    }

    public BorderPane getView() {
        return view;
    }
}