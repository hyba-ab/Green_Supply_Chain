package reports;

import entities.GreenSupplyChainData;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class DataHandler {

    // Method to convert List<GreenSupplyChainData> to Weka Instances
    public static Instances convertToWekaInstances(List<GreenSupplyChainData> data) {
        // Define attributes for the dataset
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("TotalCarbonEmissions"));
        attributes.add(new Attribute("EnergyConsumptionRenewable"));
        attributes.add(new Attribute("EnergyConsumptionNonRenewable"));
        attributes.add(new Attribute("WasteProduced"));

        // Create an Instances object with the defined attributes
        Instances instances = new Instances("GreenSupplyChainData", attributes, data.size());

        // Add data to the Instances object
        for (GreenSupplyChainData entry : data) {
            double[] values = new double[instances.numAttributes()];
            values[0] = entry.getTotalCarbonEmissions();
            values[1] = entry.getEnergyConsumptionRenewable();
            values[2] = entry.getEnergyConsumptionNonRenewable();
            values[3] = entry.getTotalWasteProduced(); // Use the correct method name
            instances.add(new DenseInstance(1.0, values));
        }

        return instances;
    }
}