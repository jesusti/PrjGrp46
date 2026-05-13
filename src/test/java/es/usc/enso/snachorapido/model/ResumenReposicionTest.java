package es.usc.enso.snachorapido.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Sprint 2 - equals y hashCode de ResumenReposicion")
class ResumenReposicionTest {

    @Test
    @DisplayName("Considera iguales dos resumenes de la misma maquina y producto")
    void debeCompararResumenesPorMaquinaYProducto() {
        // La identidad del resumen depende de la pareja maquina-producto, no de la cantidad puntual.
        ResumenReposicion resumen = new ResumenReposicion(
            "M-1",
            "P-1",
            "Agua",
            4,
            LocalDate.of(2026, 4, 25)
        );
        ResumenReposicion mismoResumen = new ResumenReposicion(
            "M-1",
            "P-1",
            "Agua fria",
            10,
            LocalDate.of(2026, 4, 28)
        );

        assertEquals(resumen, mismoResumen);
    }

    @Test
    @DisplayName("Mantiene el contrato de hashCode para objetos iguales")
    void debeGenerarMismoHashCodeParaResumenesIguales() {
        // Si equals devuelve true, hashCode debe coincidir para usar el objeto en colecciones.
        ResumenReposicion resumen = new ResumenReposicion(
            "M-1",
            "P-1",
            "Agua",
            4,
            LocalDate.of(2026, 4, 25)
        );
        ResumenReposicion mismoResumen = new ResumenReposicion(
            "M-1",
            "P-1",
            "Agua",
            4,
            LocalDate.of(2026, 4, 25)
        );

        assertEquals(resumen.hashCode(), mismoResumen.hashCode());
    }

    @Test
    @DisplayName("Distingue resumenes de distinta maquina o producto")
    void debeDistinguirResumenesDeDistintaMaquinaOProducto() {
        // Mismo producto en distinta maquina representa otra necesidad de reposicion.
        ResumenReposicion resumen = new ResumenReposicion(
            "M-1",
            "P-1",
            "Agua",
            4,
            LocalDate.of(2026, 4, 25)
        );
        ResumenReposicion otraMaquina = new ResumenReposicion(
            "M-2",
            "P-1",
            "Agua",
            4,
            LocalDate.of(2026, 4, 25)
        );
        ResumenReposicion otroProducto = new ResumenReposicion(
            "M-1",
            "P-2",
            "Zumo",
            4,
            LocalDate.of(2026, 4, 25)
        );

        assertNotEquals(resumen, otraMaquina);
        assertNotEquals(resumen, otroProducto);
    }

    @Test
    @DisplayName("Rechaza datos obligatorios vacios")
    void debeRechazarDatosObligatoriosVacios() {
        // Validacion basica del objeto de salida del plan.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ResumenReposicion(
            " ",
            "P-1",
            "Agua",
            4,
            LocalDate.of(2026, 4, 25)
        ));

        assertEquals(IllegalArgumentException.class, exception.getClass());
    }
}
