package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.MachineDao;
import es.usc.enso.snachorapido.dao.MachineInventoryDao;
import es.usc.enso.snachorapido.exception.EntityNotFoundException;
import es.usc.enso.snachorapido.model.MachineInventoryItem;

import java.util.List;

public class InventoryQueryService {

    private final MachineDao machineDao;
    private final MachineInventoryDao machineInventoryDao;

    public InventoryQueryService(MachineDao machineDao, MachineInventoryDao machineInventoryDao) {
        this.machineDao = machineDao;
        this.machineInventoryDao = machineInventoryDao;
    }

    public List<MachineInventoryItem> getMachineInventory(String machineId) {
        ensureMachineExists(machineId);
        return machineInventoryDao.findByMachineId(machineId);
    }

    public MachineInventoryItem getMachineStock(String machineId, String productId) {
        ensureMachineExists(machineId);
        return machineInventoryDao.findByMachineIdAndProductId(machineId, productId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Product with id %s is not registered in machine %s".formatted(productId, machineId)
            ));
    }

    private void ensureMachineExists(String machineId) {
        machineDao.findById(machineId)
            .orElseThrow(() -> new EntityNotFoundException("Machine with id %s was not found".formatted(machineId)));
    }
}

