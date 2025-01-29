package entities;

import java.util.List;

public class TransportProvider extends SupplyChainEntity {

    private List<String> transportModes;
    private double totalCarbonEmissions;
    private static final double ROAD_EMISSION = 10.0;
    private static final double MARITIME_EMISSION = 5.0;
    private static final double RAIL_EMISSION = 3.0;
    private static final double AIR_EMISSION = 20.0;

    public TransportProvider(String entityID, String name, String location, List<String> transportModes, double totalCarbonEmissions) {
        super(entityID, name, location);
        this.transportModes = transportModes;
        this.totalCarbonEmissions = totalCarbonEmissions;
    }

    public double calculateEnvironmentalImpact() {
        double transportEmissions = 0.0;
        for (String mode : transportModes) {
            switch (mode.toLowerCase()) {
                case "road":
                    transportEmissions += ROAD_EMISSION;
                    break;
                case "maritime":
                    transportEmissions += MARITIME_EMISSION;
                    break;
                case "rail":
                    transportEmissions += RAIL_EMISSION;
                    break;
                case "air":
                    transportEmissions += AIR_EMISSION;
                    break;
                default:
                    // Unknown transport mode, add a default emission value
                    transportEmissions += 10.0;
                    break;
            }
        }
        return totalCarbonEmissions + transportEmissions;
    }
}