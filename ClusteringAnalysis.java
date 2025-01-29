package analysis;

import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import entities.GreenSupplyChainData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusteringAnalysis {

    
     //Creates a dataset from the list of GreenSupplyChainData.
    
    public Instances createDataset(List<GreenSupplyChainData> data) throws Exception {
        // Define attributes
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("energyRenewable"));
        attributes.add(new Attribute("energyNonRenewable"));
        attributes.add(new Attribute("totalWaste"));
        attributes.add(new Attribute("renewableEnergyRatio"));
        attributes.add(new Attribute("wasteToEmissionsRatio"));
        attributes.add(new Attribute("totalCarbonEmissions")); 
        attributes.add(new Attribute("waterUsage")); 
        attributes.add(new Attribute("recyclingRate")); 
        attributes.add(new Attribute("greenCertifications")); 

        // Create dataset
        Instances dataset = new Instances("SupplierData", attributes, 0);

        // Add instances
        for (GreenSupplyChainData record : data) {
            double[] instanceValues = new double[9]; // 9 features
            instanceValues[0] = record.getEnergyConsumptionRenewable();
            instanceValues[1] = record.getEnergyConsumptionNonRenewable();
            instanceValues[2] = record.getTotalWasteProduced();
            instanceValues[3] = record.getRenewableEnergyRatio();

            // Handle invalid wasteToEmissionsRatio values
            double wasteToEmissionsRatio = record.getWasteToEmissionsRatio();
            if (Double.isNaN(wasteToEmissionsRatio) || Double.isInfinite(wasteToEmissionsRatio)) {
                wasteToEmissionsRatio = 0.0; 
            }
            instanceValues[4] = wasteToEmissionsRatio;

            instanceValues[5] = record.getTotalCarbonEmissions(); 
            instanceValues[6] = record.getWaterUsage(); 
            instanceValues[7] = record.getRecyclingRate(); 
            instanceValues[8] = record.getGreenCertifications(); 

            dataset.add(new DenseInstance(1.0, instanceValues));
        }

        return dataset;
    }

   
     //Normalizes the dataset.
     
    public Instances normalizeDataset(Instances dataset) throws Exception {
        // Apply normalization filter
        Normalize normalizeFilter = new Normalize();
        normalizeFilter.setInputFormat(dataset);
        return Filter.useFilter(dataset, normalizeFilter);
    }

    
     //Performs K-Means clustering on the data
     //@return The trained K-Means clusterer.
    
    public SimpleKMeans clusterSuppliers(List<GreenSupplyChainData> data, int k) throws Exception {
        // Create and normalize the dataset
        Instances dataset = createDataset(data);
        Instances normalizedDataset = normalizeDataset(dataset);

        // Configure and train K-Means
        SimpleKMeans kMeans = new SimpleKMeans();
        kMeans.setNumClusters(k); // Set the number of clusters
        kMeans.buildClusterer(normalizedDataset); // Train on the normalized dataset

        return kMeans;
    }

    
    //Gets the cluster assignments for each data point.
  
     //@return An array of cluster assignments.
    
    public int[] getClusterAssignments(SimpleKMeans kMeans, List<GreenSupplyChainData> data) throws Exception {
        // Create and normalize the dataset
        Instances dataset = createDataset(data);
        Instances normalizedDataset = normalizeDataset(dataset);

        // Get cluster assignments
        int[] assignments = new int[data.size()];
        for (int i = 0; i < data.size(); i++) {
            assignments[i] = kMeans.clusterInstance(normalizedDataset.get(i));
        }

        return assignments;
    }

   
     //Calculates the interpretation of each cluster based on its characteristics.
   
     //@return A map of cluster interpretations, where the key is the cluster number and the value is the interpretation.
     
    public Map<Integer, String> calculateClusterInterpretations(SimpleKMeans kMeans, List<GreenSupplyChainData> data) throws Exception {
        // Create and normalize the dataset
        Instances dataset = createDataset(data);
        Instances normalizedDataset = normalizeDataset(dataset);

        // Calculate cluster characteristics
        Map<Integer, Map<String, Double>> clusterCharacteristics = calculateClusterCharacteristics(kMeans, normalizedDataset);

        // Define interpretations based on cluster characteristics
        Map<Integer, String> interpretations = new HashMap<>();
        for (Map.Entry<Integer, Map<String, Double>> entry : clusterCharacteristics.entrySet()) {
            int cluster = entry.getKey();
            Map<String, Double> characteristics = entry.getValue();

            // Extract key attributes
            double carbonEmissions = characteristics.get("totalCarbonEmissions");
            double renewableEnergy = characteristics.get("energyRenewable");
            double waterUsage = characteristics.get("waterUsage");
            double recyclingRate = characteristics.get("recyclingRate");

            // Define interpretation based on attribute values
            StringBuilder interpretation = new StringBuilder();
            interpretation.append("Cluster ").append(cluster).append(": ");

            // Carbon emissions
            if (carbonEmissions > getClusterMean(clusterCharacteristics, "totalCarbonEmissions")) {
                interpretation.append("Higher carbon emissions, ");
            } else if (carbonEmissions < getClusterMean(clusterCharacteristics, "totalCarbonEmissions")) {
                interpretation.append("Lower carbon emissions, ");
            } else {
                interpretation.append("Average carbon emissions, ");
            }

            // Renewable energy
            if (renewableEnergy > getClusterMean(clusterCharacteristics, "energyRenewable")) {
                interpretation.append("higher renewable energy usage, ");
            } else if (renewableEnergy < getClusterMean(clusterCharacteristics, "energyRenewable")) {
                interpretation.append("lower renewable energy usage, ");
            } else {
                interpretation.append("average renewable energy usage, ");
            }

            // Water usage
            if (waterUsage > getClusterMean(clusterCharacteristics, "waterUsage")) {
                interpretation.append("higher water usage, ");
            } else if (waterUsage < getClusterMean(clusterCharacteristics, "waterUsage")) {
                interpretation.append("lower water usage, ");
            } else {
                interpretation.append("average water usage, ");
            }

            // Recycling rate
            if (recyclingRate > getClusterMean(clusterCharacteristics, "recyclingRate")) {
                interpretation.append("higher recycling rate.");
            } else if (recyclingRate < getClusterMean(clusterCharacteristics, "recyclingRate")) {
                interpretation.append("lower recycling rate.");
            } else {
                interpretation.append("average recycling rate.");
            }

            interpretations.put(cluster, interpretation.toString());
        }

        return interpretations;
    }

    
     //Helper method to calculate the mean value of an attribute across all clusters
     //@return The mean value of the attribute across all clusters.
    
    private double getClusterMean(Map<Integer, Map<String, Double>> clusterCharacteristics, String attributeName) {
        double sum = 0;
        int count = 0;

        for (Map.Entry<Integer, Map<String, Double>> entry : clusterCharacteristics.entrySet()) {
            sum += entry.getValue().get(attributeName);
            count++;
        }

        return sum / count;
    }

    /**
     * Calculates the mean values of key attributes for each cluster.
     * @param kMeans The trained K-Means clusterer.
     * @param dataset The normalized dataset used for clustering.
     * @return A map where the key is the cluster number and the value is a map of attribute names to their mean values.
     */
    private Map<Integer, Map<String, Double>> calculateClusterCharacteristics(SimpleKMeans kMeans, Instances dataset) throws Exception {
        Map<Integer, Map<String, Double>> clusterCharacteristics = new HashMap<>();
        int numClusters = kMeans.getNumClusters();
        int numAttributes = dataset.numAttributes();

        // Initialize maps for each cluster
        for (int i = 0; i < numClusters; i++) {
            clusterCharacteristics.put(i, new HashMap<>());
            for (int j = 0; j < numAttributes; j++) {
                clusterCharacteristics.get(i).put(dataset.attribute(j).name(), 0.0);
            }
        }

        // Sum attribute values for each cluster
        int[] clusterCounts = new int[numClusters];
        for (Instance instance : dataset) {
            int cluster = kMeans.clusterInstance(instance);
            clusterCounts[cluster]++;
            for (int j = 0; j < numAttributes; j++) {
                String attributeName = dataset.attribute(j).name();
                double value = instance.value(j);
                clusterCharacteristics.get(cluster).merge(attributeName, value, Double::sum);
            }
        }

        // Calculate mean values
        for (int i = 0; i < numClusters; i++) {
            for (Map.Entry<String, Double> entry : clusterCharacteristics.get(i).entrySet()) {
                entry.setValue(entry.getValue() / clusterCounts[i]);
            }
        }

        return clusterCharacteristics;
    }
}