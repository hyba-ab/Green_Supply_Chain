package entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GreenSupplyChainData implements Serializable {
    private static final long serialVersionUID = 1L;

    // Core fields
    private int companyID;
    private double totalCarbonEmissions;
    private double energyConsumptionRenewable;
    private double energyConsumptionNonRenewable;
    private int supplierPerformance;
    private int greenCertifications; 
    private int environmentalCompliance; 

    // Optional fields (stored as a map)
    private Map<String, Object> additionalFields;

    // Constructor for core fields
    public GreenSupplyChainData(int companyID, double totalCarbonEmissions,
                                double energyConsumptionRenewable, double energyConsumptionNonRenewable,
                                int greenCertifications, int environmentalCompliance) { // environmentalCompliance parameter
        this.companyID = companyID;
        this.totalCarbonEmissions = totalCarbonEmissions;
        this.energyConsumptionRenewable = energyConsumptionRenewable;
        this.energyConsumptionNonRenewable = energyConsumptionNonRenewable;
        this.greenCertifications = greenCertifications; 
        this.environmentalCompliance = environmentalCompliance; 
        this.additionalFields = new HashMap<>();
    }

    // Getters and Setters for core fields
    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

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

    public int getSupplierPerformance() {
        return supplierPerformance;
    }

    public void setSupplierPerformance(int supplierPerformance) {
        this.supplierPerformance = supplierPerformance;
    }

    // Getter and Setter for greenCertifications
    public int getGreenCertifications() {
        return greenCertifications;
    }

    public void setGreenCertifications(int greenCertifications) {
        this.greenCertifications = greenCertifications;
    }

    // Getter and Setter for environmentalCompliance
    public int getEnvironmentalCompliance() {
        return environmentalCompliance;
    }

    public void setEnvironmentalCompliance(int environmentalCompliance) {
        this.environmentalCompliance = environmentalCompliance;
    }

    // handle additional fields
    public void addAdditionalField(String key, Object value) {
        // Parse the value to Double if it's a String
        if (value instanceof String) {
            try {
                value = Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                // If parsing fails, use a default value (e.g., 0.0)
                value = 0.0;
            }
        }
        additionalFields.put(key, value);
    }

    public Object getAdditionalField(String key) {
        return additionalFields.get(key);
    }

    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }

    //methods for waste and water usage
    public double getTotalWasteProduced() {
        Object value = additionalFields.get("Total_Waste_Produced");
        if (value instanceof Double) {
            return (double) value;
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0; // Default value if parsing fails
            }
        }
        return 0.0; // Default value if the key is not found or the value is not a Double or String
    }

    public double getWaterUsage() {
        Object value = additionalFields.get("Water_Usage");
        if (value instanceof Double) {
            return (double) value;
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0; // Default value if parsing fails
            }
        }
        return 0.0; // Default value if the key is not found or the value is not a Double or String
    }

    // Method to get transport modes
    public String getTransportModes() {
        Object value = additionalFields.get("Transport_Modes");
        if (value instanceof String) {
            return (String) value;
        } else if (value != null) {
            return value.toString(); // used to Convert to String if not already
        }
        return ""; // Default value if the key is not found or the value is null
    }

    //  Renewable Energy Ratio
    public double getRenewableEnergyRatio() {
        double totalEnergy = energyConsumptionRenewable + energyConsumptionNonRenewable;
        return totalEnergy == 0 ? 0 : energyConsumptionRenewable / totalEnergy;
    }

    //  Non-Renewable Energy Ratio
    public double getNonRenewableEnergyRatio() {
        double totalEnergy = energyConsumptionRenewable + energyConsumptionNonRenewable;
        return totalEnergy == 0 ? 0 : energyConsumptionNonRenewable / totalEnergy;
    }

    //  Waste to Emissions Ratio
    public double getWasteToEmissionsRatio() {
        return totalCarbonEmissions == 0 ? 0 : getTotalWasteProduced() / totalCarbonEmissions;
    }

    //  Carbon Intensity
    public double getCarbonIntensity() {
        double totalEnergy = energyConsumptionRenewable + energyConsumptionNonRenewable;
        return totalEnergy == 0 ? 0 : totalCarbonEmissions / totalEnergy;
    }

    //  Energy Efficiency (assuming production output is available in additional fields)
    public double getEnergyEfficiency() {
        Object productionOutput = additionalFields.get("Production_Output");
        double production = 0.0;
        if (productionOutput instanceof Double) {
            production = (double) productionOutput;
        } else if (productionOutput instanceof String) {
            try {
                production = Double.parseDouble((String) productionOutput);
            } catch (NumberFormatException e) {
                production = 0.0; // Default value if parsing fails
            }
        }
        double totalEnergy = energyConsumptionRenewable + energyConsumptionNonRenewable;
        return production == 0 ? 0 : totalEnergy / production;
    }

    //  Get Recycling Rate
    public double getRecyclingRate() {
        Object value = additionalFields.get("Recycling_Rate");
        if (value instanceof Double) {
            return (double) value;
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0; // Default value if parsing fails
            }
        }
        return 0.0; // Default value if the key is not found or the value is not a Double or String
    }

    //  Get Cost of Green Initiatives
    public double getCostOfGreenInitiatives() {
        Object value = additionalFields.get("Cost_of_Green_Initiatives");
        if (value instanceof Double) {
            return (double) value;
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0; // Default value if parsing fails
            }
        }
        return 0.0; // Default value if the key is not found or the value is not a Double or String
    }

    // Get Sustainability Score
    public String getSustainabilityScore() {
        Object value = additionalFields.get("Sustainability_Score");
        if (value instanceof String) {
            return (String) value;
        } else if (value != null) {
            return value.toString(); // Convert to String if not already
        }
        return "Medium"; // Default value if the key is not found or the value is null
    }

    @Override
    public String toString() {
        return "GreenSupplyChainData{" +
                "companyID=" + companyID +
                ", totalCarbonEmissions=" + totalCarbonEmissions +
                ", energyConsumptionRenewable=" + energyConsumptionRenewable +
                ", energyConsumptionNonRenewable=" + energyConsumptionNonRenewable +
                ", supplierPerformance=" + supplierPerformance +
                ", greenCertifications=" + greenCertifications + // Added greenCertifications to toString
                ", environmentalCompliance=" + environmentalCompliance + // Added environmentalCompliance to toString
                ", additionalFields=" + additionalFields +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GreenSupplyChainData that = (GreenSupplyChainData) o;
        return companyID == that.companyID &&
               Double.compare(that.totalCarbonEmissions, totalCarbonEmissions) == 0 &&
               Double.compare(that.energyConsumptionRenewable, energyConsumptionRenewable) == 0 &&
               Double.compare(that.energyConsumptionNonRenewable, energyConsumptionNonRenewable) == 0 &&
               supplierPerformance == that.supplierPerformance &&
               greenCertifications == that.greenCertifications && // Added greenCertifications to equals
               environmentalCompliance == that.environmentalCompliance && // Added environmentalCompliance to equals
               Objects.equals(additionalFields, that.additionalFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyID, totalCarbonEmissions, energyConsumptionRenewable, energyConsumptionNonRenewable, supplierPerformance, greenCertifications, environmentalCompliance, additionalFields); // Added greenCertifications and environmentalCompliance to hashCode
    }
}