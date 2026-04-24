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
    private Button[] navButtons;
    private ScrollPane contentScroll;

    public AdminDashboard(Stage stage) {
        this.stage = stage;
        view.setStyle("-fx-background-color: " + ThemeManager.bgBase() + ";");
        buildSidebar();
        showPanel(buildDashboardPanel());
    }

    // ── SIDEBAR ──────────────────────────────────────────────────────
    private void buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setStyle("-fx-background-color: " + ThemeManager.sidebar() + "; "
                + "-fx-border-color: " + ThemeManager.sidebarBorder() + "; -fx-border-width: 0 1 0 0;");
        sidebar.setPrefWidth(220); sidebar.setMinWidth(220);

        HBox brandBox = new HBox(0);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(24, 20, 20, 20));
        Label bl = new Label("Pollaroid");
        bl.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label bd = new Label(".");
        bd.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.accent() + ";");
        brandBox.getChildren().addAll(bl, bd);

        Label menuHdr = new Label("MAIN MENU");
        menuHdr.setStyle("-fx-font-size: 10px; -fx-font-weight: 700; -fx-text-fill: " + ThemeManager.textMuted()
                + "; -fx-padding: 8 20 4 20;");

        String[] icons  = {"🏠", "🗳️", "👥", "🧑", "📊", "⚙️"};
        String[] labels = {"Dashboard", "Election Control", "Voter Manager",
                           "Candidates", "Live Results", "Settings"};

        navButtons = new Button[icons.length];
        VBox navList = new VBox(2);
        navList.setPadding(new Insets(0, 10, 0, 10));
        navList.getChildren().add(menuHdr);

        for (int i = 0; i < icons.length; i++) {
            final int idx = i;
            Button btn = new Button(icons[i] + "   " + labels[i]);
            btn.setStyle(ThemeManager.navNormal()); btn.setMaxWidth(Double.MAX_VALUE);
            navButtons[i] = btn;
            btn.setOnMouseEntered(e -> { if (!btn.getStyle().contains(ThemeManager.accent())) btn.setStyle(ThemeManager.navHover()); });
            btn.setOnMouseExited(e  -> { if (!btn.getStyle().contains(ThemeManager.accent())) btn.setStyle(ThemeManager.navNormal()); });
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

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);

        // Theme toggle
        VBox themeSection = new VBox(4);
        themeSection.setPadding(new Insets(0, 10, 4, 10));
        Button themeBtn = new Button(ThemeManager.toggleLabel());
        themeBtn.setStyle(ThemeManager.navNormal()); themeBtn.setMaxWidth(Double.MAX_VALUE);
        themeBtn.setOnAction(e -> ThemeManager.applyToggle(view.getScene(),
                () -> new AdminDashboard(stage).getView()));
        themeSection.getChildren().add(themeBtn);

        // Logout
        VBox logoutSection = new VBox(4);
        logoutSection.setPadding(new Insets(0, 10, 14, 10));
        Region div = new Region();
        div.setStyle("-fx-background-color: " + ThemeManager.divider() + "; -fx-min-height: 1; -fx-max-height: 1; -fx-margin: 4 0;");
        Button logoutBtn = new Button("🚪   Logout");
        logoutBtn.setStyle(ThemeManager.navNormal() + "-fx-text-fill: " + ThemeManager.danger() + ";");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> { VoteService.getInstance().logout();
            view.getScene().setRoot(new LandingView(stage).getView()); });
        logoutSection.getChildren().addAll(div, logoutBtn);

        // User card
        HBox userCard = new HBox(10);
        userCard.setAlignment(Pos.CENTER_LEFT);
        userCard.setPadding(new Insets(14, 16, 16, 16));
        userCard.setStyle("-fx-background-color: " + ThemeManager.activityBg() + ";");
        StackPane ava = new StackPane();
        ava.setStyle("-fx-background-color: " + ThemeManager.adminAccentBg() + "; -fx-background-radius: 50; "
                + "-fx-min-width: 34; -fx-min-height: 34; -fx-max-width: 34; -fx-max-height: 34;");
        Label avaIco = new Label("🛡️"); avaIco.setStyle("-fx-font-size: 14px;");
        ava.getChildren().add(avaIco);
        VBox um = new VBox(2);
        Label uName = new Label("Administrator");
        uName.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label uRole = new Label("System Admin");
        uRole.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ThemeManager.textMuted() + ";");
        um.getChildren().addAll(uName, uRole);
        userCard.getChildren().addAll(ava, um);

        sidebar.getChildren().addAll(brandBox, navList, spacer, themeSection, logoutSection, userCard);

        contentScroll = new ScrollPane();
        contentScroll.setFitToWidth(true);
        contentScroll.setStyle("-fx-background-color: transparent; -fx-background: "
                + ThemeManager.bgBase() + "; -fx-border-color: transparent;");
        view.setLeft(sidebar);
        view.setCenter(contentScroll);
    }

    private void setActiveNav(int idx) {
        if (navButtons == null) return;
        for (int i = 0; i < navButtons.length; i++)
            navButtons[i].setStyle(i == idx
                    ? ThemeManager.navActive(ThemeManager.accent())
                    : ThemeManager.navNormal());
    }

    private void showPanel(VBox panel) {
        contentScroll.setContent(panel);
        panel.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(260), panel);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    // ── PANEL 1: DASHBOARD ────────────────────────────────────────────
    private VBox buildDashboardPanel() {
        VBox panel = panelBase();
        VBox hdr = new VBox(4);
        hdr(hdr, "Dashboard Overview", "Welcome back, Administrator. Here's your election snapshot.");

        VoteService vs = VoteService.getInstance();
        HBox kpiRow = new HBox(14);
        kpiRow.getChildren().addAll(
            kpi("👤","Total Voters",  String.valueOf(vs.getVoterCount()),       "Registered", true,  ThemeManager.adminAccentBg()),
            kpi("🗳️","Votes Cast",    String.valueOf(vs.getTotalVotesCast()),   "Live count", true,  ThemeManager.candidateAccentBg()),
            kpi("📈","Turnout",       String.format("%.1f%%",vs.getTurnoutPercent()), "Of voters", true, ThemeManager.voterAccentBg()),
            kpi("🏛️","Status",        vs.isElectionOpen()?"OPEN":"CLOSED",     "Election",  vs.isElectionOpen(), "rgba(255,84,112,0.14)")
        );

        HBox charts = new HBox(16);
        VBox barCard = glass(); HBox.setHgrow(barCard, Priority.ALWAYS);
        barCard.getChildren().add(cardTitle("Votes by Candidate"));
        CategoryAxis xA = new CategoryAxis(); NumberAxis yA = new NumberAxis();
        BarChart<String,Number> bar = new BarChart<>(xA, yA);
        bar.setLegendVisible(false); bar.setAnimated(false); bar.setMinHeight(200);
        bar.setStyle("-fx-background-color: transparent;");
        XYChart.Series<String,Number> s = new XYChart.Series<>();
        vs.getCandidates().forEach(c -> s.getData().add(new XYChart.Data<>(c.getName(), c.getVoteCount())));
        bar.getData().add(s);
        barCard.getChildren().add(bar);

        VBox pieCard = glass(); pieCard.setPrefWidth(290);
        pieCard.getChildren().add(cardTitle("Vote Distribution"));
        ObservableList<PieChart.Data> pd = FXCollections.observableArrayList();
        vs.getCandidates().forEach(c -> pd.add(new PieChart.Data(c.getName(), Math.max(c.getVoteCount(),1))));
        PieChart pie = new PieChart(pd); pie.setLegendVisible(true); pie.setLabelsVisible(false);
        pie.setLegendSide(javafx.geometry.Side.BOTTOM); pie.setMinHeight(175);
        pieCard.getChildren().add(pie);
        charts.getChildren().addAll(barCard, pieCard);

        VBox actCard = glass();
        actCard.getChildren().add(cardTitle("Recent Activity"));
        String[][] acts = {
            {"🗳️  Vote cast — Niyati Sabalpara (V101)", "2 min ago"},
            {"👤  New voter registered — V103", "5 min ago"},
            {"📊  Live results refreshed", "10 min ago"},
            {"🛡️  Admin session started", "15 min ago"},
            {"🏛️  Election opened by Administrator", "30 min ago"}
        };
        for (String[] a : acts) {
            HBox item = new HBox(12); item.setAlignment(Pos.CENTER_LEFT);
            item.setStyle("-fx-padding: 9 12 9 12; -fx-background-color: " + ThemeManager.activityBg()
                    + "; -fx-background-radius: 8;");
            Label txt = new Label(a[0]);
            txt.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");
            Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS);
            Label t = new Label(a[1]);
            t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ThemeManager.textMuted() + ";");
            item.getChildren().addAll(txt, r, t);
            actCard.getChildren().add(item);
        }
        panel.getChildren().addAll(hdr, kpiRow, charts, actCard);
        return panel;
    }

    // ── PANEL 2: ELECTION CONTROL ────────────────────────────────────
    private VBox buildElectionControlPanel() {
        VBox panel = panelBase();
        VBox hdr = new VBox(4);
        hdr(hdr, "Election Control", "Manage the state and configuration of the active election.");

        VoteService vs = VoteService.getInstance();
        boolean open = vs.isElectionOpen();

        VBox card = glass(); card.setMaxWidth(560);
        card.getChildren().add(cardTitle("Current Election Status"));

        HBox sRow = new HBox(12); sRow.setAlignment(Pos.CENTER_LEFT);
        Label badge = new Label(open ? "● OPEN" : "● CLOSED");
        badge.setStyle("-fx-font-size: 11px; -fx-font-weight: 800; -fx-background-radius: 20; -fx-padding: 4 12 4 12; "
                + (open ? "-fx-background-color: rgba(0,229,160,0.15); -fx-text-fill: #00e5a0;"
                        : "-fx-background-color: rgba(255,84,112,0.15); -fx-text-fill: #ff5470;"));
        Label sdesc = new Label(open ? "Voters can currently cast their votes."
                : "Voting is paused. No votes can be submitted.");
        sdesc.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");
        sRow.getChildren().addAll(badge, sdesc);

        Button toggle = new Button(open ? "🔒   Close Election" : "🔓   Open Election");
        toggle.setStyle("-fx-background-color: " + (open ? "#ff5470" : "#00c98a") + "; "
                + "-fx-text-fill: " + (open ? "white" : "#0d0f1a") + "; "
                + "-fx-font-size: 13px; -fx-font-weight: 800; -fx-background-radius: 10; "
                + "-fx-padding: 11 22 11 22; -fx-cursor: hand; -fx-border-color: transparent;");
        toggle.setOnAction(e -> { vs.setElectionOpen(!vs.isElectionOpen()); setActiveNav(1); showPanel(buildElectionControlPanel()); });

        Region div = divider();
        Label en = new Label("General Election 2026");
        en.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label ed = new Label("National election for choosing the next governing body.\nAll registered voters are eligible.");
        ed.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + "; -fx-line-spacing: 4;");

        HBox statsRow = new HBox(28); statsRow.setAlignment(Pos.CENTER_LEFT);
        statsRow.getChildren().addAll(
            statChip("Candidates", String.valueOf(vs.getCandidates().size())),
            statChip("Voters", String.valueOf(vs.getVoterCount())),
            statChip("Votes Cast", String.valueOf(vs.getTotalVotesCast()))
        );
        card.getChildren().addAll(sRow, toggle, div, en, ed, statsRow);
        panel.getChildren().addAll(hdr, card);
        return panel;
    }

    // ── PANEL 3: VOTER MANAGER ───────────────────────────────────────
    private VBox buildVoterManagerPanel() {
        VBox panel = panelBase();
        VBox hdr = new VBox(4);
        hdr(hdr, "Voter Manager", "View and manage all registered voters.");

        VBox card = glass();
        HBox sRow = new HBox(12); sRow.setAlignment(Pos.CENTER_LEFT);
        TextField search = new TextField();
        search.setPromptText("🔍   Search by name, ID or email...");
        search.setStyle("-fx-pref-height: 36px; -fx-background-color: " + ThemeManager.inputBg()
                + "; -fx-background-radius: 10; -fx-border-color: " + ThemeManager.inputBorder()
                + "; -fx-border-radius: 10; -fx-border-width: 1; -fx-padding: 0 11 0 11; "
                + "-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textPrimary()
                + "; -fx-prompt-text-fill: " + ThemeManager.textMuted() + ";");
        HBox.setHgrow(search, Priority.ALWAYS);
        Label tot = new Label("Total: " + VoteService.getInstance().getVoterCount() + " voters");
        tot.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ThemeManager.textSecondary() + "; -fx-font-weight: 600;");
        sRow.getChildren().addAll(search, tot);

        TableView<User> table = new TableView<>();
        table.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        table.setPrefHeight(310);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<User,String> cId   = col("Voter ID",   "voterId");
        TableColumn<User,String> cName = col("Name",       "name");
        TableColumn<User,String> cEmail= col("Email",      "email");
        TableColumn<User,String> cMob  = col("Mobile",     "mobile");

        TableColumn<User,Void> cStatus = new TableColumn<>("Status");
        cStatus.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty); setGraphic(null);
                if (!empty && getTableRow() != null && getTableRow().getItem() != null) {
                    User u = (User) getTableRow().getItem();
                    Label b = new Label(u.hasVoted() ? "✓  Voted" : "Not Voted");
                    b.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-background-radius: 20; -fx-padding: 3 10 3 10; "
                        + (u.hasVoted()
                            ? "-fx-background-color: rgba(0,229,160,0.15); -fx-text-fill: #00e5a0;"
                            : "-fx-background-color: rgba(136,146,176,0.12); -fx-text-fill: " + ThemeManager.textSecondary() + ";"));
                    setGraphic(b);
                }
            }
        });
        table.getColumns().addAll(cId, cName, cEmail, cMob, cStatus);
        ObservableList<User> data = FXCollections.observableArrayList(VoteService.getInstance().getVoters());
        table.setItems(data);

        search.textProperty().addListener((obs, o, nv) -> {
            if (nv == null || nv.isBlank()) { table.setItems(data); return; }
            String q = nv.toLowerCase();
            ObservableList<User> f = FXCollections.observableArrayList();
            data.stream().filter(u -> u.getName().toLowerCase().contains(q)
                    || u.getVoterId().toLowerCase().contains(q)
                    || u.getEmail().toLowerCase().contains(q)).forEach(f::add);
            table.setItems(f);
        });

        card.getChildren().addAll(sRow, table);
        panel.getChildren().addAll(hdr, card);
        return panel;
    }

    // ── PANEL 4: CANDIDATES ──────────────────────────────────────────
    private VBox buildCandidatesPanel() {
        VBox panel = panelBase();
        VBox hdr = new VBox(4);
        hdr(hdr, "Candidates", "All registered candidates with live vote standings.");

        int total = VoteService.getInstance().getTotalVotesCast();
        String[] clrs = {ThemeManager.accent(), ThemeManager.accentCyan(), ThemeManager.accentTeal()};
        String[] icons = {"🟣","🔵","🟢"};
        String[] bgs   = {ThemeManager.adminAccentBg(), ThemeManager.candidateAccentBg(), ThemeManager.voterAccentBg()};
        String[] pbcls = {"vote-progress-bar","vote-progress-bar-cyan","vote-progress-bar-teal"};

        HBox grid = new HBox(16); grid.setAlignment(Pos.TOP_LEFT);
        int ci = 0;
        for (Candidate c : VoteService.getInstance().getCandidates()) {
            VBox card = glass(); card.setPrefWidth(238); card.setAlignment(Pos.TOP_LEFT);
            StackPane av = new StackPane();
            av.setStyle("-fx-background-color: " + bgs[ci%3] + "; -fx-background-radius: 12; "
                    + "-fx-min-width: 46; -fx-min-height: 46; -fx-max-width: 46; -fx-max-height: 46;");
            Label avIco = new Label(icons[ci%3]); avIco.setStyle("-fx-font-size: 20px;");
            av.getChildren().add(avIco);
            Label cn = new Label(c.getName());
            cn.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
            Label cp = new Label(c.getParty());
            cp.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");
            Region dv = divider();
            double pct = total > 0 ? c.getVoteCount()*100.0/total : 0;
            HBox vRow = new HBox(7); vRow.setAlignment(Pos.CENTER_LEFT);
            Label vc = new Label(c.getVoteCount()+" votes");
            vc.setStyle("-fx-font-size: 19px; -fx-font-weight: 900; -fx-text-fill: "+clrs[ci%3]+";");
            Label pl = new Label(String.format("(%.1f%%)", pct));
            pl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ThemeManager.textMuted() + ";");
            vRow.getChildren().addAll(vc, pl);
            ProgressBar pb = new ProgressBar(total>0?(double)c.getVoteCount()/total:0);
            pb.getStyleClass().add(pbcls[ci%3]); pb.setMaxWidth(Double.MAX_VALUE); pb.setPrefHeight(8);
            card.getChildren().addAll(av, cn, cp, dv, vRow, pb);
            grid.getChildren().add(card); ci++;
        }
        panel.getChildren().addAll(hdr, grid);
        return panel;
    }

    // ── PANEL 5: LIVE RESULTS ────────────────────────────────────────
    private VBox buildLiveResultsPanel() {
        VBox panel = panelBase();
        Label title = heading("Live Results");

        VoteService vs = VoteService.getInstance();
        Candidate winner = vs.getWinner();
        if (winner != null && winner.getVoteCount() > 0) {
            panel.getChildren().addAll(title, winnerCard(winner));
        } else {
            panel.getChildren().add(title);
        }

        HBox charts = new HBox(16);
        VBox pieCard = glass(); HBox.setHgrow(pieCard, Priority.ALWAYS);
        pieCard.getChildren().add(cardTitle("Vote Distribution"));
        ObservableList<PieChart.Data> pd = FXCollections.observableArrayList();
        vs.getCandidates().forEach(c -> pd.add(new PieChart.Data(c.getName()+" ("+c.getVoteCount()+")", Math.max(c.getVoteCount(),1))));
        PieChart pie = new PieChart(pd); pie.setLegendVisible(true); pie.setLabelsVisible(true);
        pie.setMinHeight(260); pieCard.getChildren().add(pie);

        VBox lb = leaderboard(vs); lb.setPrefWidth(305);
        charts.getChildren().addAll(pieCard, lb);
        panel.getChildren().add(charts);
        return panel;
    }

    // ── PANEL 6: SETTINGS ────────────────────────────────────────────
    private VBox buildSettingsPanel() {
        VBox panel = panelBase();
        Label title = heading("Settings");

        VBox card = glass(); card.setMaxWidth(490);
        card.getChildren().add(cardTitle("Admin Profile"));
        StackPane ava = new StackPane();
        ava.setStyle("-fx-background-color: " + ThemeManager.adminAccentBg() + "; -fx-background-radius: 50; "
                + "-fx-min-width: 68; -fx-min-height: 68; -fx-max-width: 68; -fx-max-height: 68;");
        Label avaIco = new Label("🛡️"); avaIco.setStyle("-fx-font-size: 30px;");
        ava.getChildren().add(avaIco);
        VBox fields = new VBox(11);
        fields.getChildren().addAll(
            pRow("Username","admin"), pRow("Role","System Administrator"),
            pRow("Access","Full Access — All Portals"), pRow("Session","Active — Secure")
        );
        Region dv = divider();
        Label secHdr = cardTitle("Security");
        Label secNote = new Label("All sessions are encrypted and time-limited.");
        secNote.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");

        // Theme section in settings too
        Region dv2 = divider();
        Label thHdr = cardTitle("Appearance");
        Button thBtn = new Button(ThemeManager.toggleLabel());
        thBtn.setStyle("-fx-background-color: " + ThemeManager.inputBg() + "; "
                + "-fx-text-fill: " + ThemeManager.textSecondary() + "; "
                + "-fx-font-size: 13px; -fx-font-weight: 700; -fx-background-radius: 10; "
                + "-fx-border-color: " + ThemeManager.border() + "; -fx-border-radius: 10; "
                + "-fx-border-width: 1; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
        thBtn.setOnAction(e -> ThemeManager.applyToggle(view.getScene(),
                () -> new AdminDashboard(stage).getView()));
        Label thNote = new Label("Switch between dark and light mode. Your preference is applied immediately.");
        thNote.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ThemeManager.textSecondary()
                + "; -fx-line-spacing: 4; -fx-wrap-text: true;");

        card.getChildren().addAll(ava, fields, dv, secHdr, secNote, dv2, thHdr, thNote, thBtn);
        panel.getChildren().addAll(title, card);
        return panel;
    }

    // ── SHARED HELPERS ───────────────────────────────────────────────
    private VBox panelBase() {
        VBox p = new VBox(22);
        p.setPadding(new Insets(30, 34, 30, 34));
        p.setStyle("-fx-background-color: " + ThemeManager.bgBase() + ";");
        return p;
    }

    private void hdr(VBox box, String t, String s) {
        Label tl = new Label(t); tl.setStyle("-fx-font-size: 23px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label sl = new Label(s); sl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");
        box.getChildren().addAll(tl, sl);
    }

    private Label heading(String t) {
        Label l = new Label(t); l.setStyle("-fx-font-size: 23px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        return l;
    }

    private VBox glass() {
        VBox v = new VBox(12); v.setStyle(ThemeManager.glassCard()); return v;
    }

    private Label cardTitle(String t) {
        Label l = new Label(t); l.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        return l;
    }

    private Region divider() {
        Region r = new Region();
        r.setStyle("-fx-background-color: " + ThemeManager.divider() + "; -fx-min-height: 1; -fx-max-height: 1;");
        return r;
    }

    private VBox kpi(String icon, String lbl, String val, String trend, boolean pos, String bg) {
        VBox c = new VBox(9);
        c.setStyle("-fx-background-color: " + ThemeManager.cardBg() + "; -fx-background-radius: 15; "
                + "-fx-border-color: " + ThemeManager.border() + "; -fx-border-radius: 15; "
                + "-fx-border-width: 1; -fx-padding: 18 18 18 18;");
        HBox.setHgrow(c, Priority.ALWAYS);
        StackPane ib = new StackPane();
        ib.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 11; "
                + "-fx-min-width: 44; -fx-min-height: 44; -fx-max-width: 44; -fx-max-height: 44;");
        Label ii = new Label(icon); ii.setStyle("-fx-font-size: 19px;");
        ib.getChildren().add(ii);
        Label l  = new Label(lbl); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ThemeManager.textSecondary() + "; -fx-font-weight: 700;");
        Label v  = new Label(val); v.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label tr = new Label((pos?"↑ ":"↓ ")+trend);
        tr.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (pos ? "#00c98a" : "#ff5470") + "; -fx-font-weight: 700;");
        c.getChildren().addAll(ib, l, v, tr);
        return c;
    }

    private VBox statChip(String lbl, String val) {
        VBox b = new VBox(3);
        Label l = new Label(lbl); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ThemeManager.textMuted() + "; -fx-font-weight: 700;");
        Label v = new Label(val); v.setStyle("-fx-font-size: 21px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        b.getChildren().addAll(l, v); return b;
    }

    private HBox winnerCard(Candidate winner) {
        HBox w = new HBox(16); w.setAlignment(Pos.CENTER_LEFT);
        w.setStyle("-fx-background-color: rgba(255,193,7,0.08); -fx-background-radius: 15; "
                + "-fx-border-color: rgba(255,193,7,0.28); -fx-border-width: 1; -fx-border-radius: 15; "
                + "-fx-padding: 18 22 18 22; -fx-effect: dropshadow(gaussian, rgba(255,193,7,0.15), 18, 0, 0, 0);");
        Label trophy = new Label("🏆"); trophy.setStyle("-fx-font-size: 32px;");
        VBox wi = new VBox(4);
        Label wl = new Label("LEADING CANDIDATE"); wl.setStyle("-fx-font-size: 10px; -fx-font-weight: 800; -fx-text-fill: #ffc107;");
        Label wn = new Label(winner.getName()); wn.setStyle("-fx-font-size: 21px; -fx-font-weight: 900; -fx-text-fill: #ffc107;");
        Label ws = new Label(winner.getVoteCount()+" votes · "+winner.getParty());
        ws.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");
        wi.getChildren().addAll(wl, wn, ws);
        w.getChildren().addAll(trophy, wi);
        return w;
    }

    private VBox leaderboard(VoteService vs) {
        VBox lb = glass(); lb.getChildren().add(cardTitle("Leaderboard"));
        int rank = 1; int tot = vs.getTotalVotesCast();
        String[] rc  = {"#ffc107", ThemeManager.accent(), ThemeManager.accentCyan()};
        String[] pbc = {"vote-progress-bar","vote-progress-bar-cyan","vote-progress-bar-teal"};
        for (Candidate c : vs.getCandidates()) {
            VBox rBox = new VBox(5); rBox.setPadding(new Insets(7,0,7,0));
            HBox top = new HBox(9); top.setAlignment(Pos.CENTER_LEFT);
            Label rk = new Label("#"+rank); rk.setStyle("-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: "+rc[Math.min(rank-1,2)]+"; -fx-min-width: 30;");
            VBox inf = new VBox(2); HBox.setHgrow(inf, Priority.ALWAYS);
            Label cn = new Label(c.getName()); cn.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: "+ThemeManager.textPrimary()+";");
            Label cp = new Label(c.getParty()); cp.setStyle("-fx-font-size: 11px; -fx-text-fill: "+ThemeManager.textSecondary()+";");
            inf.getChildren().addAll(cn, cp);
            Label vt = new Label(String.valueOf(c.getVoteCount())); vt.setStyle("-fx-font-size: 17px; -fx-font-weight: 900; -fx-text-fill: "+rc[Math.min(rank-1,2)]+";");
            top.getChildren().addAll(rk, inf, vt);
            ProgressBar pb = new ProgressBar(tot>0?(double)c.getVoteCount()/tot:0);
            pb.setMaxWidth(Double.MAX_VALUE); pb.setPrefHeight(7);
            pb.getStyleClass().add(pbc[Math.min(rank-1,2)]);
            rBox.getChildren().addAll(top, pb);
            lb.getChildren().add(rBox); rank++;
        }
        return lb;
    }

    private <T> TableColumn<T,String> col(String title, String prop) {
        TableColumn<T,String> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        return c;
    }

    private HBox pRow(String lbl, String val) {
        HBox r = new HBox(18); r.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(lbl); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ThemeManager.textMuted() + "; -fx-font-weight: 700; -fx-min-width: 130;");
        Label v = new Label(val); v.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + "; -fx-font-weight: 600;");
        r.getChildren().addAll(l, v);
        return r;
    }

    public BorderPane getView() { return view; }
}