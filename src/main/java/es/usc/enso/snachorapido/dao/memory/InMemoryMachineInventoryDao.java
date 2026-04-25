package es.usc.enso.snachorapido.dao.memory;

import es.usc.enso.snachorapido.dao.MachineInventoryDao;
import es.usc.enso.snachorapido.exception.DuplicateEntityException;
import es.usc.enso.snachorapido.model.MachineInventoryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryMachineInventoryDao implements MachineInventoryDao {

    private final List<MachineInventoryItem> stockItems = new ArrayList<>();

    @Override
    public void save(MachineInventoryItem stockItem) {
        if (findByMachineIdAndProductId(stockItem.getMachineId(), stockItem.getProduct().getId()).isPresent()) {
            throw new DuplicateEntityException(
                "Product with id %s is already registered in machine %s"
                    .formatted(stockItem.getProduct().getId(), stockItem.getMachineId())
            );
        }
        stockItems.add(stockItem);
    }

    @Override
    public Optional<MachineInventoryItem> findByMachineIdAndProductId(String machineId, String productId) {
        return stockItems.stream()
            .filter(item -> item.getMachineId().equals(machineId) && item.getProduct().getId().equals(productId))
            .findFirst();
    }

    @Override
    public List<MachineInventoryItem> findByMachineId(String machineId) {
        return stockItems.stream()
            .filter(item -> item.getMachineId().equals(machineId))
            .toList();
    }

    @Override
    public List<MachineInventoryItem> findAll() {
        return List.copyOf(stockItems);
    }
}

