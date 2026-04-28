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
    private String candidateName = "Candidate";
    private Candidate myCandidate = null;

    public CandidateDashboard(Stage stage) {
        this.stage = stage;
        view.setStyle("-fx-background-color: " + ThemeManager.bgBase() + ";");
        detectCandidate();
        buildSidebar();
        showPanel(buildMyStatsPanel());
    }

    private void detectCandidate() {
        User u = VoteService.getInstance().getCurrentUser();
        if (u != null) {
            candidateName = u.getName();
            VoteService.getInstance().getCandidates().stream()
                .filter(c -> c.getName().equals(candidateName))
                .findFirst().ifPresent(c -> myCandidate = c);
        }
    }

    private void buildSidebar() {
        String accentHex = ThemeManager.accentCyan();

        VBox sidebar = new VBox(0);
        sidebar.setStyle("-fx-background-color: " + ThemeManager.sidebar() + "; "
                + "-fx-border-color: " + ThemeManager.sidebarBorder() + "; -fx-border-width: 0 1 0 0;");
        sidebar.setPrefWidth(220); sidebar.setMinWidth(220);

        HBox brand = new HBox(0); brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(24, 20, 20, 20));
        Label bl = new Label("Votex");
        bl.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label bd = new Label(".");
        bd.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: " + accentHex + ";");
        brand.getChildren().addAll(bl, bd);

        Label menuHdr = new Label("CANDIDATE MENU");
        menuHdr.setStyle("-fx-font-size: 10px; -fx-font-weight: 700; -fx-text-fill: " + ThemeManager.textMuted()
                + "; -fx-padding: 8 20 4 20;");

        String[] icons  = {"📊","🏆","📋"};
        String[] labels = {"My Stats","Live Results","Campaign Info"};
        navBtns = new Button[icons.length];
        VBox navList = new VBox(2);
        navList.setPadding(new Insets(0, 10, 0, 10));
        navList.getChildren().add(menuHdr);

        for (int i = 0; i < icons.length; i++) {
            final int idx = i;
            Button btn = new Button(icons[i] + "   " + labels[i]);
            btn.setStyle(ThemeManager.navNormal()); btn.setMaxWidth(Double.MAX_VALUE);
            navBtns[i] = btn;
            btn.setOnMouseEntered(e -> { if (!btn.getStyle().contains(accentHex)) btn.setStyle(ThemeManager.navHover()); });
            btn.setOnMouseExited(e  -> { if (!btn.getStyle().contains(accentHex)) btn.setStyle(ThemeManager.navNormal()); });
            btn.setOnAction(e -> {
                setActiveNav(idx, accentHex);
                switch (idx) {
                    case 0: showPanel(buildMyStatsPanel()); break;
                    case 1: showPanel(buildLiveResultsPanel()); break;
                    case 2: showPanel(buildCampaignInfoPanel()); break;
                }
            });
            navList.getChildren().add(btn);
        }
        setActiveNav(0, accentHex);

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox themeSection = new VBox(4);
        themeSection.setPadding(new Insets(0, 10, 4, 10));
        Button themeBtn = new Button(ThemeManager.toggleLabel());
        themeBtn.setStyle(ThemeManager.navNormal()); themeBtn.setMaxWidth(Double.MAX_VALUE);
        themeBtn.setOnAction(e -> ThemeManager.applyToggle(view.getScene(),
                () -> new CandidateDashboard(stage).getView()));
        themeSection.getChildren().add(themeBtn);

        VBox logoutSec = new VBox(4);
        logoutSec.setPadding(new Insets(0, 10, 14, 10));
        Region div = divider();
        Button logoutBtn = new Button("🚪   Logout");
        logoutBtn.setStyle(ThemeManager.navNormal() + "-fx-text-fill: " + ThemeManager.danger() + ";");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> { VoteService.getInstance().logout();
            view.getScene().setRoot(new LandingView(stage).getView()); });
        logoutSec.getChildren().addAll(div, logoutBtn);

        HBox userCard = new HBox(10);
        userCard.setAlignment(Pos.CENTER_LEFT);
        userCard.setPadding(new Insets(14, 16, 16, 16));
        userCard.setStyle("-fx-background-color: " + ThemeManager.activityBg() + ";");
        StackPane ava = new StackPane();
        ava.setStyle("-fx-background-color: " + ThemeManager.candidateAccentBg() + "; -fx-background-radius: 50; "
                + "-fx-min-width: 34; -fx-min-height: 34; -fx-max-width: 34; -fx-max-height: 34;");
        Label avaIco = new Label("🎯"); avaIco.setStyle("-fx-font-size: 14px;");
        ava.getChildren().add(avaIco);
        VBox um = new VBox(2);
        Label uNm = new Label(candidateName);
        uNm.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label uRl = new Label("Candidate");
        uRl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ThemeManager.textMuted() + ";");
        um.getChildren().addAll(uNm, uRl);
        userCard.getChildren().addAll(ava, um);

        sidebar.getChildren().addAll(brand, navList, spacer, themeSection, logoutSec, userCard);

        contentScroll = new ScrollPane();
        contentScroll.setFitToWidth(true);
        contentScroll.setStyle("-fx-background-color: transparent; -fx-background: "
                + ThemeManager.bgBase() + "; -fx-border-color: transparent;");
        view.setLeft(sidebar);
        view.setCenter(contentScroll);
    }

    private void setActiveNav(int idx, String accent) {
        if (navBtns == null) return;
        for (int i = 0; i < navBtns.length; i++)
            navBtns[i].setStyle(i == idx ? ThemeManager.navActive(accent) : ThemeManager.navNormal());
    }

    private void showPanel(VBox panel) {
        contentScroll.setContent(panel);
        panel.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(260), panel);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    // ── PANEL 1: MY STATS ────────────────────────────────────────────
    private VBox buildMyStatsPanel() {
        VBox panel = panelBase();
        VBox hdr = new VBox(4);
        Label t = new Label("My Campaign Stats");
        t.setStyle("-fx-font-size: 23px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label s = new Label("Welcome back, " + candidateName + ". Here's your live standing.");
        s.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");
        hdr.getChildren().addAll(t, s);

        VoteService vs = VoteService.getInstance();
        int myVotes = myCandidate != null ? myCandidate.getVoteCount() : 0;
        int tot = vs.getTotalVotesCast();
        double share = tot > 0 ? myVotes * 100.0 / tot : 0;
        int rank = computeRank();

        HBox kpiRow = new HBox(14);
        kpiRow.getChildren().addAll(
            kpi("🗳️","My Votes",  String.valueOf(myVotes), "Live count",    true,  ThemeManager.candidateAccentBg()),
            kpi("📊","Total Cast",String.valueOf(tot),     "All candidates",true,  ThemeManager.adminAccentBg()),
            kpi("🏅","My Rank",   "#"+rank,                "Position",      rank==1, "rgba(255,193,7,0.15)"),
            kpi("📈","Vote Share",String.format("%.1f%%",share),"Of total",  true,  ThemeManager.voterAccentBg())
        );

        // Bar chart
        VBox chartCard = glass();
        chartCard.getChildren().add(cTitle("Vote Comparison — All Candidates"));
        CategoryAxis xA = new CategoryAxis(); NumberAxis yA = new NumberAxis();
        BarChart<String,Number> bar = new BarChart<>(xA, yA);
        bar.setLegendVisible(false); bar.setAnimated(false); bar.setMinHeight(230);
        bar.setStyle("-fx-background-color: transparent;");
        XYChart.Series<String,Number> ser = new XYChart.Series<>();
        vs.getCandidates().forEach(c -> ser.getData().add(new XYChart.Data<>(c.getName(), c.getVoteCount())));
        bar.getData().add(ser);
        chartCard.getChildren().add(bar);

        // Progress bars
        VBox progCard = glass();
        progCard.getChildren().add(cTitle("Vote Progress"));
        String[] clrs = {ThemeManager.accentCyan(), ThemeManager.accent(), ThemeManager.accentTeal()};
        String[] pbc  = {"vote-progress-bar-cyan","vote-progress-bar","vote-progress-bar-teal"};
        int ci = 0;
        for (Candidate c : vs.getCandidates()) {
            VBox pRow = new VBox(5);
            HBox lr = new HBox(10); lr.setAlignment(Pos.CENTER_LEFT);
            Label cn = new Label(c.getName());
            cn.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: " + clrs[ci%3] + ";");
            if (c.equals(myCandidate)) {
                Label you = new Label("YOU");
                you.setStyle("-fx-font-size: 9px; -fx-font-weight: 800; -fx-text-fill: " + ThemeManager.accentCyan()
                        + "; -fx-background-radius: 8; -fx-padding: 1 6 1 6; "
                        + "-fx-background-color: " + ThemeManager.candidateAccentBg() + ";");
                lr.getChildren().addAll(cn, you);
            } else {
                lr.getChildren().add(cn);
            }
            Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS);
            Label vt = new Label(c.getVoteCount()+" votes");
            vt.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ThemeManager.textMuted() + ";");
            lr.getChildren().addAll(r, vt);
            ProgressBar pb = new ProgressBar(tot > 0 ? (double)c.getVoteCount()/tot : 0);
            pb.setMaxWidth(Double.MAX_VALUE); pb.setPrefHeight(9);
            pb.getStyleClass().add(pbc[ci%3]);
            pRow.getChildren().addAll(lr, pb);
            progCard.getChildren().add(pRow); ci++;
        }

        panel.getChildren().addAll(hdr, kpiRow, chartCard, progCard);
        return panel;
    }

    // ── PANEL 2: LIVE RESULTS ────────────────────────────────────────
    private VBox buildLiveResultsPanel() {
        VBox panel = panelBase();
        Label title = new Label("Live Results");
        title.setStyle("-fx-font-size: 23px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");

        VoteService vs = VoteService.getInstance();
        Candidate leader = vs.getWinner();
        if (leader != null && leader.getVoteCount() > 0) {
            panel.getChildren().addAll(title, winnerCard(leader));
        } else {
            panel.getChildren().add(title);
        }

        HBox charts = new HBox(16);
        VBox pieCard = glass(); HBox.setHgrow(pieCard, Priority.ALWAYS);
        pieCard.getChildren().add(cTitle("Vote Distribution"));
        ObservableList<PieChart.Data> pd = FXCollections.observableArrayList();
        vs.getCandidates().forEach(c -> pd.add(new PieChart.Data(c.getName()+" ("+c.getVoteCount()+")", Math.max(c.getVoteCount(),1))));
        PieChart pie = new PieChart(pd); pie.setLegendVisible(true); pie.setLabelsVisible(true);
        pie.setMinHeight(250); pieCard.getChildren().add(pie);

        VBox lb = leaderboard(vs); lb.setPrefWidth(300);
        charts.getChildren().addAll(pieCard, lb);
        panel.getChildren().add(charts);
        return panel;
    }

    // ── PANEL 3: CAMPAIGN INFO ───────────────────────────────────────
    private VBox buildCampaignInfoPanel() {
        VBox panel = panelBase();
        Label title = new Label("Campaign Info");
        title.setStyle("-fx-font-size: 23px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");

        VBox card = glass(); card.setMaxWidth(500);
        StackPane ava = new StackPane();
        ava.setStyle("-fx-background-color: " + ThemeManager.candidateAccentBg() + "; -fx-background-radius: 50; "
                + "-fx-min-width: 72; -fx-min-height: 72; -fx-max-width: 72; -fx-max-height: 72;");
        Label avaIco = new Label("🎯"); avaIco.setStyle("-fx-font-size: 32px;");
        ava.getChildren().add(avaIco);

        Label nm = new Label(candidateName);
        nm.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label py = new Label(myCandidate != null ? myCandidate.getParty() : "Independent");
        py.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.accentCyan() + ";");

        Region dv = divider();
        card.getChildren().add(cTitle("Candidate Details"));
        VBox fields = new VBox(13);
        fields.getChildren().addAll(
            pRow("Candidate ID", myCandidate != null ? "CAND-00"+myCandidate.getCandidateId() : "-"),
            pRow("Full Name", candidateName),
            pRow("Party", myCandidate != null ? myCandidate.getParty() : "Independent"),
            pRow("Current Votes", myCandidate != null ? String.valueOf(myCandidate.getVoteCount()) : "0"),
            pRow("Current Rank", "#"+computeRank()),
            pRow("Election", "General Election 2026")
        );
        card.getChildren().addAll(ava, nm, py, dv, fields);
        panel.getChildren().addAll(title, card);
        return panel;
    }

    // ── HELPERS ──────────────────────────────────────────────────────
    private int computeRank() {
        if (myCandidate == null) return 0;
        int r = 1;
        for (Candidate c : VoteService.getInstance().getCandidates())
            if (!c.equals(myCandidate) && c.getVoteCount() > myCandidate.getVoteCount()) r++;
        return r;
    }

    private VBox panelBase() {
        VBox p = new VBox(22); p.setPadding(new Insets(30, 34, 30, 34));
        p.setStyle("-fx-background-color: " + ThemeManager.bgBase() + ";"); return p;
    }

    private VBox glass() {
        VBox v = new VBox(12); v.setStyle(ThemeManager.glassCard()); return v;
    }

    private Label cTitle(String t) {
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
        ib.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 11; -fx-min-width: 44; -fx-min-height: 44; -fx-max-width: 44; -fx-max-height: 44;");
        Label ii = new Label(icon); ii.setStyle("-fx-font-size: 19px;"); ib.getChildren().add(ii);
        Label l = new Label(lbl); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ThemeManager.textSecondary() + "; -fx-font-weight: 700;");
        Label v = new Label(val); v.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label tr = new Label((pos?"↑ ":"↓ ")+trend); tr.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (pos?"#00c98a":"#ff5470") + "; -fx-font-weight: 700;");
        c.getChildren().addAll(ib, l, v, tr); return c;
    }

    private HBox winnerCard(Candidate w) {
        HBox card = new HBox(15); card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: rgba(255,193,7,0.08); -fx-background-radius: 14; "
                + "-fx-border-color: rgba(255,193,7,0.28); -fx-border-width: 1; -fx-border-radius: 14; "
                + "-fx-padding: 16 20 16 20;");
        Label tr = new Label("🏆"); tr.setStyle("-fx-font-size: 30px;");
        VBox wi = new VBox(3);
        Label wl = new Label("CURRENTLY LEADING"); wl.setStyle("-fx-font-size: 10px; -fx-font-weight: 800; -fx-text-fill: #ffc107;");
        Label wn = new Label(w.getName()); wn.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #ffc107;");
        Label ws = new Label(w.getVoteCount()+" votes · "+w.getParty()); ws.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");
        wi.getChildren().addAll(wl, wn, ws);
        card.getChildren().addAll(tr, wi); return card;
    }

    private VBox leaderboard(VoteService vs) {
        VBox lb = glass(); lb.getChildren().add(cTitle("Standings"));
        int rank = 1; int tot = vs.getTotalVotesCast();
        String[] rc  = {"#ffc107", ThemeManager.accent(), ThemeManager.accentCyan()};
        String[] pbc = {"vote-progress-bar","vote-progress-bar-cyan","vote-progress-bar-teal"};
        for (Candidate c : vs.getCandidates()) {
            VBox rBox = new VBox(5); rBox.setPadding(new Insets(7,0,7,0));
            HBox top = new HBox(9); top.setAlignment(Pos.CENTER_LEFT);
            Label rk = new Label("#"+rank); rk.setStyle("-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: "+rc[Math.min(rank-1,2)]+"; -fx-min-width: 30;");
            VBox inf = new VBox(2); HBox.setHgrow(inf,Priority.ALWAYS);
            Label cn = new Label(c.getName()); cn.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: "+ThemeManager.textPrimary()+";");
            if (c.equals(myCandidate)) {
                Label you = new Label("YOU"); you.setStyle("-fx-font-size: 9px; -fx-font-weight: 800; -fx-text-fill: "+ThemeManager.accentCyan()+"; -fx-background-color: "+ThemeManager.candidateAccentBg()+"; -fx-background-radius: 8; -fx-padding: 1 6 1 6;");
                HBox nr = new HBox(7, cn, you); nr.setAlignment(Pos.CENTER_LEFT);
                inf.getChildren().add(nr);
            } else { inf.getChildren().add(cn); }
            Label cp = new Label(c.getParty()); cp.setStyle("-fx-font-size: 11px; -fx-text-fill: "+ThemeManager.textSecondary()+";");
            inf.getChildren().add(cp);
            Label vt = new Label(String.valueOf(c.getVoteCount())); vt.setStyle("-fx-font-size: 17px; -fx-font-weight: 900; -fx-text-fill: "+rc[Math.min(rank-1,2)]+";");
            top.getChildren().addAll(rk, inf, vt);
            ProgressBar pb = new ProgressBar(tot>0?(double)c.getVoteCount()/tot:0);
            pb.setMaxWidth(Double.MAX_VALUE); pb.setPrefHeight(7); pb.getStyleClass().add(pbc[Math.min(rank-1,2)]);
            rBox.getChildren().addAll(top,pb);
            lb.getChildren().add(rBox); rank++;
        }
        return lb;
    }

    private HBox pRow(String lbl, String val) {
        HBox r = new HBox(18); r.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(lbl); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ThemeManager.textMuted() + "; -fx-font-weight: 700; -fx-min-width: 150;");
        Label v = new Label(val); v.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + "; -fx-font-weight: 600;");
        r.getChildren().addAll(l, v); return r;
    }

    public BorderPane getView() { return view; }
}