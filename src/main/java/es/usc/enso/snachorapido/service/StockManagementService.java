package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.MachineInventoryDao;
import es.usc.enso.snachorapido.exception.EntityNotFoundException;
import es.usc.enso.snachorapido.model.MachineInventoryItem;

public class StockManagementService {

    private final MachineInventoryDao machineInventoryDao;

    public StockManagementService(MachineInventoryDao machineInventoryDao) {
        this.machineInventoryDao = machineInventoryDao;
    }

    public MachineInventoryItem consumeProduct(String machineId, String productId, int units) {
        MachineInventoryItem stockItem = getStockItem(machineId, productId);
        stockItem.consumeUnits(units);
        return stockItem;
    }

    public MachineInventoryItem registerSale(String machineId, String productId, int units) {
        return consumeProduct(machineId, productId, units);
    }

    public MachineInventoryItem restockProduct(String machineId, String productId, int units) {
        MachineInventoryItem stockItem = getStockItem(machineId, productId);
        stockItem.addUnits(units);
        return stockItem;
    }

    public MachineInventoryItem updateEstimatedDailyConsumption(String machineId, String productId, double value) {
        MachineInventoryItem stockItem = getStockItem(machineId, productId);
        stockItem.setEstimatedDailyConsumption(value);
        return stockItem;
    }

    private MachineInventoryItem getStockItem(String machineId, String productId) {
        return machineInventoryDao.findByMachineIdAndProductId(machineId, productId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Product with id %s is not registered in machine %s".formatted(productId, machineId)
            ));
    }
}

