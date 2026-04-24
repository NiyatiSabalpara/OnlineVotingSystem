package ui;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Candidate;
import model.User;
import service.VoteService;

public class AdminDashboard {

    private BorderPane view = new BorderPane();
    private Stage stage;
    private Button[] navButtons;   // direct Button references for setActiveNav
    private ScrollPane contentScroll;

    public AdminDashboard(Stage stage) {
        this.stage = stage;
        view.setStyle("-fx-background-color: #0d0f1a;");
        buildSidebar();
        showPanel(buildDashboardPanel());
    }

    // ================================================================
    // SIDEBAR
    // ================================================================
    private void buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setStyle("-fx-background-color: #0a0c18; "
                + "-fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 0 1 0 0;");
        sidebar.setPrefWidth(220);
        sidebar.setMinWidth(220);

        // Brand
        HBox brandBox = new HBox(0);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(26, 20, 22, 20));
        Label bl = new Label("Pollaroid");
        bl.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #ffffff;");
        Label bd = new Label(".");
        bd.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #6c63ff;");
        brandBox.getChildren().addAll(bl, bd);

        // Section label
        Label menuHeader = new Label("MAIN MENU");
        menuHeader.setStyle("-fx-font-size: 10px; -fx-font-weight: 700; -fx-text-fill: #3d4466; "
                + "-fx-padding: 10 20 6 20;");

        // Nav items [icon + label]
        String[] icons  = {"🏠", "🗳️", "👥", "🧑", "📊", "⚙️"};
        String[] labels = {"Dashboard", "Election Control", "Voter Manager",
                           "Candidates", "Live Results", "Settings"};

        navButtons = new Button[icons.length];
        VBox navList = new VBox(2);
        navList.setPadding(new Insets(0, 10, 0, 10));
        navList.getChildren().add(menuHeader);

        for (int i = 0; i < icons.length; i++) {
            final int idx = i;
            Button btn = new Button(icons[i] + "   " + labels[i]);
            btn.setStyle(navNormalStyle());
            btn.setMaxWidth(Double.MAX_VALUE);
            navButtons[i] = btn;

            btn.setOnMouseEntered(e -> {
                if (!btn.getStyle().contains("#6c63ff")) {
                    btn.setStyle(navHoverStyle());
                }
            });
            btn.setOnMouseExited(e -> {
                if (!btn.getStyle().contains("#6c63ff")) {
                    btn.setStyle(navNormalStyle());
                }
            });

            btn.setOnAction(e -> {
                setActiveNav(idx);
                switch (idx) {
                    case 0: showPanel(buildDashboardPanel()); break;
                    case 1: showPanel(buildElectionControlPanel()); break;
                    case 2: showPanel(buildVoterManagerPanel()); break;
                    case 3: showPanel(buildCandidatesPanel()); break;
                    case 4: showPanel(buildLiveResultsPanel()); break;
                    case 5: showPanel(buildSettingsPanel()); break;
                }
            });
            navList.getChildren().add(btn);
        }
        setActiveNav(0);

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Logout section
        VBox logoutSection = new VBox(4);
        logoutSection.setPadding(new Insets(0, 10, 16, 10));
        Region topDivider = new Region();
        topDivider.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-min-height: 1; -fx-max-height: 1;");
        topDivider.setPadding(new Insets(0, 0, 8, 0));

        Button logoutBtn = new Button("🚪   Logout");
        logoutBtn.setStyle(navNormalStyle() + "-fx-text-fill: #ff5470;");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(navHoverStyle() + "-fx-text-fill: #ff5470;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(navNormalStyle() + "-fx-text-fill: #ff5470;"));
        logoutBtn.setOnAction(e -> {
            VoteService.getInstance().logout();
            view.getScene().setRoot(new LandingView(stage).getView());
        });
        logoutSection.getChildren().addAll(topDivider, logoutBtn);

        // Admin user card at the very bottom
        HBox userCard = new HBox(10);
        userCard.setAlignment(Pos.CENTER_LEFT);
        userCard.setPadding(new Insets(14, 16, 18, 16));
        userCard.setStyle("-fx-background-color: rgba(255,255,255,0.025);");

        StackPane ava = new StackPane();
        ava.setStyle("-fx-background-color: rgba(108,99,255,0.2); -fx-background-radius: 50; "
                + "-fx-min-width: 34; -fx-min-height: 34; -fx-max-width: 34; -fx-max-height: 34;");
        Label avaIcon = new Label("🛡️"); avaIcon.setStyle("-fx-font-size: 14px;");
        ava.getChildren().add(avaIcon);

        VBox userMeta = new VBox(2);
        Label uName = new Label("Administrator");
        uName.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #e8eaf6;");
        Label uRole = new Label("System Admin");
        uRole.setStyle("-fx-font-size: 11px; -fx-text-fill: #3d4466;");
        userMeta.getChildren().addAll(uName, uRole);
        userCard.getChildren().addAll(ava, userMeta);

        sidebar.getChildren().addAll(brandBox, navList, spacer, logoutSection, userCard);

        // Content scroll pane
        contentScroll = new ScrollPane();
        contentScroll.setFitToWidth(true);
        contentScroll.setStyle("-fx-background-color: transparent; -fx-background: #0d0f1a; -fx-border-color: transparent;");

        view.setLeft(sidebar);
        view.setCenter(contentScroll);
    }

    private String navNormalStyle() {
        return "-fx-background-color: transparent; -fx-text-fill: #8892b0; "
                + "-fx-font-size: 14px; -fx-font-weight: 600; -fx-alignment: center-left; "
                + "-fx-padding: 11 14 11 14; -fx-background-radius: 10; -fx-cursor: hand; "
                + "-fx-border-color: transparent;";
    }

    private String navHoverStyle() {
        return "-fx-background-color: rgba(255,255,255,0.04); -fx-text-fill: #e8eaf6; "
                + "-fx-font-size: 14px; -fx-font-weight: 600; -fx-alignment: center-left; "
                + "-fx-padding: 11 14 11 14; -fx-background-radius: 10; -fx-cursor: hand; "
                + "-fx-border-color: transparent;";
    }

    private String navActiveStyle() {
        return "-fx-background-color: rgba(108,99,255,0.14); -fx-text-fill: #6c63ff; "
                + "-fx-font-size: 14px; -fx-font-weight: 800; -fx-alignment: center-left; "
                + "-fx-padding: 11 14 11 11; -fx-background-radius: 10; -fx-cursor: hand; "
                + "-fx-border-color: #6c63ff; -fx-border-width: 0 0 0 3; -fx-border-radius: 2 10 10 2;";
    }

    private void setActiveNav(int activeIdx) {
        if (navButtons == null) return;
        for (int i = 0; i < navButtons.length; i++) {
            navButtons[i].setStyle(i == activeIdx ? navActiveStyle() : navNormalStyle());
        }
    }

    private void showPanel(VBox panel) {
        contentScroll.setContent(panel);
        panel.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(280), panel);
        ft.setFromValue(0); ft.setToValue(1);
        ft.play();
    }

    // ================================================================
    // PANEL 1 — DASHBOARD
    // ================================================================
    private VBox buildDashboardPanel() {
        VBox panel = new VBox(24);
        panel.setPadding(new Insets(32, 36, 32, 36));
        panel.setStyle("-fx-background-color: #0d0f1a;");

        // Header
        VBox pageHeader = new VBox(4);
        Label title = new Label("Dashboard Overview");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #e8eaf6;");
        Label subtitle = new Label("Welcome back, Administrator. Here's your election snapshot.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #8892b0;");
        pageHeader.getChildren().addAll(title, subtitle);

        VoteService vs = VoteService.getInstance();

        // KPI row
        HBox kpiRow = new HBox(14);
        kpiRow.getChildren().addAll(
            makeKpiCard("👤", "Total Voters",     String.valueOf(vs.getVoterCount()),       "Registered", true,  "rgba(108,99,255,0.18)"),
            makeKpiCard("🗳️","Votes Cast",         String.valueOf(vs.getTotalVotesCast()),   "Live count", true,  "rgba(0,212,255,0.15)"),
            makeKpiCard("📈","Turnout",            String.format("%.1f%%", vs.getTurnoutPercent()), "Of voters", true, "rgba(0,229,160,0.15)"),
            makeKpiCard("🏛️","Status",             vs.isElectionOpen() ? "OPEN" : "CLOSED", "Election",   vs.isElectionOpen(), "rgba(255,84,112,0.15)")
        );

        // Charts row
        HBox chartsRow = new HBox(18);

        // Bar chart
        VBox barCard = makeGlassCard();
        HBox.setHgrow(barCard, Priority.ALWAYS);
        Label barTitle = new Label("Votes by Candidate");
        barTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");
        CategoryAxis xA = new CategoryAxis();
        NumberAxis yA = new NumberAxis();
        BarChart<String, Number> bar = new BarChart<>(xA, yA);
        bar.setLegendVisible(false); bar.setAnimated(false); bar.setMinHeight(200);
        bar.setStyle("-fx-background-color: transparent;");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Candidate c : vs.getCandidates())
            series.getData().add(new XYChart.Data<>(c.getName(), c.getVoteCount()));
        bar.getData().add(series);
        bar.lookup(".chart-plot-background") /* will apply via css */;
        barCard.getChildren().addAll(barTitle, bar);

        // Pie chart
        VBox pieCard = makeGlassCard();
        pieCard.setPrefWidth(300);
        Label pieTitle = new Label("Vote Distribution");
        pieTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Candidate c : vs.getCandidates())
            pieData.add(new PieChart.Data(c.getName(), Math.max(c.getVoteCount(), 1)));
        PieChart pie = new PieChart(pieData);
        pie.setLegendVisible(true); pie.setLabelsVisible(false);
        pie.setLegendSide(javafx.geometry.Side.BOTTOM); pie.setMinHeight(180);
        pieCard.getChildren().addAll(pieTitle, pie);

        chartsRow.getChildren().addAll(barCard, pieCard);

        // Activity feed
        VBox actCard = makeGlassCard();
        Label actTitle = new Label("Recent Activity");
        actTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");
        actCard.getChildren().add(actTitle);
        String[][] acts = {
            {"🗳️  Vote cast — Niyati Sabalpara (V101)", "2 min ago"},
            {"👤  New voter registered — V103",          "5 min ago"},
            {"📊  Live results refreshed",               "10 min ago"},
            {"🛡️  Admin session started",                "15 min ago"},
            {"🏛️  Election opened by Administrator",     "30 min ago"}
        };
        for (String[] a : acts) {
            HBox item = new HBox(12); item.setAlignment(Pos.CENTER_LEFT);
            item.setStyle("-fx-padding: 9 12 9 12; -fx-background-color: rgba(255,255,255,0.02); "
                    + "-fx-background-radius: 8;");
            Label txt = new Label(a[0]);
            txt.setStyle("-fx-font-size: 13px; -fx-text-fill: #8892b0;");
            Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS);
            Label t = new Label(a[1]);
            t.setStyle("-fx-font-size: 11px; -fx-text-fill: #3d4466;");
            item.getChildren().addAll(txt, r, t);
            actCard.getChildren().add(item);
        }

        panel.getChildren().addAll(pageHeader, kpiRow, chartsRow, actCard);
        return panel;
    }

    // ================================================================
    // PANEL 2 — ELECTION CONTROL
    // ================================================================
    private VBox buildElectionControlPanel() {
        VBox panel = new VBox(22);
        panel.setPadding(new Insets(32, 36, 32, 36));
        panel.setStyle("-fx-background-color: #0d0f1a;");

        Label title = new Label("Election Control");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #e8eaf6;");
        Label sub = new Label("Manage the state and configuration of the active election.");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #8892b0;");

        VoteService vs = VoteService.getInstance();
        boolean isOpen = vs.isElectionOpen();

        // Status card
        VBox statusCard = makeGlassCard();
        statusCard.setMaxWidth(580);

        Label statusLbl = new Label("Current Election Status");
        statusLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");

        HBox statusRow = new HBox(12); statusRow.setAlignment(Pos.CENTER_LEFT);
        Label statusBadge = new Label(isOpen ? "● OPEN" : "● CLOSED");
        statusBadge.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-background-radius: 20; -fx-padding: 4 12 4 12; "
                + (isOpen ? "-fx-background-color: rgba(0,229,160,0.15); -fx-text-fill: #00e5a0;"
                          : "-fx-background-color: rgba(255,84,112,0.15); -fx-text-fill: #ff5470;"));
        Label statusDesc = new Label(isOpen
                ? "Voters can currently cast their votes."
                : "Voting is paused. No votes can be submitted.");
        statusDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #c9cedf;");
        statusRow.getChildren().addAll(statusBadge, statusDesc);

        Button toggleBtn = new Button(isOpen ? "🔒   Close Election" : "🔓   Open Election");
        toggleBtn.setStyle("-fx-background-color: " + (isOpen ? "#ff5470" : "#00e5a0") + "; "
                + "-fx-text-fill: " + (isOpen ? "white" : "#0d0f1a") + "; "
                + "-fx-font-size: 13px; -fx-font-weight: 800; -fx-background-radius: 10; "
                + "-fx-padding: 11 22 11 22; -fx-cursor: hand; -fx-border-color: transparent;");
        toggleBtn.setOnAction(e -> {
            vs.setElectionOpen(!vs.isElectionOpen());
            setActiveNav(1);
            showPanel(buildElectionControlPanel());
        });

        // Divider
        Region div = new Region();
        div.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-min-height: 1; -fx-max-height: 1;");

        Label elecName = new Label("General Election 2026");
        elecName.setStyle("-fx-font-size: 19px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");
        Label elecDesc = new Label("National election for choosing the next governing body.\nAll registered voters are eligible to participate.");
        elecDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #8892b0; -fx-line-spacing: 4;");

        HBox statsRow = new HBox(30); statsRow.setAlignment(Pos.CENTER_LEFT);
        statsRow.getChildren().addAll(
            makeStatChip("Candidates",     String.valueOf(vs.getCandidates().size())),
            makeStatChip("Registered Voters", String.valueOf(vs.getVoterCount())),
            makeStatChip("Votes Cast",     String.valueOf(vs.getTotalVotesCast()))
        );

        statusCard.getChildren().addAll(statusLbl, statusRow, toggleBtn, div, elecName, elecDesc, statsRow);
        panel.getChildren().addAll(title, sub, statusCard);
        return panel;
    }

    // ================================================================
    // PANEL 3 — VOTER MANAGER
    // ================================================================
    private VBox buildVoterManagerPanel() {
        VBox panel = new VBox(22);
        panel.setPadding(new Insets(32, 36, 32, 36));
        panel.setStyle("-fx-background-color: #0d0f1a;");

        Label title = new Label("Voter Manager");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #e8eaf6;");
        Label sub = new Label("View and manage all registered voters.");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #8892b0;");

        VBox tableCard = makeGlassCard();

        // Search bar
        HBox searchRow = new HBox(12); searchRow.setAlignment(Pos.CENTER_LEFT);
        TextField searchField = new TextField();
        searchField.setPromptText("🔍   Search by name, ID or email...");
        searchField.setStyle("-fx-pref-height: 38px; -fx-background-color: rgba(255,255,255,0.05); "
                + "-fx-background-radius: 10; -fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 10; "
                + "-fx-border-width: 1; -fx-padding: 0 12 0 12; -fx-font-size: 13px; -fx-text-fill: #e8eaf6; "
                + "-fx-prompt-text-fill: #4a5568;");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Label totalLbl = new Label("Total: " + VoteService.getInstance().getVoterCount() + " voters");
        totalLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892b0; -fx-font-weight: 600;");
        searchRow.getChildren().addAll(searchField, totalLbl);

        // Table
        TableView<User> table = new TableView<>();
        table.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        table.setPrefHeight(320);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<User, String> colId   = new TableColumn<>("Voter ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("voterId"));
        TableColumn<User, String> colName = new TableColumn<>("Full Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<User, String> colEmail= new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<User, String> colMob  = new TableColumn<>("Mobile");
        colMob.setCellValueFactory(new PropertyValueFactory<>("mobile"));

        TableColumn<User, Void> colStatus = new TableColumn<>("Status");
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    User u = (User) getTableRow().getItem();
                    Label badge = new Label(u.hasVoted() ? "✓  Voted" : "Not Voted");
                    badge.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-background-radius: 20; "
                            + "-fx-padding: 3 10 3 10; " + (u.hasVoted()
                            ? "-fx-background-color: rgba(0,229,160,0.15); -fx-text-fill: #00e5a0;"
                            : "-fx-background-color: rgba(136,146,176,0.12); -fx-text-fill: #8892b0;"));
                    setGraphic(badge);
                }
            }
        });

        table.getColumns().addAll(colId, colName, colEmail, colMob, colStatus);
        ObservableList<User> data = FXCollections.observableArrayList(VoteService.getInstance().getVoters());
        table.setItems(data);

        searchField.textProperty().addListener((obs, old, nv) -> {
            if (nv == null || nv.isBlank()) { table.setItems(data); return; }
            ObservableList<User> filtered = FXCollections.observableArrayList();
            String q = nv.toLowerCase();
            for (User u : data)
                if (u.getName().toLowerCase().contains(q) || u.getVoterId().toLowerCase().contains(q)
                        || u.getEmail().toLowerCase().contains(q)) filtered.add(u);
            table.setItems(filtered);
        });

        tableCard.getChildren().addAll(searchRow, table);
        panel.getChildren().addAll(title, sub, tableCard);
        return panel;
    }

    // ================================================================
    // PANEL 4 — CANDIDATES
    // ================================================================
    private VBox buildCandidatesPanel() {
        VBox panel = new VBox(22);
        panel.setPadding(new Insets(32, 36, 32, 36));
        panel.setStyle("-fx-background-color: #0d0f1a;");

        Label title = new Label("Candidates");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #e8eaf6;");
        Label sub = new Label("All registered candidates with live vote standings.");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #8892b0;");

        int total = VoteService.getInstance().getTotalVotesCast();

        HBox grid = new HBox(18);
        grid.setAlignment(Pos.TOP_LEFT);
        String[] colors = {"#6c63ff", "#00d4ff", "#00e5a0"};
        String[] icons  = {"🟣", "🔵", "🟢"};
        String[] bgs    = {"rgba(108,99,255,0.15)", "rgba(0,212,255,0.12)", "rgba(0,229,160,0.12)"};
        String[] pbCls  = {"vote-progress-bar", "vote-progress-bar-cyan", "vote-progress-bar-teal"};

        int ci = 0;
        for (Candidate c : VoteService.getInstance().getCandidates()) {
            VBox card = makeGlassCard();
            card.setPrefWidth(240); card.setAlignment(Pos.TOP_LEFT);

            StackPane av = new StackPane();
            av.setStyle("-fx-background-color: " + bgs[ci % 3] + "; -fx-background-radius: 12; "
                    + "-fx-min-width: 48; -fx-min-height: 48; -fx-max-width: 48; -fx-max-height: 48;");
            Label avIco = new Label(icons[ci % 3]); avIco.setStyle("-fx-font-size: 22px;");
            av.getChildren().add(avIco);

            Label cName = new Label(c.getName());
            cName.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");
            Label cParty = new Label(c.getParty());
            cParty.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892b0;");

            Region divLine = new Region();
            divLine.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-min-height: 1; -fx-max-height: 1;");

            double pct = total > 0 ? c.getVoteCount() * 100.0 / total : 0;
            HBox vRow = new HBox(8); vRow.setAlignment(Pos.CENTER_LEFT);
            Label vCount = new Label(c.getVoteCount() + " votes");
            vCount.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: " + colors[ci % 3] + ";");
            Label pctLbl = new Label(String.format("(%.1f%%)", pct));
            pctLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892b0;");
            vRow.getChildren().addAll(vCount, pctLbl);

            ProgressBar pb = new ProgressBar(total > 0 ? (double) c.getVoteCount() / total : 0);
            pb.getStyleClass().add(pbCls[ci % 3]);
            pb.setMaxWidth(Double.MAX_VALUE); pb.setPrefHeight(8);

            card.getChildren().addAll(av, cName, cParty, divLine, vRow, pb);
            grid.getChildren().add(card);
            ci++;
        }

        panel.getChildren().addAll(title, sub, grid);
        return panel;
    }

    // ================================================================
    // PANEL 5 — LIVE RESULTS
    // ================================================================
    private VBox buildLiveResultsPanel() {
        VBox panel = new VBox(22);
        panel.setPadding(new Insets(32, 36, 32, 36));
        panel.setStyle("-fx-background-color: #0d0f1a;");

        Label title = new Label("Live Results");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #e8eaf6;");

        VoteService vs = VoteService.getInstance();
        Candidate winner = vs.getWinner();

        if (winner != null && winner.getVoteCount() > 0) {
            HBox winCard = new HBox(16); winCard.setAlignment(Pos.CENTER_LEFT);
            winCard.setStyle("-fx-background-color: rgba(255,193,7,0.07); -fx-background-radius: 16; "
                    + "-fx-border-color: rgba(255,193,7,0.25); -fx-border-width: 1; -fx-border-radius: 16; "
                    + "-fx-padding: 20 24 20 24; -fx-effect: dropshadow(gaussian, rgba(255,193,7,0.15), 20, 0, 0, 0);");
            Label trophy = new Label("🏆"); trophy.setStyle("-fx-font-size: 34px;");
            VBox wi = new VBox(4);
            Label wl = new Label("LEADING CANDIDATE"); wl.setStyle("-fx-font-size: 10px; -fx-font-weight: 800; -fx-text-fill: #ffc107;");
            Label wn = new Label(winner.getName()); wn.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #ffc107;");
            Label ws = new Label(winner.getVoteCount() + " votes · " + winner.getParty());
            ws.setStyle("-fx-font-size: 13px; -fx-text-fill: #8892b0;");
            wi.getChildren().addAll(wl, wn, ws);
            winCard.getChildren().addAll(trophy, wi);
            panel.getChildren().addAll(title, winCard);
        } else {
            panel.getChildren().add(title);
        }

        HBox chartsRow = new HBox(18);

        // Pie
        VBox pieCard = makeGlassCard(); HBox.setHgrow(pieCard, Priority.ALWAYS);
        Label pieTitle = new Label("Vote Distribution"); pieTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");
        ObservableList<PieChart.Data> pd = FXCollections.observableArrayList();
        for (Candidate c : vs.getCandidates())
            pd.add(new PieChart.Data(c.getName() + " (" + c.getVoteCount() + ")", Math.max(c.getVoteCount(), 1)));
        PieChart pie = new PieChart(pd);
        pie.setLegendVisible(true); pie.setLabelsVisible(true); pie.setMinHeight(260);
        pieCard.getChildren().addAll(pieTitle, pie);

        // Leaderboard
        VBox lbCard = makeGlassCard(); lbCard.setPrefWidth(300);
        Label lbTitle = new Label("Leaderboard"); lbTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");
        lbCard.getChildren().add(lbTitle);

        int rank = 1; int tot = vs.getTotalVotesCast();
        String[] rc = {"#ffc107", "#6c63ff", "#00d4ff"};
        String[] pbc = {"vote-progress-bar", "vote-progress-bar-cyan", "vote-progress-bar-teal"};
        for (Candidate c : vs.getCandidates()) {
            VBox rBox = new VBox(5); rBox.setPadding(new Insets(8, 0, 8, 0));
            HBox topRow = new HBox(10); topRow.setAlignment(Pos.CENTER_LEFT);
            Label rnk = new Label("#" + rank); rnk.setStyle("-fx-font-size: 17px; -fx-font-weight: 900; -fx-text-fill: " + rc[Math.min(rank-1,2)] + "; -fx-min-width: 32;");
            VBox inf = new VBox(2); HBox.setHgrow(inf, Priority.ALWAYS);
            Label cn2 = new Label(c.getName()); cn2.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #e8eaf6;");
            Label cp2 = new Label(c.getParty()); cp2.setStyle("-fx-font-size: 11px; -fx-text-fill: #8892b0;");
            inf.getChildren().addAll(cn2, cp2);
            Label vt = new Label(String.valueOf(c.getVoteCount())); vt.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + rc[Math.min(rank-1,2)] + ";");
            topRow.getChildren().addAll(rnk, inf, vt);
            ProgressBar pb = new ProgressBar(tot > 0 ? (double) c.getVoteCount() / tot : 0);
            pb.setMaxWidth(Double.MAX_VALUE); pb.setPrefHeight(7);
            pb.getStyleClass().add(pbc[Math.min(rank-1, 2)]);
            rBox.getChildren().addAll(topRow, pb);
            lbCard.getChildren().add(rBox);
            rank++;
        }

        chartsRow.getChildren().addAll(pieCard, lbCard);
        panel.getChildren().add(chartsRow);
        return panel;
    }

    // ================================================================
    // PANEL 6 — SETTINGS
    // ================================================================
    private VBox buildSettingsPanel() {
        VBox panel = new VBox(22);
        panel.setPadding(new Insets(32, 36, 32, 36));
        panel.setStyle("-fx-background-color: #0d0f1a;");

        Label title = new Label("Settings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #e8eaf6;");

        VBox profileCard = makeGlassCard(); profileCard.setMaxWidth(500);
        Label ctitle = new Label("Admin Profile"); ctitle.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");

        StackPane ava = new StackPane();
        ava.setStyle("-fx-background-color: rgba(108,99,255,0.2); -fx-background-radius: 50; "
                + "-fx-min-width: 72; -fx-min-height: 72; -fx-max-width: 72; -fx-max-height: 72;");
        Label avaIco = new Label("🛡️"); avaIco.setStyle("-fx-font-size: 32px;");
        ava.getChildren().add(avaIco);

        VBox fields = new VBox(12);
        fields.getChildren().addAll(
            makeProfileRow("Username",      "admin"),
            makeProfileRow("Role",          "System Administrator"),
            makeProfileRow("Access Level",  "Full Access — All Portals"),
            makeProfileRow("Session",       "Active — Secure Session")
        );

        Region div = new Region(); div.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-min-height: 1; -fx-max-height: 1;");
        Label secTitle = new Label("Security"); secTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");
        Label secNote = new Label("All sessions are encrypted and time-limited.\nContact system admin for credential changes.");
        secNote.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892b0; -fx-line-spacing: 5;");

        profileCard.getChildren().addAll(ctitle, ava, fields, div, secTitle, secNote);
        panel.getChildren().addAll(title, profileCard);
        return panel;
    }

    // ================================================================
    // HELPERS
    // ================================================================
    private VBox makeGlassCard() {
        VBox card = new VBox(14);
        card.setStyle("-fx-background-color: #13172b; -fx-background-radius: 16; "
                + "-fx-border-color: rgba(255,255,255,0.06); -fx-border-radius: 16; "
                + "-fx-border-width: 1; -fx-padding: 24 24 24 24; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 18, 0, 0, 6);");
        return card;
    }

    private VBox makeKpiCard(String icon, String lbl, String val, String trend, boolean pos, String bg) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #13172b; -fx-background-radius: 16; "
                + "-fx-border-color: rgba(255,255,255,0.06); -fx-border-radius: 16; "
                + "-fx-border-width: 1; -fx-padding: 20 20 20 20;");
        HBox.setHgrow(card, Priority.ALWAYS);

        StackPane iBox = new StackPane();
        iBox.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 11; "
                + "-fx-min-width: 46; -fx-min-height: 46; -fx-max-width: 46; -fx-max-height: 46;");
        Label iIco = new Label(icon); iIco.setStyle("-fx-font-size: 20px;");
        iBox.getChildren().add(iIco);

        Label l = new Label(lbl); l.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892b0; -fx-font-weight: 700;");
        Label v = new Label(val); v.setStyle("-fx-font-size: 28px; -fx-font-weight: 900; -fx-text-fill: #e8eaf6;");
        String trendColor = pos ? "#00e5a0" : "#ff5470";
        Label tr = new Label((pos ? "↑ " : "↓ ") + trend);
        tr.setStyle("-fx-font-size: 11px; -fx-text-fill: " + trendColor + "; -fx-font-weight: 700;");
        card.getChildren().addAll(iBox, l, v, tr);
        return card;
    }

    private VBox makeStatChip(String label, String value) {
        VBox b = new VBox(3);
        Label l = new Label(label); l.setStyle("-fx-font-size: 11px; -fx-text-fill: #8892b0; -fx-font-weight: 700;");
        Label v = new Label(value); v.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #e8eaf6;");
        b.getChildren().addAll(l, v);
        return b;
    }

    private HBox makeProfileRow(String label, String value) {
        HBox row = new HBox(18); row.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: #3d4466; -fx-font-weight: 700; -fx-min-width: 140;");
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 13px; -fx-text-fill: #c9cedf; -fx-font-weight: 600;");
        row.getChildren().addAll(l, v);
        return row;
    }

    public BorderPane getView() {
        return view;
    }
}