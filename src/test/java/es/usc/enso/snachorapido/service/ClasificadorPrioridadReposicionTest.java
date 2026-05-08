package es.usc.enso.snachorapido.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Sprint 2 - Caja blanca y McCabe de prioridad")
class ClasificadorPrioridadReposicionTest {

    private final ClasificadorPrioridadReposicion clasificador = new ClasificadorPrioridadReposicion();

    @ParameterizedTest(name = "cantidad={0}, consumo={1}, referencia={2}, objetivo={3} => {4}")
    @CsvSource({
        "0, 2.0, 2026-04-24, 2026-04-30, SIN_STOCK",
        "1, 2.0, 2026-04-24, 2026-04-30, URGENTE",
        "10, 2.0, 2026-04-24, 2026-04-24, NO_NECESARIA",
        "6, 2.0, 2026-04-24, 2026-04-30, PROXIMA",
        "20, 2.0, 2026-04-24, 2026-04-30, NO_NECESARIA"
    })
    @DisplayName("Cubre los caminos principales de complejidad ciclomatica 5")
    void debeClasificarPrioridadSegunCaminosMcCabe(
        int cantidad,
        double consumo,
        LocalDate fechaReferencia,
        LocalDate fechaObjetivo,
        PrioridadReposicion prioridadEsperada
    ) {
        // El metodo clasificarPrioridad tiene cuatro decisiones y complejidad ciclomatica 5.
        assertEquals(
            prioridadEsperada,
            clasificador.clasificarPrioridad(cantidad, consumo, fechaReferencia, fechaObjetivo)
        );
    }

    @Test
    @DisplayName("Rechaza fecha objetivo anterior")
    void debeRechazarFechaObjetivoAnterior() {
        // Rama de validacion: una ventana temporal invertida no es una planificacion valida.
        assertThrows(IllegalArgumentException.class, () -> clasificador.clasificarPrioridad(
            10,
            2.0,
            LocalDate.of(2026, 4, 24),
            LocalDate.of(2026, 4, 23)
        ));
    }

    @Test
    @DisplayName("Rechaza cantidad negativa")
    void debeRechazarCantidadNegativa() {
        // Rama de validacion de datos: el stock no puede ser negativo.
        assertThrows(IllegalArgumentException.class, () -> clasificador.clasificarPrioridad(
            -1,
            2.0,
            LocalDate.of(2026, 4, 24),
            LocalDate.of(2026, 4, 30)
        ));
    }
}
