package data;

import analysis.GeneralMetricsAnalyzer;
import analysis.FeatureEngineeringAnalyzer;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import entities.GreenSupplyChainData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class DataManager {

    // Method to import data from any source (CSV, Excel, or database)
    public List<GreenSupplyChainData> importData(String sourceType, String sourcePath, String username, String password, String query) {
        List<GreenSupplyChainData> data = new ArrayList<>();

        switch (sourceType.toLowerCase()) {
            case "csv":
                data = importDataFromCSV(sourcePath);
                break;
            case "excel":
                data = importDataFromExcel(sourcePath);
                break;
            case "database":
                data = importDataFromDatabase(sourcePath, username, password, query);
                break;
            default:
                System.err.println("Unsupported data source type: " + sourceType);
        }

        return data;
    }

    // Method to import data from CSV
    private List<GreenSupplyChainData> importDataFromCSV(String filePath) {
        List<GreenSupplyChainData> data = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> rows = reader.readAll();
            String[] headers = rows.get(0); // Get the header row

            // Skip the header row and process data rows
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length == headers.length) {
                    data.add(parseRow(headers, row));
                } else {
                    System.err.println("Skipping row " + i + ": Incorrect number of columns.");
                }
            }
            System.out.println("Data imported from CSV: " + filePath);
        } catch (IOException | CsvException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
        return data;
    }

    // Method to import data from Excel
    private List<GreenSupplyChainData> importDataFromExcel(String filePath) {
        List<GreenSupplyChainData> data = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(file)) {
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
            Row headerRow = sheet.getRow(0); // Get the header row
            String[] headers = new String[headerRow.getLastCellNum()];
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                headers[i] = headerRow.getCell(i).toString();
            }

            // Process data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String[] rowData = new String[headers.length];
                for (int j = 0; j < headers.length; j++) {
                    Cell cell = row.getCell(j);
                    rowData[j] = (cell != null) ? cell.toString() : "";
                }
                data.add(parseRow(headers, rowData));
            }
            System.out.println("Data imported from Excel: " + filePath);
        } catch (IOException e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
        }
        return data;
    }

    // Method to import data from a database
    private List<GreenSupplyChainData> importDataFromDatabase(String url, String username, String password, String query) {
        List<GreenSupplyChainData> data = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Get column names (headers)
            int columnCount = resultSet.getMetaData().getColumnCount();
            String[] headers = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                headers[i - 1] = resultSet.getMetaData().getColumnName(i);
            }

            // Process data rows
            while (resultSet.next()) {
                String[] rowData = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getString(i);
                }
                data.add(parseRow(headers, rowData));
            }
            System.out.println("Data imported from database.");
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
        return data;
    }

    //  method to parse a row into a GreenSupplyChainData object
    private GreenSupplyChainData parseRow(String[] headers, String[] row) {
        // Parse core fields with default values for missing or invalid data
        int companyID = parseInteger(row[0], -1); // Default: -1 for invalid
        double totalCarbonEmissions = parseDouble(row[2], 0.0); // Default: 0.0 for missing
        double energyConsumptionRenewable = parseDouble(row[3], 0.0); // Default: 0.0 for missing
        double energyConsumptionNonRenewable = parseDouble(row[4], 0.0); // Default: 0.0 for missing
        int greenCertifications = parseInteger(row[5], 0); // Default: 0 for missing
        int environmentalCompliance = parseInteger(row[6], 0); // Default: 0 for missing

        // Create the object with core fields
        GreenSupplyChainData record = new GreenSupplyChainData(
                companyID, totalCarbonEmissions,
                energyConsumptionRenewable, energyConsumptionNonRenewable,
                greenCertifications, environmentalCompliance
        );

        // Add optional fields
        for (int j = 7; j < headers.length; j++) {
            String header = headers[j];
            String value = (j < row.length) ? row[j] : ""; // Handle missing columns
            record.addAdditionalField(header, value);
        }

        return record;
    }

    // method to parse an integer with a default value
    private int parseInteger(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println("Warning: Invalid integer value '" + value + "'. Using default value: " + defaultValue);
            return defaultValue; // Return default value if parsing fails
        }
    }

    // method to parse a double with a default value
    private double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.out.println("Warning: Invalid double value '" + value + "'. Using default value: " + defaultValue);
            return defaultValue; // Return default value if parsing fails
        }
    }

    // clean data
    public List<GreenSupplyChainData> cleanData(List<GreenSupplyChainData> data) {
        System.out.println("Cleaning data...");

        // Step 1: Handle missing values
        handleMissingValues(data);

        // Step 2: Normalize numerical fields
        normalizeData(data);

        // Step 3: Remove duplicates
        removeDuplicates(data);

        // Step 4: Validate cleaned data
        validateCleanedData(data);

        System.out.println("Data cleaning completed.");
        return data;
    }

    // Step 1: Handle missing values
    private void handleMissingValues(List<GreenSupplyChainData> data) {
        if (data.isEmpty()) return;

        System.out.println("Handling missing values...");

        // Calculate medians for numerical fields
        double medianCarbonEmissions = calculateMedian(data, GreenSupplyChainData::getTotalCarbonEmissions);
        double medianEnergyRenewable = calculateMedian(data, GreenSupplyChainData::getEnergyConsumptionRenewable);
        double medianEnergyNonRenewable = calculateMedian(data, GreenSupplyChainData::getEnergyConsumptionNonRenewable);
        int medianGreenCertifications = (int) calculateMedian(data, record -> record.getGreenCertifications());
        int medianEnvironmentalCompliance = (int) calculateMedian(data, record -> record.getEnvironmentalCompliance());

        // Validate medians
        if (medianCarbonEmissions < 0 || medianEnergyRenewable < 0 || medianEnergyNonRenewable < 0) {
            System.err.println("Warning: Insufficient valid data to calculate medians. Using default values.");
            medianCarbonEmissions = 0.0;
            medianEnergyRenewable = 0.0;
            medianEnergyNonRenewable = 0.0;
        }

        System.out.println("Medians calculated:");
        System.out.println("  - Total Carbon Emissions: " + medianCarbonEmissions);
        System.out.println("  - Energy Consumption (Renewable): " + medianEnergyRenewable);
        System.out.println("  - Energy Consumption (Non-Renewable): " + medianEnergyNonRenewable);
        System.out.println("  - Green Certifications: " + medianGreenCertifications);
        System.out.println("  - Environmental Compliance: " + medianEnvironmentalCompliance);

        // Calculate modes for categorical fields
        String modePackagingMaterial = calculateMode(data, "Packaging_Material_Type");
        String modeTransportModes = calculateMode(data, "Transport_Modes");

        System.out.println("Modes calculated:");
        System.out.println("  - Packaging Material Type: " + modePackagingMaterial);
        System.out.println("  - Transport Modes: " + modeTransportModes);

        for (GreenSupplyChainData record : data) {
            // Fill missing core fields with medians
            if (record.getTotalCarbonEmissions() < 0) {
                System.out.println("Replacing missing/invalid Total Carbon Emissions for Company ID " + record.getCompanyID() + " with median: " + medianCarbonEmissions);
                record.setTotalCarbonEmissions(medianCarbonEmissions);
            }
            if (record.getEnergyConsumptionRenewable() < 0) {
                System.out.println("Replacing missing/invalid Energy Consumption (Renewable) for Company ID " + record.getCompanyID() + " with median: " + medianEnergyRenewable);
                record.setEnergyConsumptionRenewable(medianEnergyRenewable);
            }
            if (record.getEnergyConsumptionNonRenewable() < 0) {
                System.out.println("Replacing missing/invalid Energy Consumption (Non-Renewable) for Company ID " + record.getCompanyID() + " with median: " + medianEnergyNonRenewable);
                record.setEnergyConsumptionNonRenewable(medianEnergyNonRenewable);
            }
            if (record.getGreenCertifications() < 0) {
                System.out.println("Replacing missing/invalid Green Certifications for Company ID " + record.getCompanyID() + " with median: " + medianGreenCertifications);
                record.setGreenCertifications(medianGreenCertifications);
            }
            if (record.getEnvironmentalCompliance() < 0) {
                System.out.println("Replacing missing/invalid Environmental Compliance for Company ID " + record.getCompanyID() + " with median: " + medianEnvironmentalCompliance);
                record.setEnvironmentalCompliance(medianEnvironmentalCompliance);
            }

            // Fill missing additional fields
            Map<String, Object> additionalFields = record.getAdditionalFields();
            for (Map.Entry<String, Object> entry : additionalFields.entrySet()) {
                if (entry.getValue() == null || entry.getValue().toString().isEmpty()) {
                    switch (entry.getKey()) {
                        case "Water_Usage":
                        case "Total_Waste_Produced":
                            System.out.println("Replacing missing " + entry.getKey() + " for Company ID " + record.getCompanyID() + " with default value: 0.0");
                            additionalFields.put(entry.getKey(), 0.0); // Default for numerical fields
                            break;
                        case "Packaging_Material_Type":
                            System.out.println("Replacing missing Packaging Material Type for Company ID " + record.getCompanyID() + " with mode: " + modePackagingMaterial);
                            additionalFields.put(entry.getKey(), modePackagingMaterial);
                            break;
                        case "Transport_Modes":
                            System.out.println("Replacing missing Transport Modes for Company ID " + record.getCompanyID() + " with mode: " + modeTransportModes);
                            additionalFields.put(entry.getKey(), modeTransportModes);
                            break;
                        default:
                            System.out.println("Replacing missing " + entry.getKey() + " for Company ID " + record.getCompanyID() + " with default value: Unknown");
                            additionalFields.put(entry.getKey(), "Unknown"); // Default for other fields
                    }
                }
            }
        }
    }

    // Step 2: Normalize numerical fields
    private void normalizeData(List<GreenSupplyChainData> data) {
        if (data.isEmpty()) return;

        System.out.println("Normalizing numerical fields...");

        // Calculate min and max values for numerical fields
        double minCarbonEmissions = calculateMin(data, GreenSupplyChainData::getTotalCarbonEmissions);
        double maxCarbonEmissions = calculateMax(data, GreenSupplyChainData::getTotalCarbonEmissions);
        double minEnergyRenewable = calculateMin(data, GreenSupplyChainData::getEnergyConsumptionRenewable);
        double maxEnergyRenewable = calculateMax(data, GreenSupplyChainData::getEnergyConsumptionRenewable);
        double minEnergyNonRenewable = calculateMin(data, GreenSupplyChainData::getEnergyConsumptionNonRenewable);
        double maxEnergyNonRenewable = calculateMax(data, GreenSupplyChainData::getEnergyConsumptionNonRenewable);

        System.out.println("Min-Max values calculated:");
        System.out.println("  - Total Carbon Emissions: Min=" + minCarbonEmissions + ", Max=" + maxCarbonEmissions);
        System.out.println("  - Energy Consumption (Renewable): Min=" + minEnergyRenewable + ", Max=" + maxEnergyRenewable);
        System.out.println("  - Energy Consumption (Non-Renewable): Min=" + minEnergyNonRenewable + ", Max=" + maxEnergyNonRenewable);

        for (GreenSupplyChainData record : data) {
            // Normalize core fields using Min-Max normalization
            record.setTotalCarbonEmissions((record.getTotalCarbonEmissions() - minCarbonEmissions) / (maxCarbonEmissions - minCarbonEmissions));
            record.setEnergyConsumptionRenewable((record.getEnergyConsumptionRenewable() - minEnergyRenewable) / (maxEnergyRenewable - minEnergyRenewable));
            record.setEnergyConsumptionNonRenewable((record.getEnergyConsumptionNonRenewable() - minEnergyNonRenewable) / (maxEnergyNonRenewable - minEnergyNonRenewable));

            // Normalize additional numerical fields
            Map<String, Object> additionalFields = record.getAdditionalFields();
            for (Map.Entry<String, Object> entry : additionalFields.entrySet()) {
                if (entry.getValue() instanceof Double) {
                    double value = (Double) entry.getValue();
                    System.out.println("Normalizing " + entry.getKey() + " for Company ID " + record.getCompanyID() + ": " + value);
                    additionalFields.put(entry.getKey(), value / 1000); // Example: Scale down by 1000
                }
            }
        }
    }

    // Step 3: Remove duplicates
    private void removeDuplicates(List<GreenSupplyChainData> data) {
        System.out.println("Removing duplicates...");
        Set<String> uniqueKeys = new HashSet<>();
        List<GreenSupplyChainData> duplicates = new ArrayList<>();

        for (GreenSupplyChainData record : data) {
            String key = record.getCompanyID() + "-" ; // Composite key
            if (!uniqueKeys.add(key)) {
                duplicates.add(record);
            }
        }

        int duplicatesRemoved = duplicates.size();
        data.removeAll(duplicates);

        System.out.println("Removed " + duplicatesRemoved + " duplicate records.");
        if (duplicatesRemoved > 0) {
            System.out.println("Duplicate records:");
            duplicates.forEach(record -> System.out.println("Company ID: " + record.getCompanyID() ));
        }
    }

    // Step 4: Validate cleaned data
    private void validateCleanedData(List<GreenSupplyChainData> data) {
        System.out.println("Validating cleaned data...");
        for (GreenSupplyChainData record : data) {
            if (record.getTotalCarbonEmissions() < 0 || record.getEnergyConsumptionRenewable() < 0 || record.getEnergyConsumptionNonRenewable() < 0) {
                System.err.println("Warning: Invalid values found in cleaned data for Company ID " + record.getCompanyID());
            }
        }
        System.out.println("Validation completed.");
    }

    // method to calculate the median of a numerical field
    private double calculateMedian(List<GreenSupplyChainData> data, ToDoubleFunction<GreenSupplyChainData> fieldExtractor) {
        List<Double> values = data.stream()
                .mapToDouble(fieldExtractor)
                .filter(value -> value >= 0) // Exclude invalid values
                .sorted()
                .boxed()
                .collect(Collectors.toList());
        if (values.isEmpty()) return 0.0;
        int mid = values.size() / 2;
        return values.get(mid);
    }

    //method to calculate the mode of a categorical field
    private String calculateMode(List<GreenSupplyChainData> data, String fieldName) {
        Map<String, Long> frequencyMap = data.stream()
                .map(record -> record.getAdditionalFields().get(fieldName))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
    }

    // method to calculate the maximum value of a numerical field
    private double calculateMax(List<GreenSupplyChainData> data, ToDoubleFunction<GreenSupplyChainData> fieldExtractor) {
        return data.stream()
                .mapToDouble(fieldExtractor)
                .max()
                .orElse(1.0); // Avoid division by zero
    }

    // method to calculate the minimum value of a numerical field
    private double calculateMin(List<GreenSupplyChainData> data, ToDoubleFunction<GreenSupplyChainData> fieldExtractor) {
        return data.stream()
                .mapToDouble(fieldExtractor)
                .min()
                .orElse(0.0); // Default to 0.0 if no valid values
    }

    // Method to store data in DataStore
    public void storeData(List<GreenSupplyChainData> data) {
        DataStore dataStore = new DataStore();
        dataStore.saveDataAsCsv(data); // Call the new method to save data as CSV
        System.out.println("Data stored successfully.");
    }

    // Method to analyze data (general metrics and feature engineering)
    public void analyzeData(List<GreenSupplyChainData> data) {
        // Step 1: Clean the data
        List<GreenSupplyChainData> cleanedData = cleanData(data);

        // Step 2: Perform general metrics analysis
        GeneralMetricsAnalyzer generalMetricsAnalyzer = new GeneralMetricsAnalyzer();
        generalMetricsAnalyzer.analyze(cleanedData);

        // Step 3: Perform feature engineering for individual companies
        FeatureEngineeringAnalyzer featureEngineeringAnalyzer = new FeatureEngineeringAnalyzer();
        featureEngineeringAnalyzer.analyze(cleanedData);

        // Step 4: Store the cleaned and analyzed data
        storeData(cleanedData);
    }
}