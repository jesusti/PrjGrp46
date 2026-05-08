package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.MachineInventoryDao;
import es.usc.enso.snachorapido.model.MachineInventoryItem;

import java.util.Comparator;
import java.util.List;

public class StockGlobalService {

    private final MachineInventoryDao machineInventoryDao;

    public StockGlobalService(MachineInventoryDao machineInventoryDao) {
        this.machineInventoryDao = machineInventoryDao;
    }

    public List<MachineInventoryItem> obterStockCompleto() {
        return machineInventoryDao.findAll().stream()
            .sorted(Comparator.comparing(MachineInventoryItem::getMachineId)
                .thenComparing(item -> item.getProduct().getId()))
            .toList();
    }

    public List<MachineInventoryItem> obterStockDeMaquina(String idMaquina) {
        return machineInventoryDao.findByMachineId(idMaquina).stream()
            .sorted(Comparator.comparing(item -> item.getProduct().getId()))
            .toList();
    }
}
