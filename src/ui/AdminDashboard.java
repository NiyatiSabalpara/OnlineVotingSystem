package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class AdminDashboard {

    private BorderPane view = new BorderPane();

    public AdminDashboard(Stage stage) {
        view.getStyleClass().add("admin-root");

        // --- Sidebar ---
        VBox sidebar = new VBox(15);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(250);
        
        Label brandLabel = new Label("Voting System.");
        brandLabel.getStyleClass().add("sidebar-brand");
        VBox.setMargin(brandLabel, new Insets(0, 0, 30, 10));

        Button dashboardBtn = createSidebarButton("Dashboard", true);
        Button partiesBtn = createSidebarButton("Parties", false);
        Button votersBtn = createSidebarButton("Voter Manager", false);
        Button candidatesBtn = createSidebarButton("Candidates", false);
        Button resultsBtn = createSidebarButton("Live Results", false);
        Button settingsBtn = createSidebarButton("Settings", false);
        Button logoutBtn = createSidebarButton("Logout", false);

        logoutBtn.setOnAction(e -> stage.getScene().setRoot(new LoginView(stage).getView()));
        resultsBtn.setOnAction(e -> stage.getScene().setRoot(new ResultWindow(stage).getView()));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(
            brandLabel, dashboardBtn, partiesBtn, votersBtn, candidatesBtn, resultsBtn, settingsBtn, 
            spacer, logoutBtn
        );

        // --- Main Content Area ---
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30, 40, 30, 40));

        Label pageTitle = new Label("Dashboard Overview");
        pageTitle.getStyleClass().add("admin-page-title");

        // KPI Cards Row
        HBox kpiRow = new HBox(20);
        kpiRow.getChildren().addAll(
            createKpiCard("Total Voters", "12,345", "~ 20% Than Last Month", true, "#b39ddb"),
            createKpiCard("Total Expenses", "$3,213", "~ 8% Than Last Month", true, "#90caf9"),
            createKpiCard("Votes Processed", "65,920", "~ 32% Than Last Month", true, "#81d4fa"),
            createKpiCard("Server Requests", "72,123", "~ 3% Than Last Month", false, "#a5d6a7")
        );

        // Charts Row
        HBox chartsRow = new HBox(20);
        
        // Bar Chart (Votes over time)
        VBox barChartCard = new VBox(10);
        barChartCard.getStyleClass().add("chart-card");
        HBox.setHgrow(barChartCard, Priority.ALWAYS);
        Label barChartTitle = new Label("Voting Trends & Engagement");
        barChartTitle.getStyleClass().add("chart-title");
        
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.getStyleClass().add("chart-axis");
        NumberAxis yAxis = new NumberAxis();
        yAxis.getStyleClass().add("chart-axis");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.getStyleClass().add("chart-plot-background");
        barChart.setLegendVisible(false);
        barChart.setMinHeight(300);
        
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.getData().add(new XYChart.Data<>("Feb", 4100));
        series1.getData().add(new XYChart.Data<>("Mar", 4900));
        series1.getData().add(new XYChart.Data<>("Apr", 2100));
        series1.getData().add(new XYChart.Data<>("May", 4100));
        series1.getData().add(new XYChart.Data<>("Jun", 1800));
        series1.getData().add(new XYChart.Data<>("Jul", 3100));
        barChart.getData().add(series1);
        barChartCard.getChildren().addAll(barChartTitle, barChart);

        // Pie Chart
        VBox pieChartCard = new VBox(10);
        pieChartCard.getStyleClass().add("chart-card");
        pieChartCard.setPrefWidth(350);
        Label pieChartTitle = new Label("Devices");
        pieChartTitle.getStyleClass().add("chart-title");
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("iOS", 40),
            new PieChart.Data("Mac", 35),
            new PieChart.Data("Windows", 25)
        );
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setLegendVisible(true);
        pieChart.setLegendSide(javafx.geometry.Side.BOTTOM);
        pieChart.getStyleClass().add("chart-plot-background");
        pieChartCard.getChildren().addAll(pieChartTitle, pieChart);

        chartsRow.getChildren().addAll(barChartCard, pieChartCard);

        mainContent.getChildren().addAll(pageTitle, kpiRow, chartsRow);
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        view.setLeft(sidebar);
        view.setCenter(scrollPane);
    }

    private Button createSidebarButton(String text, boolean isActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("sidebar-menu-btn");
        if (isActive) {
            btn.getStyleClass().add("sidebar-menu-btn-active");
        }
        return btn;
    }

    private VBox createKpiCard(String titleText, String valueText, String trendText, boolean isPositive, String iconColorHex) {
        VBox card = new VBox(15);
        card.getStyleClass().add("kpi-card");
        HBox.setHgrow(card, Priority.ALWAYS);

        // Icon placeholder
        StackPane iconPane = new StackPane();
        iconPane.getStyleClass().add("kpi-icon-container");
        iconPane.setStyle("-fx-background-color: " + iconColorHex + ";");
        Circle iconCircle = new Circle(8, Color.WHITE); 
        iconPane.getChildren().add(iconCircle);

        Label title = new Label(titleText);
        title.getStyleClass().add("kpi-title");

        Label value = new Label(valueText);
        value.getStyleClass().add("kpi-value");

        Label trend = new Label(trendText);
        trend.getStyleClass().add("kpi-trend");
        if (!isPositive) {
            trend.getStyleClass().add("kpi-trend-down");
        }

        card.getChildren().addAll(iconPane, title, value, trend);
        return card;
    }

    public BorderPane getView() {
        return view;
    }
}