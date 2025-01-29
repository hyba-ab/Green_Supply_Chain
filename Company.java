package entities;

import java.util.List;

public class Company extends SupplyChainEntity {

    // Core metrics
    public double totalCarbonEmissions;
    public double energyConsumptionRenewable;
    public double energyConsumptionNonRenewable;
    public double totalWasteProduced;
    public double waterUsage;
    public double recyclingRate;
    public int greenCertifications; // Binary: 1 if the company has certifications, 0 otherwise
    public int environmentalCompliance; // Binary: 1 if compliant, 0 otherwise
    public double energyEfficiency;
    public double costOfGreenInitiatives;

    // Weights for each metric
    private static final double CARBON_WEIGHT = 0.3;
    private static final double NON_RENEWABLE_WEIGHT = 0.2;
    private static final double RENEWABLE_WEIGHT = -0.15;
    private static final double WASTE_WEIGHT = 0.1;
    private static final double WATER_WEIGHT = 0.1;
    private static final double RECYCLING_WEIGHT = -0.1;
    private static final double CERTIFICATION_WEIGHT = -0.05;
    private static final double COMPLIANCE_WEIGHT = -0.05;
    private static final double EFFICIENCY_WEIGHT = -0.05;
    private static final double COST_WEIGHT = -0.05;

    // Max values for normalization (from the dataset)
    private static final double MAX_CARBON = 1794;
    private static final double MAX_RENEWABLE = 249.53;
    private static final double MAX_NON_RENEWABLE = 349.99;
    private static final double MAX_WASTE = 99.91;
    private static final double MAX_WATER = 149.94;
    private static final double MAX_RECYCLING = 84.95;
    private static final double MAX_EFFICIENCY = 9.92;
    private static final double MAX_COST = 19938.48;

    public Company(String entityID, String name, String location, 
                   double totalCarbonEmissions, double energyConsumptionRenewable, 
                   double energyConsumptionNonRenewable, double totalWasteProduced, 
                   double waterUsage, double recyclingRate, int greenCertifications, 
                   int environmentalCompliance, double energyEfficiency, 
                   double costOfGreenInitiatives) {
        super(entityID, name, location);
        this.totalCarbonEmissions = totalCarbonEmissions;
        this.energyConsumptionRenewable = energyConsumptionRenewable;
        this.energyConsumptionNonRenewable = energyConsumptionNonRenewable;
        this.totalWasteProduced = totalWasteProduced;
        this.waterUsage = waterUsage;
        this.recyclingRate = recyclingRate;
        this.greenCertifications = greenCertifications;
        this.environmentalCompliance = environmentalCompliance;
        this.energyEfficiency = energyEfficiency;
        this.costOfGreenInitiatives = costOfGreenInitiatives;
    }

    /**
     * Calculates the environmental impact score using a weighted formula.
     * The formula incorporates multiple metrics and normalizes them for fair comparison.
     *
     * @return The environmental impact score.
     */
    public double calculateEnvironmentalImpact() {
        // Normalize metrics
        double normalizedCarbon = totalCarbonEmissions / MAX_CARBON;
        double normalizedNonRenewable = energyConsumptionNonRenewable / MAX_NON_RENEWABLE;
        double normalizedRenewable = energyConsumptionRenewable / MAX_RENEWABLE;
        double normalizedWaste = totalWasteProduced / MAX_WASTE;
        double normalizedWater = waterUsage / MAX_WATER;
        double normalizedRecycling = recyclingRate / MAX_RECYCLING;
        double normalizedEfficiency = energyEfficiency / MAX_EFFICIENCY;
        double normalizedCost = costOfGreenInitiatives / MAX_COST;

        // Calculate environmental impact using weighted formula
        double environmentalImpact =
            (normalizedCarbon * CARBON_WEIGHT) +
            (normalizedNonRenewable * NON_RENEWABLE_WEIGHT) -
            (normalizedRenewable * RENEWABLE_WEIGHT) +
            (normalizedWaste * WASTE_WEIGHT) +
            (normalizedWater * WATER_WEIGHT) -
            (normalizedRecycling * RECYCLING_WEIGHT) -
            (greenCertifications * CERTIFICATION_WEIGHT) -
            (environmentalCompliance * COMPLIANCE_WEIGHT) -
            (normalizedEfficiency * EFFICIENCY_WEIGHT) -
            (normalizedCost * COST_WEIGHT);

        return environmentalImpact;
    }

    // Getters and Setters ( used for me flexibility of the system)
    public double getTotalCarbonEmissions() {
        return totalCarbonEmissions;
    }

    public void setTotalCarbonEmissions(double totalCarbonEmissions) {
        this.totalCarbonEmissions = totalCarbonEmissions;
    }

    public double getEnergyConsumptionRenewable() {
        return energyConsumptionRenewable;
    }

    public void setEnergyConsumptionRenewable(double energyConsumptionRenewable) {
        this.energyConsumptionRenewable = energyConsumptionRenewable;
    }

    public double getEnergyConsumptionNonRenewable() {
        return energyConsumptionNonRenewable;
    }

    public void setEnergyConsumptionNonRenewable(double energyConsumptionNonRenewable) {
        this.energyConsumptionNonRenewable = energyConsumptionNonRenewable;
    }

    public double getTotalWasteProduced() {
        return totalWasteProduced;
    }

    public void setTotalWasteProduced(double totalWasteProduced) {
        this.totalWasteProduced = totalWasteProduced;
    }

    public double getWaterUsage() {
        return waterUsage;
    }

    public void setWaterUsage(double waterUsage) {
        this.waterUsage = waterUsage;
    }

    public double getRecyclingRate() {
        return recyclingRate;
    }

    public void setRecyclingRate(double recyclingRate) {
        this.recyclingRate = recyclingRate;
    }

    public int getGreenCertifications() {
        return greenCertifications;
    }

    public void setGreenCertifications(int greenCertifications) {
        this.greenCertifications = greenCertifications;
    }

    public int getEnvironmentalCompliance() {
        return environmentalCompliance;
    }

    public void setEnvironmentalCompliance(int environmentalCompliance) {
        this.environmentalCompliance = environmentalCompliance;
    }

    public double getEnergyEfficiency() {
        return energyEfficiency;
    }

    public void setEnergyEfficiency(double energyEfficiency) {
        this.energyEfficiency = energyEfficiency;
    }

    public double getCostOfGreenInitiatives() {
        return costOfGreenInitiatives;
    }

    public void setCostOfGreenInitiatives(double costOfGreenInitiatives) {
        this.costOfGreenInitiatives = costOfGreenInitiatives;
    }
}