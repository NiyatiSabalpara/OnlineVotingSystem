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
        view.setStyle("-fx-background-color: #0d0f1a;");
        buildSidebar();
        showPanel(buildCastVotePanel());
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
        brandDot.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #00e5a0;");
        brandBox.getChildren().addAll(brandLabel, brandDot);

        String[][] navItems = {
            {"🗳️", "Cast Vote"},
            {"📊", "Results"},
            {"👤", "My Profile"}
        };

        VBox navList = new VBox(2);
        navList.setPadding(new Insets(0, 10, 0, 10));

        Label menuHeader = new Label("VOTER MENU");
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
                    case 0: showPanel(buildCastVotePanel()); break;
                    case 1: showPanel(buildResultsPanel()); break;
                    case 2: showPanel(buildMyProfilePanel()); break;
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

        // User card at bottom
        HBox userCard = new HBox(12);
        userCard.setAlignment(Pos.CENTER_LEFT);
        userCard.setPadding(new Insets(15, 20, 15, 20));
        userCard.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 1 0 0 0;");
        StackPane ava = new StackPane();
        ava.setStyle("-fx-background-color: rgba(0,229,160,0.15); -fx-background-radius: 50; -fx-min-width: 36; -fx-min-height: 36; -fx-max-width: 36; -fx-max-height: 36;");
        Label avaIcon = new Label("🗳️"); avaIcon.setStyle("-fx-font-size: 16px;");
        ava.getChildren().add(avaIcon);
        VBox userInfo = new VBox(2);
        String uName = currentUser != null ? currentUser.getName() : "Voter";
        Label displayName = new Label(uName); displayName.getStyleClass().add("sidebar-user-name");
        Label uRole = new Label(currentUser != null ? currentUser.getVoterId() : ""); uRole.getStyleClass().add("sidebar-user-role");
        userInfo.getChildren().addAll(displayName, uRole);
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
            navBtns[i].getStyleClass().removeAll("sidebar-nav-btn-active-teal");
            if (i == activeIdx) navBtns[i].getStyleClass().add("sidebar-nav-btn-active-teal");
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
    // PANEL 1: CAST VOTE
    // ================================================================
    private VBox buildCastVotePanel() {
        VBox panel = new VBox(28);
        panel.getStyleClass().add("content-area");

        boolean hasVoted = currentUser != null && currentUser.hasVoted();
        boolean electionOpen = VoteService.getInstance().isElectionOpen();
        String voterName = currentUser != null ? currentUser.getName() : "Voter";

        // Page header
        VBox pageHeader = new VBox(4);
        Label title = new Label(hasVoted ? "Your Vote Is Counted ✓" : "Cast Your Vote");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Hello, " + voterName + ". " + (hasVoted ? "Thank you for participating in this election."
                : electionOpen ? "Select your preferred candidate below." : "The election is currently closed."));
        sub.getStyleClass().add("page-subtitle");
        pageHeader.getChildren().addAll(title, sub);

        // Election status banner
        HBox electionBanner = new HBox(12);
        electionBanner.setAlignment(Pos.CENTER_LEFT);
        electionBanner.setStyle("-fx-background-color: " + (electionOpen ? "rgba(0,229,160,0.06)" : "rgba(255,84,112,0.06)")
                + "; -fx-background-radius: 14; -fx-border-color: " + (electionOpen ? "rgba(0,229,160,0.2)" : "rgba(255,84,112,0.2)")
                + "; -fx-border-radius: 14; -fx-border-width: 1; -fx-padding: 16 20 16 20;");
        Label elecIcon = new Label(electionOpen ? "🏛️" : "🔒"); elecIcon.setStyle("-fx-font-size: 24px;");
        VBox elecInfo = new VBox(3);
        Label elecName = new Label("General Election 2026"); elecName.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");
        Label elecStatus = new Label(electionOpen ? "● Election is OPEN — Voting in progress" : "● Election is CLOSED — Voting not allowed");
        elecStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (electionOpen ? "#00e5a0" : "#ff5470") + "; -fx-font-weight: 700;");
        elecInfo.getChildren().addAll(elecName, elecStatus);
        Label elecBadge = new Label(electionOpen ? "ACTIVE" : "CLOSED");
        elecBadge.getStyleClass().add(electionOpen ? "badge-open" : "badge-closed");
        Region elecSpacer = new Region(); HBox.setHgrow(elecSpacer, Priority.ALWAYS);
        electionBanner.getChildren().addAll(elecIcon, elecInfo, elecSpacer, elecBadge);

        // If already voted => success card
        if (hasVoted) {
            VBox successCard = new VBox(16);
            successCard.getStyleClass().add("success-card");
            successCard.setAlignment(Pos.CENTER);
            successCard.setMaxWidth(500);
            Label sIcon = new Label("✔"); sIcon.getStyleClass().add("success-icon");
            sIcon.setStyle("-fx-font-size: 60px; -fx-text-fill: #00e5a0;");
            Label sTitle = new Label("Vote Successfully Cast!");
            sTitle.getStyleClass().add("success-title");
            Label sText = new Label("Your vote has been securely recorded.\nThank you for participating in the democratic process.");
            sText.getStyleClass().add("success-text"); sText.setAlignment(Pos.CENTER);

            Label votedBadge = new Label("✓  Verified Voter");
            votedBadge.setStyle("-fx-font-size: 13px; -fx-text-fill: #00e5a0; -fx-font-weight: 800; "
                    + "-fx-background-color: rgba(0,229,160,0.1); -fx-background-radius: 20; -fx-padding: 6 16 6 16; "
                    + "-fx-border-color: rgba(0,229,160,0.3); -fx-border-radius: 20; -fx-border-width: 1;");

            Button viewResults = new Button("View Election Results →");
            viewResults.getStyleClass().add("btn-primary-teal");
            viewResults.setOnAction(e -> { setActiveNav(1); showPanel(buildResultsPanel()); });

            successCard.getChildren().addAll(sIcon, sTitle, sText, votedBadge, viewResults);

            StackPane successWrapper = new StackPane(successCard);
            successWrapper.setAlignment(Pos.CENTER);

            VBox container = new VBox(20, pageHeader, electionBanner, successWrapper);
            container.setPadding(new Insets(35, 40, 35, 40));
            return container;
        }

        // Election closed => placeholder
        if (!electionOpen) {
            VBox closedCard = new VBox(15);
            closedCard.getStyleClass().add("glass-card");
            closedCard.setAlignment(Pos.CENTER);
            closedCard.setMaxWidth(400);
            Label closedIcon = new Label("🔒"); closedIcon.setStyle("-fx-font-size: 48px;");
            Label closedTitle = new Label("Election Currently Closed");
            closedTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");
            Label closedSub = new Label("Please wait for the administrator\nto open the election.");
            closedSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #8892b0; -fx-text-alignment: center;");
            closedSub.setAlignment(Pos.CENTER);
            closedCard.getChildren().addAll(closedIcon, closedTitle, closedSub);
            StackPane closedWrapper = new StackPane(closedCard); closedWrapper.setAlignment(Pos.CENTER);
            panel.getChildren().addAll(pageHeader, electionBanner, closedWrapper);
            return panel;
        }

        // Candidate Cards
        Label selectLabel = new Label("Select Your Candidate");
        selectLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: 800; -fx-text-fill: #e8eaf6;");
        Label selectSub = new Label("Click a candidate card to select, then confirm your vote.");
        selectSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #8892b0;");
        VBox selectHeader = new VBox(4, selectLabel, selectSub);

        // Candidate card grid
        HBox[] cardHolders = new HBox[1];
        cardHolders[0] = new HBox(16);
        cardHolders[0].setAlignment(Pos.CENTER_LEFT);

        // Tracking selected card
        VBox[] selectedCardRef = new VBox[1];
        Label[] confirmSelectedLabel = new Label[1];
        Button[] castBtnRef = new Button[1];
        Label[] confirmLabelRef = new Label[VoteService.getInstance().getCandidates().size()];

        String[] icons = {"🟣", "🔵", "🟢"};
        String[] colors = {"#6c63ff", "#00d4ff", "#00e5a0"};
        VBox[] candidateCards = new VBox[VoteService.getInstance().getCandidates().size()];

        int ci = 0;
        for (Candidate c : VoteService.getInstance().getCandidates()) {
            final int cIdx = ci;
            final Candidate cFinal = c;

            VBox card = new VBox(14);
            card.getStyleClass().add("vote-candidate-card");
            card.setAlignment(Pos.TOP_LEFT);
            card.setPrefWidth(220);
            candidateCards[ci] = card;

            // Avatar
            StackPane av = new StackPane();
            av.setStyle("-fx-background-color: " + colors[ci % 3] + "22; -fx-background-radius: 50; "
                    + "-fx-min-width: 60; -fx-min-height: 60; -fx-max-width: 60; -fx-max-height: 60;");
            Label avIcon = new Label(icons[ci % 3]); avIcon.setStyle("-fx-font-size: 26px;");
            av.getChildren().add(avIcon);

            Label cName = new Label(c.getName());
            cName.getStyleClass().add("candidate-name-text");
            Label cParty = new Label(c.getParty());
            cParty.getStyleClass().add("candidate-party-text");

            Region divLine = new Region(); divLine.getStyleClass().add("divider-line");

            Button selectBtn = new Button("Select Candidate");
            selectBtn.setStyle("-fx-background-color: " + colors[ci % 3] + "22; -fx-text-fill: " + colors[ci % 3] + "; "
                    + "-fx-font-size: 13px; -fx-font-weight: 700; -fx-background-radius: 10; "
                    + "-fx-border-color: " + colors[ci % 3] + "44; -fx-border-radius: 10; -fx-border-width: 1; "
                    + "-fx-padding: 9 15 9 15; -fx-cursor: hand;");
            selectBtn.setMaxWidth(Double.MAX_VALUE);

            card.getChildren().addAll(av, cName, cParty, divLine, selectBtn);
            cardHolders[0].getChildren().add(card);

            final String accent = colors[ci % 3];
            selectBtn.setOnAction(e -> {
                // Reset all cards
                for (VBox vc : candidateCards) {
                    vc.getStyleClass().removeAll("vote-candidate-card-selected");
                }
                card.getStyleClass().add("vote-candidate-card-selected");
                selectedCandidate = cFinal;
                if (castBtnRef[0] != null) {
                    castBtnRef[0].setDisable(false);
                    castBtnRef[0].setStyle(castBtnRef[0].getStyle().replace("opacity: 0.5;", ""));
                }
                if (confirmSelectedLabel[0] != null) {
                    confirmSelectedLabel[0].setText("Selected: " + cFinal.getName() + " · " + cFinal.getParty());
                    confirmSelectedLabel[0].setVisible(true);
                }
            });

            ci++;
        }

        // Confirm / Cast button area
        VBox confirmArea = new VBox(14);
        confirmArea.getStyleClass().add("glass-card");
        confirmArea.setAlignment(Pos.CENTER_LEFT);

        HBox confirmRow = new HBox(15);
        confirmRow.setAlignment(Pos.CENTER_LEFT);

        Label confirmLabel = new Label("No candidate selected yet");
        confirmLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8892b0;");
        confirmSelectedLabel[0] = confirmLabel;

        Region conf_spacer = new Region(); HBox.setHgrow(conf_spacer, Priority.ALWAYS);

        Button castBtn = new Button("🗳️  Cast Vote");
        castBtn.getStyleClass().add("btn-primary-teal");
        castBtn.setDisable(true);
        castBtn.setStyle(castBtn.getStyle() + " -fx-opacity: 0.5;");
        castBtnRef[0] = castBtn;

        castBtn.setDisable(selectedCandidate == null);

        confirmRow.getChildren().addAll(confirmLabel, conf_spacer, castBtn);
        confirmArea.getChildren().add(confirmRow);

        castBtn.setOnAction(e -> {
            if (selectedCandidate == null) return;
            // Confirmation dialog
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Your Vote");
            confirm.setHeaderText("You are voting for: " + selectedCandidate.getName());
            confirm.setContentText("Party: " + selectedCandidate.getParty() + "\n\nThis action cannot be undone. Are you sure?");
            confirm.showAndWait().ifPresent(res -> {
                if (res == ButtonType.OK) {
                    boolean success = VoteService.getInstance().castVote(selectedCandidate.getCandidateId());
                    if (success) {
                        if (currentUser != null) {
                            SmsService.send(currentUser.getMobile(), "Your vote has been cast. Thank you!");
                        }
                        showPanel(buildCastVotePanel());
                        setActiveNav(0);
                    } else {
                        Alert err = new Alert(Alert.AlertType.ERROR, "Vote casting failed. You may have already voted.");
                        err.show();
                    }
                }
            });
        });

        panel.getChildren().addAll(pageHeader, electionBanner, selectHeader, cardHolders[0], confirmArea);
        return panel;
    }

    // ================================================================
    // PANEL 2: RESULTS
    // ================================================================
    private VBox buildResultsPanel() {
        VBox panel = new VBox(25);
        panel.getStyleClass().add("content-area");

        Label title = new Label("Election Results");
        title.getStyleClass().add("page-title");

        VoteService vs = VoteService.getInstance();

        if (!vs.isElectionOpen()) {
            panel.getChildren().add(title);
            // Winner announcement if closed
            Candidate winner = vs.getWinner();
            if (winner != null && winner.getVoteCount() > 0) {
                HBox winnerCard = new HBox(20);
                winnerCard.getStyleClass().add("winner-card");
                winnerCard.setAlignment(Pos.CENTER_LEFT);
                Label wTrophy = new Label("🏆"); wTrophy.setStyle("-fx-font-size: 36px;");
                VBox wInfo = new VBox(4);
                Label wLabel = new Label("WINNER"); wLabel.getStyleClass().add("winner-label");
                Label wName = new Label(winner.getName()); wName.getStyleClass().add("winner-name");
                Label wVotes = new Label(winner.getVoteCount() + " votes · " + winner.getParty()); wVotes.getStyleClass().add("winner-label");
                wInfo.getChildren().addAll(wLabel, wName, wVotes);
                winnerCard.getChildren().addAll(wTrophy, wInfo);
                panel.getChildren().add(winnerCard);
            }
        } else {
            Label sub = new Label("Live results — updates in real time as votes are cast.");
            sub.getStyleClass().add("page-subtitle");
            panel.getChildren().addAll(title, sub);
        }

        // Pie chart
        VBox pieCard = new VBox(15);
        pieCard.getStyleClass().add("glass-card");
        Label pieTitle = new Label("Vote Distribution");
        pieTitle.getStyleClass().add("card-title");
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Candidate c : vs.getCandidates())
            pieData.add(new PieChart.Data(c.getName() + " (" + c.getVoteCount() + ")", Math.max(c.getVoteCount(), 1)));
        PieChart pie = new PieChart(pieData);
        pie.setLegendVisible(true);
        pie.setLabelsVisible(true);
        pie.setMinHeight(260);
        pieCard.getChildren().addAll(pieTitle, pie);

        // Leaderboard
        VBox lbCard = new VBox(15);
        lbCard.getStyleClass().add("glass-card");
        Label lbTitle = new Label("Standings");
        lbTitle.getStyleClass().add("card-title");
        lbCard.getChildren().add(lbTitle);

        int rank = 1; int total = vs.getTotalVotesCast();
        String[] lbColors = {"#ffc107", "#6c63ff", "#00d4ff"};
        String[] pbClasses = {"vote-progress-bar", "vote-progress-bar-cyan", "vote-progress-bar-teal"};
        for (Candidate c : vs.getCandidates()) {
            VBox row = new VBox(6);
            row.setPadding(new Insets(8, 0, 8, 0));
            HBox topRow = new HBox(12); topRow.setAlignment(Pos.CENTER_LEFT);
            Label rnk = new Label("#" + rank);
            rnk.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + lbColors[Math.min(rank-1,2)] + "; -fx-min-width: 35;");
            VBox info = new VBox(2); HBox.setHgrow(info, Priority.ALWAYS);
            Label cn = new Label(c.getName()); cn.getStyleClass().add("leaderboard-name");
            Label cp = new Label(c.getParty()); cp.getStyleClass().add("leaderboard-party");
            info.getChildren().addAll(cn, cp);
            Label vt = new Label(c.getVoteCount() + " votes"); vt.getStyleClass().add("leaderboard-votes");
            topRow.getChildren().addAll(rnk, info, vt);
            ProgressBar pb = new ProgressBar(total > 0 ? (double)c.getVoteCount()/total : 0);
            pb.setMaxWidth(Double.MAX_VALUE); pb.setPrefHeight(8);
            pb.getStyleClass().add(pbClasses[Math.min(rank-1, 2)]);
            row.getChildren().addAll(topRow, pb);
            lbCard.getChildren().add(row);
            rank++;
        }

        HBox chartsRow = new HBox(20);
        HBox.setHgrow(pieCard, Priority.ALWAYS);
        chartsRow.getChildren().addAll(pieCard, lbCard);
        lbCard.setPrefWidth(310);
        panel.getChildren().add(chartsRow);
        return panel;
    }

    // ================================================================
    // PANEL 3: MY PROFILE
    // ================================================================
    private VBox buildMyProfilePanel() {
        VBox panel = new VBox(25);
        panel.getStyleClass().add("content-area");

        Label title = new Label("My Profile");
        title.getStyleClass().add("page-title");

        VBox profileCard = new VBox(20);
        profileCard.getStyleClass().add("glass-card");
        profileCard.setMaxWidth(520);

        // Avatar
        StackPane ava = new StackPane();
        ava.setStyle("-fx-background-color: rgba(0,229,160,0.15); -fx-background-radius: 50; "
                + "-fx-min-width: 80; -fx-min-height: 80; -fx-max-width: 80; -fx-max-height: 80; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,229,160,0.3), 20, 0, 0, 0);");
        Label avaIcon = new Label("🗳️"); avaIcon.setStyle("-fx-font-size: 36px;");
        ava.getChildren().add(avaIcon);

        String name = currentUser != null ? currentUser.getName() : "Voter";
        String voterId = currentUser != null ? currentUser.getVoterId() : "-";
        boolean hasVoted = currentUser != null && currentUser.hasVoted();

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #e8eaf6;");
        Label idLabel = new Label("Voter ID: " + voterId);
        idLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8892b0; -fx-font-family: 'Courier New';");

        // Voted status badge
        HBox statusRow = new HBox(10); statusRow.setAlignment(Pos.CENTER_LEFT);
        Label votedBadge = new Label(hasVoted ? "✓  Vote Cast" : "○  Not Voted Yet");
        votedBadge.getStyleClass().add(hasVoted ? "badge-voted" : "badge-not-voted");
        Label elecBadge = new Label(VoteService.getInstance().isElectionOpen() ? "● Election Open" : "● Election Closed");
        elecBadge.getStyleClass().add(VoteService.getInstance().isElectionOpen() ? "badge-open" : "badge-closed");
        statusRow.getChildren().addAll(votedBadge, elecBadge);

        Region div = new Region(); div.getStyleClass().add("divider-line");
        Label detailsTitle = new Label("Voter Details"); detailsTitle.getStyleClass().add("card-title");

        VBox fields = new VBox(14);
        fields.getChildren().addAll(
            makeProfileField("Voter ID", voterId),
            makeProfileField("Full Name", name),
            makeProfileField("Email", currentUser != null ? currentUser.getEmail() : "-"),
            makeProfileField("Mobile", currentUser != null ? currentUser.getMobile() : "-"),
            makeProfileField("Voted", hasVoted ? "Yes — Vote confirmed" : "No — Not voted yet"),
            makeProfileField("Election", "General Election 2026")
        );

        profileCard.getChildren().addAll(ava, nameLabel, idLabel, statusRow, div, detailsTitle, fields);

        // Helpful tips card
        if (!hasVoted && VoteService.getInstance().isElectionOpen()) {
            VBox tipsCard = new VBox(12);
            tipsCard.getStyleClass().add("glass-card");
            tipsCard.setMaxWidth(520);
            tipsCard.setStyle(tipsCard.getStyle() + "-fx-border-color: rgba(0,229,160,0.2);");
            Label tipsTitle = new Label("💡 Ready to Vote?");
            tipsTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #00e5a0;");
            Label tipsSub = new Label("You haven't cast your vote yet. Head to the 'Cast Vote' section to participate.");
            tipsSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #8892b0; -fx-line-spacing: 4;");
            Button goVote = new Button("Go to Cast Vote →");
            goVote.getStyleClass().add("btn-primary-teal");
            goVote.setOnAction(e -> { setActiveNav(0); showPanel(buildCastVotePanel()); });
            tipsCard.getChildren().addAll(tipsTitle, tipsSub, goVote);
            panel.getChildren().addAll(title, profileCard, tipsCard);
        } else {
            panel.getChildren().addAll(title, profileCard);
        }

        return panel;
    }

    // ================================================================
    // HELPERS
    // ================================================================
    private HBox makeProfileField(String label, String value) {
        HBox row = new HBox(20); row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label); lbl.getStyleClass().add("profile-field-label"); lbl.setMinWidth(120);
        Label val = new Label(value); val.getStyleClass().add("profile-field-value");
        row.getChildren().addAll(lbl, val);
        return row;
    }

    public BorderPane getView() {
        return view;
    }
}