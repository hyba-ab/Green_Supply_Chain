package reports;

import entities.GreenSupplyChainData;
import java.util.List;

public class ReportGenerator implements Reportable {

    @Override
    public void generateReport() {
        // This method can be used to generate a general report or call specific report methods.
        System.out.println("Generating General Report...");
        
    }

    public void generateCarbonReport(List<GreenSupplyChainData> data) {
        System.out.println("=== Carbon Report ===");
        double totalEmissions = data.stream()
                .mapToDouble(GreenSupplyChainData::getTotalCarbonEmissions)
                .sum();
        System.out.println("Total Carbon Emissions: " + totalEmissions);
    }

    public void generateEnergyReport(List<GreenSupplyChainData> data) {
        System.out.println("=== Energy Report ===");
        double totalRenewableEnergy = data.stream()
                .mapToDouble(GreenSupplyChainData::getEnergyConsumptionRenewable)
                .sum();
        double totalNonRenewableEnergy = data.stream()
                .mapToDouble(GreenSupplyChainData::getEnergyConsumptionNonRenewable)
                .sum();
        System.out.println("Total Renewable Energy: " + totalRenewableEnergy);
        System.out.println("Total Non-Renewable Energy: " + totalNonRenewableEnergy);
    }

    public void generateOtherMetricsReport(List<GreenSupplyChainData> data) {
        System.out.println("=== Other Metrics Report ===");
        double totalWaste = data.stream()
                .mapToDouble(GreenSupplyChainData::getTotalWasteProduced)
                .sum();
        double totalWaterUsage = data.stream()
                .mapToDouble(GreenSupplyChainData::getWaterUsage)
                .sum();
        System.out.println("Total Waste Produced: " + totalWaste);
        System.out.println("Total Water Usage: " + totalWaterUsage);
    }
}