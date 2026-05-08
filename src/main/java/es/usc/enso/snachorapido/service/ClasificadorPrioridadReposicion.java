package es.usc.enso.snachorapido.service;

import java.time.LocalDate;

public class ClasificadorPrioridadReposicion {

    public PrioridadReposicion clasificarPrioridad(
        int cantidadActual,
        double consumoDiario,
        LocalDate fechaReferencia,
        LocalDate fechaObjetivo
    ) {
        validarDatos(cantidadActual, consumoDiario, fechaReferencia, fechaObjetivo);

        if (cantidadActual == 0) {
            return PrioridadReposicion.SIN_STOCK;
        }

        LocalDate fechaLimite = calcularFechaLimite(cantidadActual, consumoDiario, fechaReferencia);
        if (!fechaLimite.isAfter(fechaReferencia)) {
            return PrioridadReposicion.URGENTE;
        }
        if (fechaObjetivo.isEqual(fechaReferencia)) {
            return PrioridadReposicion.NO_NECESARIA;
        }
        if (!fechaLimite.isAfter(fechaObjetivo)) {
            return PrioridadReposicion.PROXIMA;
        }
        return PrioridadReposicion.NO_NECESARIA;
    }

    private void validarDatos(
        int cantidadActual,
        double consumoDiario,
        LocalDate fechaReferencia,
        LocalDate fechaObjetivo
    ) {
        if (fechaReferencia == null) {
            throw new IllegalArgumentException("La fecha de referencia no puede ser nula");
        }
        if (fechaObjetivo == null) {
            throw new IllegalArgumentException("La fecha objetivo no puede ser nula");
        }
        if (fechaObjetivo.isBefore(fechaReferencia)) {
            throw new IllegalArgumentException("La fecha objetivo no puede ser anterior a la referencia");
        }
        if (cantidadActual < 0) {
            throw new IllegalArgumentException("La cantidad actual no puede ser negativa");
        }
        if (consumoDiario <= 0) {
            throw new IllegalArgumentException("El consumo diario debe ser mayor que cero");
        }
    }

    private LocalDate calcularFechaLimite(int cantidadActual, double consumoDiario, LocalDate fechaReferencia) {
        int diasHastaAgotamiento = (int) Math.ceil(cantidadActual / consumoDiario);
        return fechaReferencia.plusDays(diasHastaAgotamiento).minusDays(1);
    }
}
