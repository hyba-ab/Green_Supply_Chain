package analysis;

import entities.GreenSupplyChainData;
import reports.ReportGenerator;
import java.util.List;
import java.util.stream.Collectors;

public class EnergyAnalysis extends SupplyChainAnalysis {

	public EnergyAnalysis(List<GreenSupplyChainData> data, ReportGenerator reportGenerator) {
        super(data, reportGenerator); // Pass data and reportGenerator to the parent class
    }

    @Override
    public void generateReport() {
        printReportHeader("Energy Analysis Report");
        double totalRenewableEnergy = calculateTotalRenewableEnergy();
        double totalNonRenewableEnergy = calculateTotalNonRenewableEnergy();
        List<GreenSupplyChainData> topRenewableUsers = identifyTopRenewableUsers(5);

        System.out.println("Total Renewable Energy: " + totalRenewableEnergy);
        System.out.println("Total Non-Renewable Energy: " + totalNonRenewableEnergy);
        System.out.println("Top 5 Renewable Energy Users: " + topRenewableUsers);
    }

    public double calculateTotalRenewableEnergy() {
        return data.stream()
                .mapToDouble(GreenSupplyChainData::getEnergyConsumptionRenewable)
                .sum();
    }

    public double calculateTotalNonRenewableEnergy() {
        return data.stream()
                .mapToDouble(GreenSupplyChainData::getEnergyConsumptionNonRenewable)
                .sum();
    }

    public List<GreenSupplyChainData> identifyTopRenewableUsers(int topN) {
        return data.stream()
                .sorted((d1, d2) -> Double.compare(d2.getEnergyConsumptionRenewable(), d1.getEnergyConsumptionRenewable()))
                .limit(topN)
                .collect(Collectors.toList());
    }
}