package es.usc.enso.snachorapido.dao;

import es.usc.enso.snachorapido.model.MachineInventoryItem;

import java.util.List;
import java.util.Optional;

public interface MachineInventoryDao {

    void save(MachineInventoryItem stockItem);

    Optional<MachineInventoryItem> findByMachineIdAndProductId(String machineId, String productId);

    List<MachineInventoryItem> findByMachineId(String machineId);

    List<MachineInventoryItem> findAll();
}

