package es.usc.enso.snachorapido.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Probas unitarias de ReplenishmentNeed")
class ReplenishmentNeedTest {

    @Test
    @DisplayName("Indica se a reposición debe facerse antes ou nunha fecha dada")
    void debeIndicarSiLaReposicionVenceEnLaFechaConsultada() {
        // Fecha límite calculada polo servizo de reposición.
        ReplenishmentNeed reposicion = new ReplenishmentNeed(
            "M-1",
            "P-1",
            "Auga",
            3,
            3.0,
            LocalDate.of(2026, 4, 25),
            LocalDate.of(2026, 4, 24)
        );

        // Comprobación: antes da fecha limite non vence; na fecha límite si.
        assertFalse(reposicion.mustBeReplenishedBy(LocalDate.of(2026, 4, 23)));
        assertTrue(reposicion.mustBeReplenishedBy(LocalDate.of(2026, 4, 24)));
        assertTrue(reposicion.mustBeReplenishedBy(LocalDate.of(2026, 4, 25)));
    }
}
