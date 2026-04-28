package es.usc.enso.snachorapido.integration;

import es.usc.enso.snachorapido.dao.memory.InMemoryLocationDao;
import es.usc.enso.snachorapido.dao.memory.InMemoryMachineDao;
import es.usc.enso.snachorapido.dao.memory.InMemoryMachineInventoryDao;
import es.usc.enso.snachorapido.dao.memory.InMemoryProductDao;
import es.usc.enso.snachorapido.model.Location;
import es.usc.enso.snachorapido.model.Machine;
import es.usc.enso.snachorapido.model.Product;
import es.usc.enso.snachorapido.model.ReplenishmentNeed;
import es.usc.enso.snachorapido.service.CatalogLoadService;
import es.usc.enso.snachorapido.service.InventoryQueryService;
import es.usc.enso.snachorapido.service.ReplenishmentService;
import es.usc.enso.snachorapido.service.StockManagementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Prueba de aceptacion del Sprint 1")
class Sprint1AcceptanceTest {

    @Test
    @DisplayName("Cubre carga, consulta, venta simulada y deteccion de reposicion")
    void debeCubrirEscenarioPrincipalDeAceptacionSprint1() {
        // Preparacion: se montan todos los DAOs en memoria que pide la memoria del Sprint 1.
        InMemoryLocationDao locationDao = new InMemoryLocationDao();
        InMemoryProductDao productDao = new InMemoryProductDao();
        InMemoryMachineDao machineDao = new InMemoryMachineDao();
        InMemoryMachineInventoryDao inventoryDao = new InMemoryMachineInventoryDao();

        CatalogLoadService loadService = new CatalogLoadService(locationDao, productDao, machineDao, inventoryDao);
        InventoryQueryService queryService = new InventoryQueryService(machineDao, inventoryDao);
        StockManagementService stockService = new StockManagementService(inventoryDao);
        ReplenishmentService replenishmentService = new ReplenishmentService(inventoryDao);

        Location location = new Location("LOC-1", "Campus", 42.88, -8.54);
        Product water = new Product("P-1", "Agua", "Botella");
        Product juice = new Product("P-2", "Zumo", "Naranja");
        Machine machine = new Machine("M-1", "Maquina 1", location);

        // Ejecucion: se carga el catalogo completo y el stock inicial de la maquina.
        loadService.loadLocations(List.of(location));
        loadService.loadProducts(List.of(water, juice));
        loadService.loadMachines(List.of(machine));
        loadService.addInventoryItem("M-1", "P-1", 6, 3.0);
        loadService.addInventoryItem("M-1", "P-2", 20, 2.0);

        // Comprobacion: la maquina tiene los dos productos cargados.
        assertEquals(2, queryService.getMachineInventory("M-1").size());

        // Ejecucion: se simula una venta de agua.
        stockService.registerSale("M-1", "P-1", 3);

        // Comprobacion: el stock baja y la reposicion se detecta con fecha limite correcta.
        assertEquals(3, queryService.getMachineStock("M-1", "P-1").getQuantity());
        List<ReplenishmentNeed> needs = replenishmentService.getProductsNeedingReplenishment(LocalDate.of(2026, 4, 24));
        assertEquals(1, needs.size());
        assertEquals("P-1", needs.getFirst().getProductId());
        assertEquals(LocalDate.of(2026, 4, 24), needs.getFirst().getLatestReplenishmentDate());
    }
}
