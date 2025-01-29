package analysis;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import entities.GreenSupplyChainData;

import java.util.ArrayList;
import java.util.List;

public class ClassificationAnalysis {

    private Instances createDataset(List<GreenSupplyChainData> data) throws Exception {
        // Define attributes
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("energyRenewable"));
        attributes.add(new Attribute("energyNonRenewable"));
        attributes.add(new Attribute("totalWaste"));
        attributes.add(new Attribute("renewableEnergyRatio"));
        attributes.add(new Attribute("wasteToEmissionsRatio"));

        // Define the class attribute as a categorical (nominal) attribute
        ArrayList<String> classValues = new ArrayList<>();
        classValues.add("Low");
        classValues.add("Medium");
        classValues.add("High");
        attributes.add(new Attribute("sustainabilityScore", classValues));

        // Create dataset
        Instances dataset = new Instances("SupplierData", attributes, 0);

        // Add instances
        for (GreenSupplyChainData record : data) {
            double[] instanceValues = new double[6]; // 5 features + 1 class attribute
            instanceValues[0] = record.getEnergyConsumptionRenewable();
            instanceValues[1] = record.getEnergyConsumptionNonRenewable();
            instanceValues[2] = record.getTotalWasteProduced();
            instanceValues[3] = record.getRenewableEnergyRatio();

            // Handle invalid wasteToEmissionsRatio values
            double wasteToEmissionsRatio = record.getWasteToEmissionsRatio();
            if (Double.isNaN(wasteToEmissionsRatio) || Double.isInfinite(wasteToEmissionsRatio)) {
                wasteToEmissionsRatio = 0.0; // Default value for invalid cases
            }
            instanceValues[4] = wasteToEmissionsRatio;

            // Convert numeric sustainabilityScore to categorical
            double sustainabilityScore;
            try {
                // Convert the String to double
                sustainabilityScore = Double.parseDouble(record.getSustainabilityScore());
            } catch (NumberFormatException e) {
                // If the String is not a valid number, use a default value (e.g., 0.0)
                sustainabilityScore = 0.0; // Default value for invalid cases
            }

            // Handle NaN or infinite values
            if (Double.isNaN(sustainabilityScore) || Double.isInfinite(sustainabilityScore)) {
                sustainabilityScore = 0.0; // Default value for invalid cases
            }

            // Bin the sustainabilityScore into categories
            String category;
            if (sustainabilityScore < 50) {
                category = "Low";
            } else if (sustainabilityScore <= 80) {
                category = "Medium";
            } else {
                category = "High";
            }
            instanceValues[5] = classValues.indexOf(category); // Set the categorical value

            dataset.add(new DenseInstance(1.0, instanceValues));
        }

        // Set the class index (last attribute)
        dataset.setClassIndex(dataset.numAttributes() - 1);

        return dataset;
    }

    private Instances preprocessDataset(Instances dataset) throws Exception {
        // Handle missing values
        ReplaceMissingValues replaceMissingValues = new ReplaceMissingValues();
        replaceMissingValues.setInputFormat(dataset);
        dataset = Filter.useFilter(dataset, replaceMissingValues);

        // Normalize the dataset
        Normalize normalizeFilter = new Normalize();
        normalizeFilter.setInputFormat(dataset);
        dataset = Filter.useFilter(dataset, normalizeFilter);

        return dataset;
    }

    public Classifier trainClassificationModel(List<GreenSupplyChainData> data) throws Exception {
        // Create and preprocess the dataset
        Instances dataset = createDataset(data);
        Instances preprocessedDataset = preprocessDataset(dataset);

        // Train a J48 decision tree classifier
        Classifier classifier = new J48();
        classifier.buildClassifier(preprocessedDataset);

        return classifier;
    }

    public String classifySupplier(Classifier classifier, double[] features) throws Exception {
        // Create a new instance with the provided features
        Instances dataset = createDataset(new ArrayList<>()); // Create an empty dataset to get the structure
        DenseInstance newInstance = new DenseInstance(1.0, features);
        dataset.add(newInstance);
        dataset.setClassIndex(dataset.numAttributes() - 1);

        // Classify the new instance
        double prediction = classifier.classifyInstance(dataset.firstInstance());

        // Map the predicted index to the corresponding category
        ArrayList<String> classValues = new ArrayList<>();
        classValues.add("Low");
        classValues.add("Medium");
        classValues.add("High");

        return classValues.get((int) prediction); // Return the predicted category
    }
}