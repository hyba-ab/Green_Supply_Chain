package analysis;

import entities.GreenSupplyChainData;
import java.util.List;

public class FeatureEngineeringAnalyzer {

    public void analyze(List<GreenSupplyChainData> data) {
        if (data.isEmpty()) {
            System.out.println("No data to analyze.");
            return;
        }

        System.out.println("=== Feature Engineering for Individual Companies ===");
        for (GreenSupplyChainData company : data) {
            System.out.println("Company ID: " + company.getCompanyID());
            System.out.println("  - Renewable Energy Ratio: " + company.getRenewableEnergyRatio());
            System.out.println("  - Non-Renewable Energy Ratio: " + company.getNonRenewableEnergyRatio());
            System.out.println("  - Carbon Intensity: " + company.getCarbonIntensity());
            
            System.out.println("---------------------------");
        }
    }
}