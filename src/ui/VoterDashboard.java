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
import service.SmsService;
import service.VoteService;

public class VoterDashboard {

    private BorderPane view = new BorderPane();
    private Stage stage;
    private ScrollPane contentScroll;
    private Button[] navBtns;
    private User currentUser;
    private Candidate selectedCandidate = null;

    public VoterDashboard(Stage stage) {
        this.stage = stage;
        this.currentUser = VoteService.getInstance().getCurrentUser();
        view.setStyle("-fx-background-color: " + ThemeManager.bgBase() + ";");
        buildSidebar();
        showPanel(buildCastVotePanel());
    }

    private void buildSidebar() {
        String accentHex = ThemeManager.accentTeal();

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

        Label menuHdr = new Label("VOTER MENU");
        menuHdr.setStyle("-fx-font-size: 10px; -fx-font-weight: 700; -fx-text-fill: " + ThemeManager.textMuted()
                + "; -fx-padding: 8 20 4 20;");

        String[] icons  = {"🗳️","📊","👤"};
        String[] labels = {"Cast Vote","Results","My Profile"};
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
                    case 0: showPanel(buildCastVotePanel()); break;
                    case 1: showPanel(buildResultsPanel()); break;
                    case 2: showPanel(buildMyProfilePanel()); break;
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
                () -> new VoterDashboard(stage).getView()));
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

        String uName = currentUser != null ? currentUser.getName() : "Voter";
        String uId   = currentUser != null ? currentUser.getVoterId() : "";
        HBox userCard = new HBox(10);
        userCard.setAlignment(Pos.CENTER_LEFT);
        userCard.setPadding(new Insets(14, 16, 16, 16));
        userCard.setStyle("-fx-background-color: " + ThemeManager.activityBg() + ";");
        StackPane ava = new StackPane();
        ava.setStyle("-fx-background-color: " + ThemeManager.voterAccentBg() + "; -fx-background-radius: 50; "
                + "-fx-min-width: 34; -fx-min-height: 34; -fx-max-width: 34; -fx-max-height: 34;");
        Label avaIco = new Label("🗳️"); avaIco.setStyle("-fx-font-size: 14px;");
        ava.getChildren().add(avaIco);
        VBox um = new VBox(2);
        Label un = new Label(uName); un.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label ur = new Label(uId);   ur.setStyle("-fx-font-size: 10px; -fx-text-fill: " + ThemeManager.textMuted() + "; -fx-font-family: 'Courier New';");
        um.getChildren().addAll(un, ur);
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

    // ── PANEL 1: CAST VOTE ───────────────────────────────────────────
    private VBox buildCastVotePanel() {
        VBox panel = panelBase();
        boolean hasVoted = currentUser != null && currentUser.hasVoted();
        boolean elecOpen = VoteService.getInstance().isElectionOpen();
        String voterName = currentUser != null ? currentUser.getName() : "Voter";

        Label title = new Label(hasVoted ? "Your Vote Is Counted ✓" : "Cast Your Vote");
        title.setStyle("-fx-font-size: 23px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label sub = new Label("Hello, " + voterName + ". " + (hasVoted
                ? "Thank you for participating." : elecOpen
                ? "Select your preferred candidate below." : "The election is currently closed."));
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");
        VBox hdr = new VBox(4, title, sub);

        // Status banner
        HBox banner = new HBox(12); banner.setAlignment(Pos.CENTER_LEFT);
        banner.setStyle("-fx-background-color: " + (elecOpen ? "rgba(0,201,138,0.06)" : "rgba(255,84,112,0.06)")
                + "; -fx-background-radius: 13; -fx-border-color: "
                + (elecOpen ? "rgba(0,201,138,0.2)" : "rgba(255,84,112,0.2)")
                + "; -fx-border-radius: 13; -fx-border-width: 1; -fx-padding: 14 18 14 18;");
        Label bIcon = new Label(elecOpen ? "🏛️" : "🔒"); bIcon.setStyle("-fx-font-size: 22px;");
        VBox bInfo = new VBox(3);
        Label bName = new Label("General Election 2026");
        bName.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label bState = new Label(elecOpen ? "● Election is OPEN — Voting in progress" : "● Election is CLOSED");
        bState.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (elecOpen ? "#00c98a" : "#ff5470") + "; -fx-font-weight: 700;");
        bInfo.getChildren().addAll(bName, bState);
        banner.getChildren().addAll(bIcon, bInfo);

        if (hasVoted) {
            VBox sc = glass(); sc.setAlignment(Pos.CENTER); sc.setMaxWidth(480);
            Label sIco = new Label("✔"); sIco.setStyle("-fx-font-size: 56px; -fx-text-fill: #00c98a;");
            Label sTit = new Label("Vote Successfully Cast!");
            sTit.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
            Label sTxt = new Label("Your vote has been securely recorded.\nThank you for participating.");
            sTxt.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary()
                    + "; -fx-text-alignment: center; -fx-line-spacing: 4;");
            sTxt.setAlignment(Pos.CENTER);
            Label badge = new Label("✓  Verified Voter");
            badge.setStyle("-fx-font-size: 13px; -fx-text-fill: #00c98a; -fx-font-weight: 800; "
                    + "-fx-background-color: " + ThemeManager.voterAccentBg() + "; "
                    + "-fx-background-radius: 20; -fx-padding: 6 16 6 16; "
                    + "-fx-border-color: rgba(0,201,138,0.3); -fx-border-radius: 20; -fx-border-width: 1;");
            Button viewRes = new Button("View Election Results →");
            viewRes.setStyle("-fx-background-color: " + ThemeManager.accentTeal() + "; -fx-text-fill: #0d0f1a; "
                    + "-fx-font-size: 13px; -fx-font-weight: 800; -fx-background-radius: 10; "
                    + "-fx-padding: 11 22 11 22; -fx-cursor: hand; -fx-border-color: transparent;");
            viewRes.setOnAction(e -> { setActiveNav(1, ThemeManager.accentTeal()); showPanel(buildResultsPanel()); });
            sc.getChildren().addAll(sIco, sTit, sTxt, badge, viewRes);
            panel.getChildren().addAll(hdr, banner, sc);
            return panel;
        }

        if (!elecOpen) {
            VBox closed = glass(); closed.setAlignment(Pos.CENTER); closed.setMaxWidth(380);
            Label cIco = new Label("🔒"); cIco.setStyle("-fx-font-size: 44px;");
            Label cTit = new Label("Election Closed");
            cTit.setStyle("-fx-font-size: 17px; -fx-font-weight: 800; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
            Label cSub = new Label("Please wait for the admin to open the election.");
            cSub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + "; -fx-text-alignment: center;");
            cSub.setAlignment(Pos.CENTER);
            closed.getChildren().addAll(cIco, cTit, cSub);
            panel.getChildren().addAll(hdr, banner, closed);
            return panel;
        }

        // Candidate cards
        Label selLbl = new Label("Select Your Candidate");
        selLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label selSub = new Label("Click a card to select, then confirm below.");
        selSub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");

        HBox cardRow = new HBox(16);
        String[] clrs = {ThemeManager.accent(), ThemeManager.accentCyan(), ThemeManager.accentTeal()};
        String[] icons = {"🟣","🔵","🟢"};
        String[] bgs   = {ThemeManager.adminAccentBg(), ThemeManager.candidateAccentBg(), ThemeManager.voterAccentBg()};

        VBox[] candCards = new VBox[VoteService.getInstance().getCandidates().size()];
        Label[] confirmRef = new Label[1];
        Button[] castRef   = new Button[1];

        int ci = 0;
        for (Candidate c : VoteService.getInstance().getCandidates()) {
            final int cIdx = ci;
            final Candidate cFinal = c;
            VBox card = new VBox(13);
            card.setStyle(ThemeManager.glassCard() + " -fx-cursor: hand;");
            card.setAlignment(Pos.TOP_LEFT); card.setPrefWidth(215);
            candCards[ci] = card;

            StackPane av = new StackPane();
            av.setStyle("-fx-background-color: " + bgs[ci%3] + "; -fx-background-radius: 12; "
                    + "-fx-min-width: 48; -fx-min-height: 48; -fx-max-width: 48; -fx-max-height: 48;");
            Label avIco = new Label(icons[ci%3]); avIco.setStyle("-fx-font-size: 22px;");
            av.getChildren().add(avIco);
            Label cn = new Label(c.getName());
            cn.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
            Label cp = new Label(c.getParty());
            cp.setStyle("-fx-font-size: 12px; -fx-text-fill: " + clrs[ci%3] + ";");
            Region dv = new Region(); dv.setStyle("-fx-background-color: " + ThemeManager.divider() + "; -fx-min-height: 1; -fx-max-height: 1;");
            Button selBtn = new Button("Select");
            selBtn.setStyle("-fx-background-color: " + bgs[ci%3] + "; -fx-text-fill: " + clrs[ci%3] + "; "
                    + "-fx-font-size: 12px; -fx-font-weight: 700; -fx-background-radius: 9; "
                    + "-fx-border-color: " + clrs[ci%3] + "44; -fx-border-radius: 9; -fx-border-width: 1; "
                    + "-fx-padding: 8 14 8 14; -fx-cursor: hand;");
            selBtn.setMaxWidth(Double.MAX_VALUE);
            card.getChildren().addAll(av, cn, cp, dv, selBtn);
            cardRow.getChildren().add(card);

            selBtn.setOnAction(e -> {
                for (VBox vc : candCards) vc.setStyle(ThemeManager.glassCard() + " -fx-cursor: hand;");
                card.setStyle(ThemeManager.glassCard() + " -fx-cursor: hand; "
                        + "-fx-border-color: " + clrs[cIdx%3] + "; -fx-border-radius: 16;");
                selectedCandidate = cFinal;
                if (castRef[0] != null) { castRef[0].setDisable(false); castRef[0].setOpacity(1); }
                if (confirmRef[0] != null) {
                    confirmRef[0].setText("Selected: " + cFinal.getName() + " · " + cFinal.getParty());
                }
            });
            ci++;
        }

        VBox confirmArea = glass();
        HBox confRow = new HBox(14); confRow.setAlignment(Pos.CENTER_LEFT);
        Label confLbl = new Label("No candidate selected yet");
        confLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");
        confirmRef[0] = confLbl;
        Region rr = new Region(); HBox.setHgrow(rr, Priority.ALWAYS);
        Button castBtn = new Button("🗳️  Cast Vote");
        castBtn.setStyle("-fx-background-color: " + ThemeManager.accentTeal() + "; -fx-text-fill: #0d0f1a; "
                + "-fx-font-size: 13px; -fx-font-weight: 800; -fx-background-radius: 10; "
                + "-fx-padding: 11 22 11 22; -fx-cursor: hand; -fx-border-color: transparent;");
        castBtn.setDisable(true); castBtn.setOpacity(0.5);
        castRef[0] = castBtn;
        castBtn.setOnAction(e -> {
            if (selectedCandidate == null) return;
            Alert dlg = new Alert(Alert.AlertType.CONFIRMATION);
            dlg.setTitle("Confirm Vote");
            dlg.setHeaderText("Voting for: " + selectedCandidate.getName());
            dlg.setContentText("Party: " + selectedCandidate.getParty() + "\n\nThis cannot be undone. Proceed?");
            dlg.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    boolean ok = VoteService.getInstance().castVote(selectedCandidate.getCandidateId());
                    if (ok) {
                        if (currentUser != null) SmsService.send(currentUser.getMobile(), "Vote cast. Thank you!");
                        showPanel(buildCastVotePanel()); setActiveNav(0, ThemeManager.accentTeal());
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Vote failed. You may have already voted.").show();
                    }
                }
            });
        });
        confRow.getChildren().addAll(confLbl, rr, castBtn);
        confirmArea.getChildren().add(confRow);
        panel.getChildren().addAll(hdr, banner, new VBox(4, selLbl, selSub), cardRow, confirmArea);
        return panel;
    }

    // ── PANEL 2: RESULTS ────────────────────────────────────────────
    private VBox buildResultsPanel() {
        VBox panel = panelBase();
        Label title = new Label("Election Results");
        title.setStyle("-fx-font-size: 23px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");

        VoteService vs = VoteService.getInstance();
        if (!vs.isElectionOpen()) {
            panel.getChildren().add(title);
            Candidate w = vs.getWinner();
            if (w != null && w.getVoteCount() > 0) panel.getChildren().add(winnerCard(w));
        } else {
            Label s = new Label("Live results — updates as votes are cast.");
            s.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");
            panel.getChildren().addAll(title, s);
        }

        HBox charts = new HBox(16);
        VBox pieC = glass(); HBox.setHgrow(pieC, Priority.ALWAYS);
        pieC.getChildren().add(cTitle("Vote Distribution"));
        ObservableList<PieChart.Data> pd = FXCollections.observableArrayList();
        vs.getCandidates().forEach(c -> pd.add(new PieChart.Data(c.getName()+" ("+c.getVoteCount()+")",Math.max(c.getVoteCount(),1))));
        PieChart pie = new PieChart(pd); pie.setLegendVisible(true); pie.setLabelsVisible(true); pie.setMinHeight(255);
        pieC.getChildren().add(pie);

        VBox lb = leaderboard(vs); lb.setPrefWidth(305);
        charts.getChildren().addAll(pieC, lb);
        panel.getChildren().add(charts);
        return panel;
    }

    // ── PANEL 3: MY PROFILE ─────────────────────────────────────────
    private VBox buildMyProfilePanel() {
        VBox panel = panelBase();
        Label title = new Label("My Profile");
        title.setStyle("-fx-font-size: 23px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");

        String nm   = currentUser != null ? currentUser.getName()    : "Voter";
        String id   = currentUser != null ? currentUser.getVoterId() : "-";
        boolean hv  = currentUser != null && currentUser.hasVoted();

        VBox card = glass(); card.setMaxWidth(500);
        StackPane ava = new StackPane();
        ava.setStyle("-fx-background-color: " + ThemeManager.voterAccentBg() + "; -fx-background-radius: 50; "
                + "-fx-min-width: 68; -fx-min-height: 68; -fx-max-width: 68; -fx-max-height: 68;");
        Label avaIco = new Label("🗳️"); avaIco.setStyle("-fx-font-size: 30px;");
        ava.getChildren().add(avaIco);

        Label nameL = new Label(nm); nameL.setStyle("-fx-font-size: 21px; -fx-font-weight: 900; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        Label idL   = new Label("Voter ID: " + id); idL.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ThemeManager.textMuted() + "; -fx-font-family: 'Courier New';");

        HBox badges = new HBox(10); badges.setAlignment(Pos.CENTER_LEFT);
        Label vbg = new Label(hv ? "✓  Vote Cast" : "○  Not Voted");
        vbg.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-background-radius: 20; -fx-padding: 4 12 4 12; "
                + (hv ? "-fx-background-color: rgba(0,201,138,0.15); -fx-text-fill: #00c98a;"
                      : "-fx-background-color: rgba(136,146,176,0.12); -fx-text-fill: " + ThemeManager.textSecondary() + ";"));
        Label ebg = new Label(VoteService.getInstance().isElectionOpen() ? "● Open" : "● Closed");
        ebg.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-background-radius: 20; -fx-padding: 4 12 4 12; "
                + (VoteService.getInstance().isElectionOpen()
                    ? "-fx-background-color: rgba(0,201,138,0.12); -fx-text-fill: #00c98a;"
                    : "-fx-background-color: rgba(255,84,112,0.12); -fx-text-fill: #ff5470;"));
        badges.getChildren().addAll(vbg, ebg);

        Region dv = divider();
        card.getChildren().add(cTitle("Voter Details"));
        VBox fields = new VBox(12);
        fields.getChildren().addAll(
            pRow("Voter ID",  id),
            pRow("Full Name", nm),
            pRow("Email",     currentUser != null ? currentUser.getEmail()  : "-"),
            pRow("Mobile",    currentUser != null ? currentUser.getMobile() : "-"),
            pRow("Voted",     hv ? "Yes — Confirmed" : "No"),
            pRow("Election",  "General Election 2026")
        );
        card.getChildren().addAll(ava, nameL, idL, badges, dv, fields);

        panel.getChildren().addAll(title, card);

        if (!hv && VoteService.getInstance().isElectionOpen()) {
            VBox tip = glass(); tip.setMaxWidth(500);
            Label tipHdr = new Label("💡 Ready to Vote?");
            tipHdr.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: " + ThemeManager.accentTeal() + ";");
            Label tipSub = new Label("You haven't voted yet. Head to 'Cast Vote' to participate.");
            tipSub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");
            Button goBtn = new Button("Go to Cast Vote →");
            goBtn.setStyle("-fx-background-color: " + ThemeManager.accentTeal() + "; -fx-text-fill: #0d0f1a; "
                    + "-fx-font-size: 13px; -fx-font-weight: 800; -fx-background-radius: 10; "
                    + "-fx-padding: 10 20 10 20; -fx-cursor: hand; -fx-border-color: transparent;");
            goBtn.setOnAction(e -> { setActiveNav(0, ThemeManager.accentTeal()); showPanel(buildCastVotePanel()); });
            tip.getChildren().addAll(tipHdr, tipSub, goBtn);
            panel.getChildren().add(tip);
        }
        return panel;
    }

    // ── HELPERS ──────────────────────────────────────────────────────
    private VBox panelBase() {
        VBox p = new VBox(22); p.setPadding(new Insets(30, 34, 30, 34));
        p.setStyle("-fx-background-color: " + ThemeManager.bgBase() + ";"); return p;
    }
    private VBox glass() { VBox v = new VBox(12); v.setStyle(ThemeManager.glassCard()); return v; }
    private Label cTitle(String t) {
        Label l = new Label(t); l.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: " + ThemeManager.textPrimary() + ";");
        return l;
    }
    private Region divider() {
        Region r = new Region(); r.setStyle("-fx-background-color: " + ThemeManager.divider() + "; -fx-min-height: 1; -fx-max-height: 1;");
        return r;
    }

    private HBox winnerCard(Candidate w) {
        HBox c = new HBox(15); c.setAlignment(Pos.CENTER_LEFT);
        c.setStyle("-fx-background-color: rgba(255,193,7,0.08); -fx-background-radius: 14; "
                + "-fx-border-color: rgba(255,193,7,0.28); -fx-border-width: 1; -fx-border-radius: 14; -fx-padding: 16 20 16 20;");
        Label tr = new Label("🏆"); tr.setStyle("-fx-font-size: 30px;");
        VBox wi = new VBox(3);
        Label wl = new Label("WINNER"); wl.setStyle("-fx-font-size: 10px; -fx-font-weight: 800; -fx-text-fill: #ffc107;");
        Label wn = new Label(w.getName()); wn.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #ffc107;");
        Label ws = new Label(w.getVoteCount()+" votes · "+w.getParty()); ws.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ThemeManager.textSecondary() + ";");
        wi.getChildren().addAll(wl, wn, ws); c.getChildren().addAll(tr, wi); return c;
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
            VBox inf = new VBox(2); HBox.setHgrow(inf, Priority.ALWAYS);
            Label cn = new Label(c.getName()); cn.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: "+ThemeManager.textPrimary()+";");
            Label cp = new Label(c.getParty()); cp.setStyle("-fx-font-size: 11px; -fx-text-fill: "+ThemeManager.textSecondary()+";");
            inf.getChildren().addAll(cn, cp);
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
        Label l = new Label(lbl); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ThemeManager.textMuted() + "; -fx-font-weight: 700; -fx-min-width: 120;");
        Label v = new Label(val); v.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ThemeManager.textSecondary() + "; -fx-font-weight: 600;");
        r.getChildren().addAll(l, v); return r;
    }

    public BorderPane getView() { return view; }
}