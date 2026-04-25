package es.usc.enso.snachorapido.model;

import es.usc.enso.snachorapido.exception.InvalidOperationException;

public class MachineInventoryItem {

    private final String machineId;
    private final Product product;
    private int quantity;
    private double estimatedDailyConsumption;

    public MachineInventoryItem(String machineId, Product product, int quantity, double estimatedDailyConsumption) {
        this.machineId = validateMachineId(machineId);
        this.product = validateProduct(product);
        this.quantity = validateQuantity(quantity);
        this.estimatedDailyConsumption = validateEstimatedDailyConsumption(estimatedDailyConsumption);
    }

    public String getMachineId() {
        return machineId;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getEstimatedDailyConsumption() {
        return estimatedDailyConsumption;
    }

    public void setEstimatedDailyConsumption(double estimatedDailyConsumption) {
        this.estimatedDailyConsumption = validateEstimatedDailyConsumption(estimatedDailyConsumption);
    }

    public void addUnits(int units) {
        if (units <= 0) {
            throw new InvalidOperationException("Units to add must be greater than zero");
        }
        quantity += units;
    }

    public void consumeUnits(int units) {
        if (units <= 0) {
            throw new InvalidOperationException("Units to consume must be greater than zero");
        }
        if (units > quantity) {
            throw new InvalidOperationException("Cannot consume more units than available stock");
        }
        quantity -= units;
    }

    private static String validateMachineId(String machineId) {
        if (machineId == null || machineId.isBlank()) {
            throw new IllegalArgumentException("Machine id cannot be blank");
        }
        return machineId.trim();
    }

    private static Product validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        return product;
    }

    private static int validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        return quantity;
    }

    private static double validateEstimatedDailyConsumption(double estimatedDailyConsumption) {
        if (estimatedDailyConsumption <= 0) {
            throw new IllegalArgumentException("Estimated daily consumption must be greater than zero");
        }
        return estimatedDailyConsumption;
    }
}

