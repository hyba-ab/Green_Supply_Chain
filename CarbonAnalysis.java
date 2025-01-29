package analysis;

import entities.GreenSupplyChainData;
import reports.ReportGenerator;
import java.util.List;
import java.util.stream.Collectors;

public class CarbonAnalysis extends SupplyChainAnalysis {

	public CarbonAnalysis(List<GreenSupplyChainData> data, ReportGenerator reportGenerator) {
        super(data, reportGenerator); // Pass data and reportGenerator to the parent class
    }

    

    @Override
    public void generateReport() {
        printReportHeader("Carbon Analysis Report");
        double totalEmissions = calculateTotalEmissions();
        double averageEmissions = calculateAverageEmissions();
        List<GreenSupplyChainData> topPolluters = identifyTopPolluters(5);

        System.out.println("Total Carbon Emissions: " + totalEmissions);
        System.out.println("Average Carbon Emissions: " + averageEmissions);
        System.out.println("Top 5 Polluters: " + topPolluters);
    }

    public double calculateAverageEmissions() {
        return data.stream()
                .mapToDouble(GreenSupplyChainData::getTotalCarbonEmissions)
                .average()
                .orElse(0.0);
    }

    public List<GreenSupplyChainData> identifyTopPolluters(int topN) {
        return data.stream()
                .sorted((d1, d2) -> Double.compare(d2.getTotalCarbonEmissions(), d1.getTotalCarbonEmissions()))
                .limit(topN)
                .collect(Collectors.toList());
    }
}