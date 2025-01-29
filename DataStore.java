package data;

import entities.GreenSupplyChainData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DataStore {

    private static final String CSV_FILE = "data_store.csv"; // CSV file where cleaned and validated  data is stored (at the same folder of the code base).

    // Save data to a CSV file
    public void saveDataAsCsv(List<GreenSupplyChainData> data) {
        if (data == null || data.isEmpty()) {
            System.err.println("Error: No data to save.");
            return;
        }

        try (FileWriter writer = new FileWriter(CSV_FILE)) {
            // Write the header row
            writer.write("Company_ID,Year,Total_Carbon_Emissions,Energy_Consumption_Renewable,Energy_Consumption_NonRenewable,");
            writer.write("Total_Waste_Produced,Water_Usage,Packaging_Material_Type,Green_Certifications,Supplier_Carbon_Footprint,");
            writer.write("Product_Carbon_Footprint,Recycling_Rate,Transport_Modes,Energy_Efficiency,Environmental_Compliance,");
            writer.write("Cost_of_Green_Initiatives,Sustainability_Score\n");

            // Write data rows
            for (GreenSupplyChainData record : data) {
                writer.write(record.getCompanyID() + ",");
                writer.write(record.getTotalCarbonEmissions() + ",");
                writer.write(record.getEnergyConsumptionRenewable() + ",");
                writer.write(record.getEnergyConsumptionNonRenewable() + ",");

                // Write additional fields
                Map<String, Object> additionalFields = record.getAdditionalFields();
                writer.write(getFieldValue(additionalFields, "Total_Waste_Produced") + ",");
                writer.write(getFieldValue(additionalFields, "Water_Usage") + ",");
                writer.write(getFieldValue(additionalFields, "Packaging_Material_Type") + ",");
                writer.write(getFieldValue(additionalFields, "Green_Certifications") + ",");
                writer.write(getFieldValue(additionalFields, "Supplier_Carbon_Footprint") + ",");
                writer.write(getFieldValue(additionalFields, "Product_Carbon_Footprint") + ",");
                writer.write(getFieldValue(additionalFields, "Recycling_Rate") + ",");
                writer.write(getFieldValue(additionalFields, "Transport_Modes") + ",");
                writer.write(getFieldValue(additionalFields, "Energy_Efficiency") + ",");
                writer.write(getFieldValue(additionalFields, "Environmental_Compliance") + ",");
                writer.write(getFieldValue(additionalFields, "Cost_of_Green_Initiatives") + ",");
                writer.write(getFieldValue(additionalFields, "Sustainability_Score") + "\n");
            }

            System.out.println("Data saved to CSV file: " + new File(CSV_FILE).getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving data to CSV file: " + e.getMessage());
        }
    }

    //  method to safely get field values from the additionalFields map
    private String getFieldValue(Map<String, Object> additionalFields, String key) {
        Object value = additionalFields.get(key);
        return (value != null) ? value.toString() : ""; // Return empty string if the field is missing
    }
}