package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.MachineInventoryDao;
import es.usc.enso.snachorapido.model.MachineInventoryItem;
import es.usc.enso.snachorapido.model.ReplenishmentNeed;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class PlanReposicionService {

    private final MachineInventoryDao machineInventoryDao;

    public PlanReposicionService(MachineInventoryDao machineInventoryDao) {
        this.machineInventoryDao = machineInventoryDao;
    }

    public List<ReplenishmentNeed> crearPlanReposicion(LocalDate fechaReferencia, LocalDate fechaObjetivo) {
        validarFechas(fechaReferencia, fechaObjetivo);

        return machineInventoryDao.findAll().stream()
            .map(stockItem -> crearNecesidadReposicion(stockItem, fechaReferencia))
            .filter(need -> !need.getLatestReplenishmentDate().isAfter(fechaObjetivo))
            .sorted(Comparator.comparing(ReplenishmentNeed::getLatestReplenishmentDate)
                .thenComparing(ReplenishmentNeed::getMachineId)
                .thenComparing(ReplenishmentNeed::getProductId))
            .toList();
    }

    private void validarFechas(LocalDate fechaReferencia, LocalDate fechaObjetivo) {
        if (fechaReferencia == null) {
            throw new IllegalArgumentException("La fecha de referencia no puede ser nula");
        }
        if (fechaObjetivo == null) {
            throw new IllegalArgumentException("La fecha objetivo no puede ser nula");
        }
        if (fechaObjetivo.isBefore(fechaReferencia)) {
            throw new IllegalArgumentException("La fecha objetivo no puede ser anterior a la fecha de referencia");
        }
    }

    private ReplenishmentNeed crearNecesidadReposicion(MachineInventoryItem stockItem, LocalDate fechaReferencia) {
        int diasHastaAgotamiento = stockItem.getQuantity() == 0
            ? 0
            : (int) Math.ceil(stockItem.getQuantity() / stockItem.getEstimatedDailyConsumption());
        LocalDate fechaAgotamiento = fechaReferencia.plusDays(diasHastaAgotamiento);
        LocalDate fechaLimite = fechaAgotamiento.minusDays(1);

        return new ReplenishmentNeed(
            stockItem.getMachineId(),
            stockItem.getProduct().getId(),
            stockItem.getProduct().getName(),
            stockItem.getQuantity(),
            stockItem.getEstimatedDailyConsumption(),
            fechaAgotamiento,
            fechaLimite
        );
    }
}
