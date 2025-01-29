package analysis;

import entities.GreenSupplyChainData;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneralMetricsAnalyzer {

    // Method to analyze and display general metrics
    public void analyze(List<GreenSupplyChainData> data) {
        if (data.isEmpty()) {
            System.out.println("No data to analyze.");
            return;
        }

        // Calculate general metrics
        double totalCarbonEmissions = calculateTotalCarbonEmissions(data);
        double averageRenewableEnergy = calculateAverageRenewableEnergy(data);
        double averageNonRenewableEnergy = calculateAverageNonRenewableEnergy(data);
        double totalWasteProduced = calculateTotalWasteProduced(data);
        double averageWaterUsage = calculateAverageWaterUsage(data);
        Map<String, Long> transportModeDistribution = calculateTransportModeDistribution(data);

        // Display results
        System.out.println("=== General Metrics for All Companies ===");
        System.out.println("Total Carbon Emissions: " + totalCarbonEmissions);
        System.out.println("Average Renewable Energy Consumption: " + averageRenewableEnergy);
        System.out.println("Average Non-Renewable Energy Consumption: " + averageNonRenewableEnergy);
        System.out.println("Total Waste Produced: " + totalWasteProduced);
        System.out.println("Average Water Usage: " + averageWaterUsage);
        System.out.println("Transport Mode Distribution: " + transportModeDistribution);
    }

    // Method to calculate total carbon emissions
    public double calculateTotalCarbonEmissions(List<GreenSupplyChainData> data) {
        return data.stream()
                .mapToDouble(GreenSupplyChainData::getTotalCarbonEmissions)
                .sum();
    }

    // Method to calculate average renewable energy consumption
    public double calculateAverageRenewableEnergy(List<GreenSupplyChainData> data) {
        return data.stream()
                .mapToDouble(GreenSupplyChainData::getEnergyConsumptionRenewable)
                .average()
                .orElse(0.0);
    }

    // Method to calculate average non-renewable energy consumption
    public double calculateAverageNonRenewableEnergy(List<GreenSupplyChainData> data) {
        return data.stream()
                .mapToDouble(GreenSupplyChainData::getEnergyConsumptionNonRenewable)
                .average()
                .orElse(0.0);
    }

    // Method to calculate total waste produced
    public double calculateTotalWasteProduced(List<GreenSupplyChainData> data) {
        return data.stream()
                .mapToDouble(GreenSupplyChainData::getTotalWasteProduced)
                .sum();
    }

    // Method to calculate average water usage
    public double calculateAverageWaterUsage(List<GreenSupplyChainData> data) {
        return data.stream()
                .mapToDouble(GreenSupplyChainData::getWaterUsage)
                .average()
                .orElse(0.0);
    }

    // Method to calculate transport mode distribution
    public Map<String, Long> calculateTransportModeDistribution(List<GreenSupplyChainData> data) {
        return data.stream()
                .collect(Collectors.groupingBy(
                        record -> {
                            String transportMode = record.getTransportModes();
                            // Ensure transport mode is not null or empty
                            return (transportMode == null || transportMode.isEmpty()) ? "Unknown" : transportMode;
                        },
                        Collectors.counting()
                ));
    }
}