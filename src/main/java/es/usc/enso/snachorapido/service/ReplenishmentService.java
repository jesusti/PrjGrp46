package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.MachineInventoryDao;
import es.usc.enso.snachorapido.model.MachineInventoryItem;
import es.usc.enso.snachorapido.model.ReplenishmentNeed;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class ReplenishmentService {

    private final MachineInventoryDao machineInventoryDao;

    public ReplenishmentService(MachineInventoryDao machineInventoryDao) {
        this.machineInventoryDao = machineInventoryDao;
    }

    public List<ReplenishmentNeed> getReplenishmentNeeds(LocalDate referenceDate) {
        return machineInventoryDao.findAll().stream()
            .map(stockItem -> buildReplenishmentNeed(stockItem, referenceDate))
            .sorted(Comparator.comparing(ReplenishmentNeed::getLatestReplenishmentDate))
            .toList();
    }

    public List<ReplenishmentNeed> getProductsNeedingReplenishment(LocalDate referenceDate) {
        return getReplenishmentNeeds(referenceDate).stream()
            .filter(need -> need.mustBeReplenishedBy(referenceDate))
            .toList();
    }

    private ReplenishmentNeed buildReplenishmentNeed(MachineInventoryItem stockItem, LocalDate referenceDate) {
        int daysUntilDepletion = stockItem.getQuantity() == 0
            ? 0
            : (int) Math.ceil(stockItem.getQuantity() / stockItem.getEstimatedDailyConsumption());
        LocalDate predictedDepletionDate = referenceDate.plusDays(daysUntilDepletion);
        LocalDate latestReplenishmentDate = predictedDepletionDate.minusDays(1);

        return new ReplenishmentNeed(
            stockItem.getMachineId(),
            stockItem.getProduct().getId(),
            stockItem.getProduct().getName(),
            stockItem.getQuantity(),
            stockItem.getEstimatedDailyConsumption(),
            predictedDepletionDate,
            latestReplenishmentDate
        );
    }
}
