package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.MachineInventoryDao;
import es.usc.enso.snachorapido.model.MachineInventoryItem;

import java.time.LocalDate;

public class PlanificacionMasivaService {

    private final MachineInventoryDao machineInventoryDao;

    public PlanificacionMasivaService(MachineInventoryDao machineInventoryDao) {
        this.machineInventoryDao = machineInventoryDao;
    }

    public long contarProductosAReponerHasta(LocalDate fechaReferencia, LocalDate fechaObjetivo) {
        validarFechas(fechaReferencia, fechaObjetivo);

        return machineInventoryDao.findAll().stream()
            .filter(stockItem -> !calcularFechaLimite(stockItem, fechaReferencia).isAfter(fechaObjetivo))
            .count();
    }

    public boolean hayReposicionesPendientes(LocalDate fechaReferencia, LocalDate fechaObjetivo) {
        return contarProductosAReponerHasta(fechaReferencia, fechaObjetivo) > 0;
    }

    private void validarFechas(LocalDate fechaReferencia, LocalDate fechaObjetivo) {
        if (fechaReferencia == null || fechaObjetivo == null) {
            throw new IllegalArgumentException("Las fechas de planificacion no pueden ser nulas");
        }
        if (fechaObjetivo.isBefore(fechaReferencia)) {
            throw new IllegalArgumentException("La fecha objetivo no puede ser anterior a la referencia");
        }
    }

    private LocalDate calcularFechaLimite(MachineInventoryItem stockItem, LocalDate fechaReferencia) {
        int diasHastaAgotamiento = stockItem.getQuantity() == 0
            ? 0
            : (int) Math.ceil(stockItem.getQuantity() / stockItem.getEstimatedDailyConsumption());
        return fechaReferencia.plusDays(diasHastaAgotamiento).minusDays(1);
    }
}
