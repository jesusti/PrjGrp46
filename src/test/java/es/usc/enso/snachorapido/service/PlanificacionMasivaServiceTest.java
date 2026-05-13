package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.memory.InMemoryMachineInventoryDao;
import es.usc.enso.snachorapido.model.MachineInventoryItem;
import es.usc.enso.snachorapido.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Sprint 2 - Rendimiento con assertTimeout")
class PlanificacionMasivaServiceTest {

    @Test
    @DisplayName("Genera una planificacion masiva en tiempo razonable")
    void debeContarPlanificacionMasivaEnTiempoRazonable() {
        // El volumen simula muchas maquinas y valida que el servicio sigue siendo rapido en memoria.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        for (int i = 0; i < 500; i++) {
            dao.save(new MachineInventoryItem(
                "M-" + i,
                new Product("P-" + i, "Producto " + i, "Producto de prueba"),
                1,
                1.0
            ));
        }
        PlanificacionMasivaService service = new PlanificacionMasivaService(dao);

        Long total = assertTimeout(Duration.ofMillis(500), () -> service.contarProductosAReponerHasta(
            LocalDate.of(2026, 4, 24),
            LocalDate.of(2026, 4, 24)
        ));

        assertEquals(500L, total);
    }

    @Test
    @DisplayName("Indica que no hay reposiciones si no existe stock")
    void debeIndicarQueNoHayReposicionesPendientesSinStock() {
        // Caso limite: sin inventario, el plan masivo no tiene productos pendientes.
        PlanificacionMasivaService service = new PlanificacionMasivaService(new InMemoryMachineInventoryDao());

        assertFalse(service.hayReposicionesPendientes(
            LocalDate.of(2026, 4, 24),
            LocalDate.of(2026, 4, 30)
        ));
    }

    @Test
    @DisplayName("Indica que hay reposiciones pendientes si algun producto vence")
    void debeIndicarQueHayReposicionesPendientes() {
        // Se valida la salida booleana publica a partir de un producto que vence en la ventana.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        dao.save(new MachineInventoryItem("M-1", new Product("P-1", "Agua", "Botella"), 1, 1.0));
        PlanificacionMasivaService service = new PlanificacionMasivaService(dao);

        assertTrue(service.hayReposicionesPendientes(
            LocalDate.of(2026, 4, 24),
            LocalDate.of(2026, 4, 24)
        ));
    }
}
