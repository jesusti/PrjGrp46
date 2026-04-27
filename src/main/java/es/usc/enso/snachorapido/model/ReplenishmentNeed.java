package es.usc.enso.snachorapido.model;

import java.time.LocalDate;

public class ReplenishmentNeed {

    private final String machineId;
    private final String productId;
    private final String productName;
    private final int currentQuantity;
    private final double estimatedDailyConsumption;
    private final LocalDate predictedDepletionDate;
    private final LocalDate latestReplenishmentDate;

    public ReplenishmentNeed(
        String machineId,
        String productId,
        String productName,
        int currentQuantity,
        double estimatedDailyConsumption,
        LocalDate predictedDepletionDate,
        LocalDate latestReplenishmentDate
    ) {
        this.machineId = machineId;
        this.productId = productId;
        this.productName = productName;
        this.currentQuantity = currentQuantity;
        this.estimatedDailyConsumption = estimatedDailyConsumption;
        this.predictedDepletionDate = predictedDepletionDate;
        this.latestReplenishmentDate = latestReplenishmentDate;
    }

    public String getMachineId() {
        return machineId;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public double getEstimatedDailyConsumption() {
        return estimatedDailyConsumption;
    }

    public LocalDate getPredictedDepletionDate() {
        return predictedDepletionDate;
    }

    public LocalDate getLatestReplenishmentDate() {
        return latestReplenishmentDate;
    }

    public boolean mustBeReplenishedBy(LocalDate date) {
        return !latestReplenishmentDate.isAfter(date);
    }
}

