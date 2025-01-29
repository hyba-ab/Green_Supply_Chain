package analysis;

import entities.GreenSupplyChainData;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import java.util.List;

public class AnalysisManager {

    private ClassificationAnalysis classificationAnalysis;
    private RegressionAnalysis regressionAnalysis;
    private ClusteringAnalysis clusteringAnalysis;
    private GeneralMetricsAnalyzer generalMetricsAnalyzer; 
    private FeatureEngineeringAnalyzer featureEngineeringAnalyzer; 

    public AnalysisManager() {
        this.classificationAnalysis = new ClassificationAnalysis();
        this.regressionAnalysis = new RegressionAnalysis();
        this.clusteringAnalysis = new ClusteringAnalysis();
        this.generalMetricsAnalyzer = new GeneralMetricsAnalyzer(); 
        this.featureEngineeringAnalyzer = new FeatureEngineeringAnalyzer(); 
    }

    public void analyzeData(List<GreenSupplyChainData> data) {
        try {
            // Step 1: Perform general metrics analysis
            System.out.println("\n=== Performing General Metrics Analysis ===");
            generalMetricsAnalyzer.analyze(data);

            // Step 2: Perform feature engineering for individual companies
            System.out.println("\n=== Performing Feature Engineering ===");
            featureEngineeringAnalyzer.analyze(data);

            // Step 3: Train a classification model
            System.out.println("\n=== Training Classification Model ===");
            Classifier classifier = classificationAnalysis.trainClassificationModel(data); // No casting to RandomForest

            // Step 4: Classify a new supplier
            System.out.println("\n=== Classifying a New Supplier ===");
            // Example values for a new supplier
            double[] newSupplierFeatures = {
                1500.0, // energyRenewable
                100.0,  // energyNonRenewable
                50.0,   // totalWaste
                120.0,  // waterUsage
                80.0,   // recyclingRate
                7.5,    // energyEfficiency
                15000.0 // costOfGreenInitiatives
            };

            String supplierPerformance = classificationAnalysis.classifySupplier(classifier, newSupplierFeatures);
            System.out.println("Predicted Supplier Performance: " + supplierPerformance);

            // Step 5: Train a regression model
            System.out.println("\n=== Training Regression Model ===");
            LinearRegression regressionModel = regressionAnalysis.trainSupplierRegressionModel(data);

            // Step 6: Predict supplier performance for a new record
            System.out.println("\n=== Predicting Supplier Performance ===");
            // Example values for a new record
            double[] newSupplierFeaturesForPrediction = {
                500.0,  // energyRenewable
                300.0,  // energyNonRenewable
                100.0,  // totalWaste
                110.0,  // waterUsage
                75.0,   // recyclingRate
                8.0,    // energyEfficiency
                14000.0 // costOfGreenInitiatives
            };

            // Pass individual double values to the predictSupplierPerformance method
            double predictedPerformance = regressionAnalysis.predictSupplierPerformance(
                regressionModel, 
                newSupplierFeaturesForPrediction[0], // energyRenewable
                newSupplierFeaturesForPrediction[1], // energyNonRenewable
                newSupplierFeaturesForPrediction[2], // totalWaste
                newSupplierFeaturesForPrediction[3], // waterUsage
                newSupplierFeaturesForPrediction[4], // recyclingRate
                newSupplierFeaturesForPrediction[5], // energyEfficiency
                newSupplierFeaturesForPrediction[6]  // costOfGreenInitiatives
            );
            System.out.println("Predicted Supplier Performance: " + predictedPerformance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}