
package analysis;

import entities.GreenSupplyChainData;
import reports.ReportGenerator;
import java.util.List;

public abstract class SupplyChainAnalysis {
    protected List<GreenSupplyChainData> data;
    protected ReportGenerator reportGenerator;

    public SupplyChainAnalysis(List<GreenSupplyChainData> data, ReportGenerator reportGenerator) {
        this.data = data;
        this.reportGenerator = reportGenerator;
    }

    // Common method to calculate total emissions 
    public double calculateTotalEmissions() {
        return data.stream()
                .mapToDouble(GreenSupplyChainData::getTotalCarbonEmissions)
                .sum();
    }

    // Abstract method for generating a report (must be implemented by subclasses)
    public abstract void generateReport();

    // Common method to print a header for reports
    protected void printReportHeader(String reportName) {
        System.out.println("=== " + reportName + " ===");
        System.out.println("Data Size: " + data.size());
    }
}