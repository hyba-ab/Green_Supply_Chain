package analysis;

import entities.GreenSupplyChainData;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class RegressionAnalysis {

    private Instances dataset; // Store the dataset as a class variable

    public LinearRegression trainSupplierRegressionModel(List<GreenSupplyChainData> data) throws Exception {
        // Define attributes
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Energy_Consumption_Renewable"));
        attributes.add(new Attribute("Energy_Consumption_NonRenewable"));
        attributes.add(new Attribute("Total_Waste_Produced"));
        attributes.add(new Attribute("Water_Usage"));
        attributes.add(new Attribute("Recycling_Rate"));
        attributes.add(new Attribute("Energy_Efficiency"));
        attributes.add(new Attribute("Cost_of_Green_Initiatives"));

        // Define target attribute (for regression)
        attributes.add(new Attribute("Supplier_Performance")); // Continuous target variable

        // Create dataset
        this.dataset = new Instances("SupplierData", attributes, 0);
        this.dataset.setClassIndex(this.dataset.numAttributes() - 1); // Set the target attribute index

        // Add instances
        for (GreenSupplyChainData record : data) {
            double[] instanceValues = new double[8]; // 7 features + 1 target attribute
            instanceValues[0] = record.getEnergyConsumptionRenewable();
            instanceValues[1] = record.getEnergyConsumptionNonRenewable();
            instanceValues[2] = record.getTotalWasteProduced();
            instanceValues[3] = record.getWaterUsage();
            instanceValues[4] = record.getRecyclingRate();
            instanceValues[5] = record.getEnergyEfficiency();
            instanceValues[6] = record.getCostOfGreenInitiatives();
            instanceValues[7] = record.getSupplierPerformance(); // Target attribute

            dataset.add(new DenseInstance(1.0, instanceValues));
        }

        // Train Linear Regression model
        LinearRegression linearRegression = new LinearRegression();
        linearRegression.buildClassifier(this.dataset); // Train the model

        return linearRegression;
    }

    public Instances getDataset() {
        return this.dataset; // Return the dataset
    }

    public double predictSupplierPerformance(LinearRegression model, double energyRenewable, double energyNonRenewable,
                                            double totalWaste, double waterUsage, double recyclingRate, double energyEfficiency,
                                            double costOfGreenInitiatives) throws Exception {
        // Create a test instance
        double[] instanceValues = new double[8]; // 7 features + 1 target attribute
        instanceValues[0] = energyRenewable;
        instanceValues[1] = energyNonRenewable;
        instanceValues[2] = totalWaste;
        instanceValues[3] = waterUsage;
        instanceValues[4] = recyclingRate;
        instanceValues[5] = energyEfficiency;
        instanceValues[6] = costOfGreenInitiatives;
        instanceValues[7] = Double.NaN; // Unknown target value

        // Create dataset for the test instance
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Energy_Consumption_Renewable"));
        attributes.add(new Attribute("Energy_Consumption_NonRenewable"));
        attributes.add(new Attribute("Total_Waste_Produced"));
        attributes.add(new Attribute("Water_Usage"));
        attributes.add(new Attribute("Recycling_Rate"));
        attributes.add(new Attribute("Energy_Efficiency"));
        attributes.add(new Attribute("Cost_of_Green_Initiatives"));
        attributes.add(new Attribute("Supplier_Performance")); // Target attribute

        Instances dataset = new Instances("SupplierData", attributes, 0);
        dataset.setClassIndex(dataset.numAttributes() - 1);

        DenseInstance testInstance = new DenseInstance(1.0, instanceValues);
        testInstance.setDataset(dataset);

        // Predict supplier performance
        return model.classifyInstance(testInstance);
    }
}