package main;

import analysis.AnalysisManager;
import analysis.CarbonAnalysis;
import analysis.ClusteringAnalysis;
import analysis.EnergyAnalysis;
import data.DataManager;
import entities.GreenSupplyChainData;
import entities.Company;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import reports.ReportGenerator;
import reports.VisualizationManager;
import weka.clusterers.SimpleKMeans;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main extends Application {

    private List<GreenSupplyChainData> cleanedData; // Store cleaned data for use in JavaFX

    public static void main(String[] args) {
        // Launch JavaFX application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Step 1: Create a simple JavaFX UI
        primaryStage.setTitle("Green Supply Chain Analysis");

        // Button to run the command-line application
        Button runButton = new Button("Run Data Analysis");
        runButton.setOnAction(event -> {
            runCommandLineApp();
            System.out.println("Data analysis completed. You can now view visualizations.");
        });

        // Button to sort companies by environmental impact
        Button sortButton = new Button("Sort Companies by Environmental Impact");
        sortButton.setOnAction(event -> {
            if (cleanedData != null) {
                sortCompaniesByEnvironmentalImpact(cleanedData);
            } else {
                System.out.println("Please run data analysis first.");
            }
        });

        // Button to open visualizations
        Button visualizeButton = new Button("Open Visualizations");
        visualizeButton.setOnAction(event -> {
            if (cleanedData != null) {
                VisualizationManager visualizationManager = new VisualizationManager(cleanedData);
                visualizationManager.openVisualizations();
            } else {
                System.out.println("Please run data analysis first.");
            }
        });

        // Layout
        VBox vbox = new VBox(10, runButton, sortButton, visualizeButton);
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to run the command-line application
    private void runCommandLineApp() {
        // Step 1: Initialize DataManager and Scanner
        DataManager dataManager = new DataManager();
        Scanner scanner = new Scanner(System.in);

        // Step 2: Ask the user for the data source type
        System.out.println("Enter the data source type (CSV, Excel, or Database):");
        String sourceType = scanner.nextLine().trim().toLowerCase();

        // Step 3: Import data based on the source type
        List<GreenSupplyChainData> data = importData(dataManager, scanner, sourceType);
        if (data == null || data.isEmpty()) {
            System.err.println("No data was imported. Please check the data source and try again.");
            scanner.close();
            return; // Exit early if no data was imported
        }

        // Step 4: Clean and store the data
        System.out.println("Data imported successfully. Starting data cleaning...");
        cleanedData = dataManager.cleanData(data); // Store cleaned data for use in JavaFX
        System.out.println("Data cleaning completed. Storing cleaned data...");
        dataManager.storeData(cleanedData);

        // Step 5: Print the first 5 records to verify
        System.out.println("First 5 cleaned records:");
        for (int i = 0; i < 5 && i < cleanedData.size(); i++) {
            System.out.println(cleanedData.get(i));
        }

        // Step 6: Perform data analysis using AnalysisManager
        performDataAnalysis(cleanedData);

        // Step 7: Perform clustering and display interpretations
        performClustering(cleanedData);

        // Step 8: Generate reports
        generateReports(cleanedData);

        // Step 9: Close the scanner
        scanner.close();
    }

    // Helper method to import data based on the source type
    private List<GreenSupplyChainData> importData(DataManager dataManager, Scanner scanner, String sourceType) {
        switch (sourceType) {
            case "csv":
                System.out.println("Enter the path to the CSV file:");
                String csvFilePath = scanner.nextLine().trim();
                return dataManager.importData("csv", csvFilePath, null, null, null);
            case "excel":
                System.out.println("Enter the path to the Excel file:");
                String excelFilePath = scanner.nextLine().trim();
                return dataManager.importData("excel", excelFilePath, null, null, null);
            case "database":
                System.out.println("Enter the database URL:");
                String url = scanner.nextLine().trim();
                System.out.println("Enter the database username:");
                String username = scanner.nextLine().trim();
                System.out.println("Enter the database password:");
                String password = scanner.nextLine().trim();
                System.out.println("Enter the SQL query:");
                String query = scanner.nextLine().trim();
                return dataManager.importData("database", url, username, password, query);
            default:
                System.err.println("Unsupported data source type: " + sourceType);
                return null; // Return null for unsupported data source types
        }
    }

    // Helper method to perform data analysis
    private void performDataAnalysis(List<GreenSupplyChainData> cleanedData) {
        System.out.println("\nPerforming data analysis...");
        AnalysisManager analysisManager = new AnalysisManager();
        analysisManager.analyzeData(cleanedData); // Perform general metrics and feature engineering
    }

    // Helper method to perform clustering and display interpretations
    private void performClustering(List<GreenSupplyChainData> cleanedData) {
        System.out.println("\nPerforming clustering...");
        try {
            ClusteringAnalysis clusteringAnalysis = new ClusteringAnalysis();
            SimpleKMeans kMeans = clusteringAnalysis.clusterSuppliers(cleanedData, 3); // 3 clusters

            // Get cluster assignments
            int[] assignments = clusteringAnalysis.getClusterAssignments(kMeans, cleanedData);
            System.out.println("\nCluster Assignments:");
            for (int i = 0; i < assignments.length; i++) {
                System.out.println("Data Point " + i + " -> Cluster " + assignments[i]);
            }

            // Get cluster interpretations
            Map<Integer, String> interpretations = clusteringAnalysis.calculateClusterInterpretations(kMeans, cleanedData);
            System.out.println("\nCluster Interpretations:");
            for (Map.Entry<Integer, String> entry : interpretations.entrySet()) {
                System.out.println("Cluster " + entry.getKey() + ": " + entry.getValue());
            }
        } catch (Exception e) {
            System.err.println("Error during clustering: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to generate reports
    private void generateReports(List<GreenSupplyChainData> cleanedData) {
        ReportGenerator reportGenerator = new ReportGenerator(); // Instantiate ReportGenerator
        CarbonAnalysis carbonAnalysis = new CarbonAnalysis(cleanedData, reportGenerator);
        EnergyAnalysis energyAnalysis = new EnergyAnalysis(cleanedData, reportGenerator);

        System.out.println("\nGenerating Reports...");
        carbonAnalysis.generateReport();
        energyAnalysis.generateReport();
    }

    // method to sort companies by environmental impact
    private void sortCompaniesByEnvironmentalImpact(List<GreenSupplyChainData> cleanedData) {
        // Step 1: Create a list to store companies and their environmental impact
        List<CompanyImpact> companyImpacts = new ArrayList<>();

        // Step 2: Calculate environmental impact for each company
        for (GreenSupplyChainData data : cleanedData) {
            // Create a Company object using the relevant fields
            Company company = new Company(
                    String.valueOf(data.getCompanyID()), // Entity ID
                    "Company " + data.getCompanyID(),   // Name (placeholder)
                    "Location " + data.getCompanyID(),  // Location (placeholder)
                    data.getTotalCarbonEmissions(),
                    data.getEnergyConsumptionRenewable(),
                    data.getEnergyConsumptionNonRenewable(),
                    data.getTotalWasteProduced(),
                    data.getWaterUsage(),
                    data.getRecyclingRate(),
                    data.getGreenCertifications(), // Binary: 1 if the company has certifications, 0 otherwise
                    data.getEnvironmentalCompliance(), // Binary: 1 if compliant, 0 otherwise
                    data.getEnergyEfficiency(),
                    data.getCostOfGreenInitiatives()
            );

            // Calculate environmental impact
            double environmentalImpact = company.calculateEnvironmentalImpact();

            // Store the company and its impact in the list
            companyImpacts.add(new CompanyImpact(company, environmentalImpact));
        }

        // Step 3: Sort the companies by environmental impact (ascending order)
        companyImpacts.sort(Comparator.comparingDouble(CompanyImpact::getEnvironmentalImpact));

        // Step 4: Display the sorted list
        System.out.println("\nCompanies sorted by Environmental Impact (Ascending Order):");
        for (CompanyImpact companyImpact : companyImpacts) {
            System.out.println("Company ID: " + companyImpact.getCompany().getEntityID() +
                    ", Environmental Impact: " + companyImpact.getEnvironmentalImpact());
        }
    }

    // Helper class to store a company and its environmental impact
    private static class CompanyImpact {
        private final Company company;
        private final double environmentalImpact;

        public CompanyImpact(Company company, double environmentalImpact) {
            this.company = company;
            this.environmentalImpact = environmentalImpact;
        }

        public Company getCompany() {
            return company;
        }

        public double getEnvironmentalImpact() {
            return environmentalImpact;
        }
    }
}