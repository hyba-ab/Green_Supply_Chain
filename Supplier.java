package entities;

public class Supplier extends SupplyChainEntity {

    private double supplierCarbonFootprint;
    private int supplierPerformance; // Environmental performance score (0-100)

    public Supplier(String entityID, String name, String location, double supplierCarbonFootprint, int supplierPerformance) {
        super(entityID, name, location);
        this.supplierCarbonFootprint = supplierCarbonFootprint;
        this.supplierPerformance = supplierPerformance;
    }

    public double calculateEnvironmentalImpact() {
        // Environmental Impact = Supplier_Carbon_Footprint * (1 - (Supplier_Performance / 100))
        return supplierCarbonFootprint * (1 - (supplierPerformance / 100.0));
    }
}