package reports;

import analysis.*;
import entities.GreenSupplyChainData;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.Node;

import java.util.List;
import java.util.Map;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.PrincipalComponents;

public class VisualizationManager {

    private List<GreenSupplyChainData> data;

    public VisualizationManager(List<GreenSupplyChainData> data) {
        this.data = data;
    }

    // Method to create a radar chart for multiple metrics
    public VBox createRadarChart() {
        // Create a VBox to hold the radar chart and the explanation
        VBox vbox = new VBox(20); // Spacing between chart and explanation
        vbox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Example data for the radar chart
        List<String> metrics = List.of("Emissions", "Renewable Energy", "Non-Renewable", "Waste");
        List<Double> values = List.of(50.0, 80.0, 30.0, 60.0); // Example values
        double maxValue = 100.0; // Maximum value for scaling

        // Create the radar chart
        RadarChart radarChart = new RadarChart(metrics, values, maxValue);
        radarChart.setPrefSize(800, 800); // Increased size for better visibility

        // Customize the radar chart appearance
        radarChart.setAxisLabelFill(Color.DARKBLUE); // Set axis label color
        radarChart.setAxisLineColor(Color.DARKGRAY); // Set axis line color
        radarChart.setAxisLineWidth(2.0); // Set axis line width

        // Explanation label below the radar chart
        Label explanationLabel = new Label(
            "Radar Chart Explanation:\n" +
            "This chart visualizes the performance of a company across multiple metrics.\n" +
            "Each axis represents a different metric (e.g., Emissions, Renewable Energy).\n" +
            "The values are scaled from 0 to 100, where 100 represents the best performance.\n" +
            "A larger area indicates better overall performance across all metrics."
        );
        explanationLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #333; " +
            "-fx-wrap-text: true; " +
            "-fx-font-weight: bold;" // Make the text bold
        );
        explanationLabel.setMaxWidth(800); // Match the width of the radar chart
        explanationLabel.setAlignment(javafx.geometry.Pos.CENTER); // Center-align the text

        // Add the radar chart and explanation label to the VBox
        vbox.getChildren().addAll(radarChart, explanationLabel);

        return vbox;
    }
    // Method to create an enhanced bar chart for renewable vs non-renewable energy consumption
    public BarChart<String, Number> createEnergyConsumptionChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Energy Type");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Energy Consumption");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Renewable vs Non-Renewable Energy Consumption");
        barChart.setLegendVisible(true);
        barChart.setStyle("-fx-font-size: 14px;");

        XYChart.Series<String, Number> renewableSeries = new XYChart.Series<>();
        renewableSeries.setName("Renewable Energy");
        XYChart.Series<String, Number> nonRenewableSeries = new XYChart.Series<>();
        nonRenewableSeries.setName("Non-Renewable Energy");

        EnergyAnalysis energyAnalysis = new EnergyAnalysis(data, null);
        double totalRenewable = energyAnalysis.calculateTotalRenewableEnergy();
        double totalNonRenewable = energyAnalysis.calculateTotalNonRenewableEnergy();

        renewableSeries.getData().add(new XYChart.Data<>("Renewable", totalRenewable));
        nonRenewableSeries.getData().add(new XYChart.Data<>("Non-Renewable", totalNonRenewable));

        barChart.getData().addAll(renewableSeries, nonRenewableSeries);

        // Add hover effects and tooltips
        for (XYChart.Series<String, Number> series : barChart.getData()) {
            for (XYChart.Data<String, Number> dataPoint : series.getData()) {
                Node node = dataPoint.getNode();
                node.setOnMouseEntered(event -> {
                    node.setEffect(new Glow(0.8));
                    Tooltip.install(node, new Tooltip(String.format("%s: %.2f", dataPoint.getXValue(), dataPoint.getYValue())));
                });
                node.setOnMouseExited(event -> node.setEffect(null));
            }
        }

        return barChart;
    }

    // Method to create an enhanced bar chart for top 5 polluters
    public BarChart<String, Number> createTopPollutersChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Company ID");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Carbon Emissions");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top 5 Polluters");
        barChart.setLegendVisible(true);
        barChart.setStyle("-fx-font-size: 14px;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Top Polluters");

        CarbonAnalysis carbonAnalysis = new CarbonAnalysis(data, null);
        List<GreenSupplyChainData> topPolluters = carbonAnalysis.identifyTopPolluters(5);

        for (GreenSupplyChainData polluter : topPolluters) {
            series.getData().add(new XYChart.Data<>("Company " + polluter.getCompanyID(), polluter.getTotalCarbonEmissions()));
        }

        barChart.getData().add(series);

        // Add hover effects and tooltips
        for (XYChart.Data<String, Number> dataPoint : series.getData()) {
            Node node = dataPoint.getNode();
            node.setOnMouseEntered(event -> {
                node.setEffect(new Glow(0.8));
                Tooltip.install(node, new Tooltip(String.format("%s: %.2f", dataPoint.getXValue(), dataPoint.getYValue())));
            });
            node.setOnMouseExited(event -> node.setEffect(null));
        }

        return barChart;
    }

    // Method to create an enhanced bar chart for top 5 renewable energy users
    public BarChart<String, Number> createTopRenewableEnergyUsersChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Company ID");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Renewable Energy Consumption");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top 5 Renewable Energy Users");
        barChart.setLegendVisible(true);
        barChart.setStyle("-fx-font-size: 14px;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Top Renewable Users");

        EnergyAnalysis energyAnalysis = new EnergyAnalysis(data, null);
        List<GreenSupplyChainData> topRenewableUsers = energyAnalysis.identifyTopRenewableUsers(5);

        for (GreenSupplyChainData user : topRenewableUsers) {
            series.getData().add(new XYChart.Data<>("Company " + user.getCompanyID(), user.getEnergyConsumptionRenewable()));
        }

        barChart.getData().add(series);

        // Add hover effects and tooltips
        for (XYChart.Data<String, Number> dataPoint : series.getData()) {
            Node node = dataPoint.getNode();
            node.setOnMouseEntered(event -> {
                node.setEffect(new Glow(0.8));
                Tooltip.install(node, new Tooltip(String.format("%s: %.2f", dataPoint.getXValue(), dataPoint.getYValue())));
            });
            node.setOnMouseExited(event -> node.setEffect(null));
        }

        return barChart;
    }

    // Method to create a scatter plot for clustering results
    public ScatterChart<Number, Number> createClusteringChart(SimpleKMeans kMeans) throws Exception {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Principal Component 1");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Principal Component 2");
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle("Clustering Results");

        // Perform PCA to reduce data to 2 dimensions
        ClusteringAnalysis clusteringAnalysis = new ClusteringAnalysis();
        Instances dataset = clusteringAnalysis.createDataset(data); // Now accessible
        Instances normalizedDataset = clusteringAnalysis.normalizeDataset(dataset); // Now accessible

        PrincipalComponents pca = new PrincipalComponents();
        pca.setInputFormat(normalizedDataset);
        pca.setMaximumAttributes(2); // Reduce to 2 dimensions
        Instances pcaDataset = Filter.useFilter(normalizedDataset, pca); // Now accessible

        // Create a series for each cluster
        int numClusters = kMeans.getNumClusters();
        for (int i = 0; i < numClusters; i++) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Cluster " + i);

            // Add points to the series
            for (int j = 0; j < pcaDataset.size(); j++) {
                int cluster = kMeans.clusterInstance(normalizedDataset.get(j));
                if (cluster == i) {
                    double x = pcaDataset.get(j).value(0); // First principal component
                    double y = pcaDataset.get(j).value(1); // Second principal component
                    series.getData().add(new XYChart.Data<>(x, y));
                }
            }

            scatterChart.getData().add(series);
        }

        scatterChart.setLegendVisible(true);
        scatterChart.setStyle("-fx-font-size: 14px;");

        return scatterChart;
    }


    public void openVisualizations() {
        Stage visualizationStage = new Stage();
        visualizationStage.setTitle("Green Supply Chain Visualizations");

        // Create a tab pane for better organization
        TabPane tabPane = new TabPane();

        // Energy Consumption Tab (unchanged)
        Tab energyConsumptionTab = new Tab("Energy Consumption");
        VBox energyConsumptionBox = new VBox(20);
        energyConsumptionBox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        BarChart<String, Number> energyConsumptionChart = createEnergyConsumptionChart();
        energyConsumptionChart.setPrefSize(800, 600);
        energyConsumptionBox.getChildren().add(energyConsumptionChart);

        energyConsumptionTab.setContent(energyConsumptionBox);

        

        // Clustering Chart Tab (unchanged)
        Tab clusteringChartTab = new Tab("Clustering Results");
        VBox clusteringChartBox = new VBox(20);
        clusteringChartBox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        SimpleKMeans kMeans = new SimpleKMeans();
        try {
            kMeans.setNumClusters(3);
            ClusteringAnalysis clusteringAnalysis = new ClusteringAnalysis();
            Instances dataset = clusteringAnalysis.createDataset(data);
            Instances normalizedDataset = clusteringAnalysis.normalizeDataset(dataset);
            kMeans.buildClusterer(normalizedDataset);

            ScatterChart<Number, Number> clusteringChart = createClusteringChart(kMeans);
            clusteringChart.setPrefSize(800, 600);
            clusteringChartBox.getChildren().add(clusteringChart);
        } catch (Exception e) {
            e.printStackTrace();
        }

        clusteringChartTab.setContent(clusteringChartBox);

        // Top Polluters Tab (unchanged)
        Tab topPollutersTab = new Tab("Top Polluters");
        VBox topPollutersBox = new VBox(20);
        topPollutersBox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        BarChart<String, Number> topPollutersChart = createTopPollutersChart();
        topPollutersChart.setPrefSize(800, 600);
        topPollutersBox.getChildren().add(topPollutersChart);

        topPollutersTab.setContent(topPollutersBox);

        // Top Renewable Energy Users Tab (unchanged)
        Tab topRenewableEnergyUsersTab = new Tab("Top Renewable Energy Users");
        VBox topRenewableEnergyUsersBox = new VBox(20);
        topRenewableEnergyUsersBox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        BarChart<String, Number> topRenewableEnergyUsersChart = createTopRenewableEnergyUsersChart();
        topRenewableEnergyUsersChart.setPrefSize(800, 600);
        topRenewableEnergyUsersBox.getChildren().add(topRenewableEnergyUsersChart);

        topRenewableEnergyUsersTab.setContent(topRenewableEnergyUsersBox);

        
     // Radar Chart Tab (enhanced)
        Tab radarChartTab = new Tab("Radar Chart");
        VBox radarChartBox = createRadarChart(); // Use the enhanced method
        radarChartTab.setContent(radarChartBox);
 
        // Add tabs to the tab pane
        tabPane.getTabs().addAll(energyConsumptionTab, radarChartTab, clusteringChartTab, topPollutersTab, topRenewableEnergyUsersTab);

        // Create scene and show stage
        Scene scene = new Scene(tabPane, 1200, 800);
        visualizationStage.setScene(scene);
        visualizationStage.show();
    }}