package reports;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.effect.Glow;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.List;

public class RadarChart extends Pane {

    private List<String> metrics;
    private List<Double> values;
    private double maxValue;
    private Color axisLabelFill = Color.DARKBLUE; // Default axis label color
    private Color axisLineColor = Color.DARKGRAY; // Default axis line color
    private double axisLineWidth = 2.0; // Default axis line width

    public RadarChart(List<String> metrics, List<Double> values, double maxValue) {
        this.metrics = metrics;
        this.values = values;
        this.maxValue = maxValue;
        drawChart();
    }

    // Setter for axis label fill color
    public void setAxisLabelFill(Color color) {
        this.axisLabelFill = color;
        drawChart(); // Redraw the chart with the new color
    }

    // Setter for axis line color
    public void setAxisLineColor(Color color) {
        this.axisLineColor = color;
        drawChart(); // Redraw the chart with the new color
    }

    // Setter for axis line width
    public void setAxisLineWidth(double width) {
        this.axisLineWidth = width;
        drawChart(); // Redraw the chart with the new width
    }

    private void drawChart() {
        this.getChildren().clear(); // Clear the chart before redrawing

        int numMetrics = metrics.size();
        double angleStep = 360.0 / numMetrics;
        double radius = 150; // Radius of the radar chart
        double centerX = 200; // Center X of the chart
        double centerY = 200; // Center Y of the chart

        // Draw the axes (spokes)
        for (int i = 0; i < numMetrics; i++) {
            double angle = Math.toRadians(i * angleStep);
            double endX = centerX + radius * Math.cos(angle);
            double endY = centerY - radius * Math.sin(angle);

            Line axis = new Line(centerX, centerY, endX, endY);
            axis.setStroke(axisLineColor); // Use the specified axis line color
            axis.setStrokeWidth(axisLineWidth); // Use the specified axis line width
            this.getChildren().add(axis);

            // Add metric labels
            Text label = new Text(endX, endY, metrics.get(i));
            label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            label.setFill(axisLabelFill); // Use the specified axis label fill color
            this.getChildren().add(label);
        }

        // Draw the data polygon
        Polygon polygon = new Polygon();
        for (int i = 0; i < numMetrics; i++) {
            double angle = Math.toRadians(i * angleStep);
            double value = values.get(i);
            double scaledValue = (value / maxValue) * radius;
            double pointX = centerX + scaledValue * Math.cos(angle);
            double pointY = centerY - scaledValue * Math.sin(angle);
            polygon.getPoints().addAll(pointX, pointY);
        }

        // Create a gradient fill for the polygon
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(0, 128, 255, 0.8)),
                new Stop(1, Color.rgb(0, 255, 128, 0.8)));

        polygon.setFill(gradient); // Semi-transparent gradient fill
        polygon.setStroke(Color.BLUE);
        polygon.setStrokeWidth(2);
        polygon.setEffect(new DropShadow(10, Color.BLUE)); // Add a shadow effect

        // Add hover effects and tooltips
        polygon.setOnMouseEntered(event -> {
            polygon.setEffect(new Glow(0.8));
            Tooltip.install(polygon, new Tooltip("Value: " + values.toString()));
        });
        polygon.setOnMouseExited(event -> polygon.setEffect(new DropShadow(10, Color.BLUE)));

        this.getChildren().add(polygon);
    }
}