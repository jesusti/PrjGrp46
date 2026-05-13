package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.memory.InMemoryMachineInventoryDao;
import es.usc.enso.snachorapido.model.MachineInventoryItem;
import es.usc.enso.snachorapido.model.Product;
import es.usc.enso.snachorapido.model.ReplenishmentNeed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Sprint 2 - Caja negra de plan de reposicion")
class PlanReposicionServiceTest {

    @Test
    @DisplayName("Genera un plan de reposicion para una fecha objetivo")
    void debeGenerarPlanDeReposicionParaUnaFechaObjetivo() {
        // Escenario de aceptacion: solo entran en el plan los productos que vencen hasta la fecha objetivo.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        dao.save(new MachineInventoryItem("M-1", new Product("P-1", "Agua", "Botella"), 2, 2.0));
        dao.save(new MachineInventoryItem("M-2", new Product("P-2", "Zumo", "Naranja"), 20, 1.0));
        PlanReposicionService service = new PlanReposicionService(dao);

        List<ReplenishmentNeed> plan = service.crearPlanReposicion(
            LocalDate.of(2026, 4, 24),
            LocalDate.of(2026, 4, 25)
        );

        assertEquals(1, plan.size());
        assertEquals("M-1", plan.get(0).getMachineId());
        assertEquals("P-1", plan.get(0).getProductId());
    }

    @Test
    @DisplayName("Ordena el plan por fecha limite de reposicion")
    void debeOrdenarPlanPorFechaLimite() {
        // Se comprueba una regla observable del plan: lo mas urgente aparece primero.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        dao.save(new MachineInventoryItem("M-1", new Product("P-1", "Agua", "Botella"), 10, 2.0));
        dao.save(new MachineInventoryItem("M-2", new Product("P-2", "Zumo", "Naranja"), 3, 3.0));
        PlanReposicionService service = new PlanReposicionService(dao);

        List<ReplenishmentNeed> plan = service.crearPlanReposicion(
            LocalDate.of(2026, 4, 24),
            LocalDate.of(2026, 4, 30)
        );

        assertEquals("P-2", plan.get(0).getProductId());
        assertEquals("P-1", plan.get(1).getProductId());
    }

    @Test
    @DisplayName("Excluye productos con stock suficiente hasta despues de la fecha objetivo")
    void debeExcluirProductosConStockSuficiente() {
        // Clase de equivalencia valida: un producto que se agota despues del horizonte no debe aparecer.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        dao.save(new MachineInventoryItem("M-1", new Product("P-1", "Agua", "Botella"), 30, 1.0));
        PlanReposicionService service = new PlanReposicionService(dao);

        List<ReplenishmentNeed> plan = service.crearPlanReposicion(
            LocalDate.of(2026, 4, 24),
            LocalDate.of(2026, 4, 30)
        );

        assertEquals(0, plan.size());
    }

    @Test
    @DisplayName("Rechaza una fecha objetivo anterior a la referencia")
    void debeRechazarFechaObjetivoAnteriorALaReferencia() {
        // Caso invalido de caja negra: la ventana de planificacion no puede ir hacia atras.
        PlanReposicionService service = new PlanReposicionService(new InMemoryMachineInventoryDao());

        assertThrows(IllegalArgumentException.class, () -> service.crearPlanReposicion(
            LocalDate.of(2026, 4, 24),
            LocalDate.of(2026, 4, 23)
        ));
    }
}
