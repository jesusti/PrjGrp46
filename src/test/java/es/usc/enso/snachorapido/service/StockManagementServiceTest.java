package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.memory.InMemoryMachineInventoryDao;
import es.usc.enso.snachorapido.exception.EntityNotFoundException;
import es.usc.enso.snachorapido.exception.InvalidOperationException;
import es.usc.enso.snachorapido.model.MachineInventoryItem;
import es.usc.enso.snachorapido.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Pruebas unitarias de StockManagementService")
class StockManagementServiceTest {

    @Test
    @DisplayName("Registra una venta y reduce el stock")
    void debeRegistrarVentaYReducirStock() {
        // Preparacion.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        dao.save(new MachineInventoryItem("M-1", new Product("P-1", "Agua", "Botella"), 10, 2.0));
        StockManagementService service = new StockManagementService(dao);

        // Ejecucion: una venta simulada consume unidades.
        MachineInventoryItem updatedItem = service.registerSale("M-1", "P-1", 3);

        // Comprobacion.
        assertEquals(7, updatedItem.getQuantity());
        assertEquals(7, dao.findByMachineIdAndProductId("M-1", "P-1").orElseThrow().getQuantity());
    }

    @Test
    @DisplayName("Repone unidades en el stock")
    void debeReponerProducto() {
        // Preparacion.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        dao.save(new MachineInventoryItem("M-1", new Product("P-1", "Agua", "Botella"), 10, 2.0));
        StockManagementService service = new StockManagementService(dao);

        // Ejecucion.
        MachineInventoryItem updatedItem = service.restockProduct("M-1", "P-1", 4);

        // Comprobacion.
        assertEquals(14, updatedItem.getQuantity());
    }

    @Test
    @DisplayName("Actualiza el consumo diario estimado")
    void debeActualizarConsumoDiarioEstimado() {
        // Preparacion.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        dao.save(new MachineInventoryItem("M-1", new Product("P-1", "Agua", "Botella"), 10, 2.0));
        StockManagementService service = new StockManagementService(dao);

        // Ejecucion.
        MachineInventoryItem updatedItem = service.updateEstimatedDailyConsumption("M-1", "P-1", 3.5);

        // Comprobacion.
        assertEquals(3.5, updatedItem.getEstimatedDailyConsumption(), 0.0001);
    }

    @Test
    @DisplayName("Rechaza operaciones sobre stock inexistente")
    void debeRechazarOperacionesSobreStockInexistente() {
        // Preparacion.
        StockManagementService service = new StockManagementService(new InMemoryMachineInventoryDao());

        // Comprobacion: el servicio avisa si la pareja maquina-producto no existe.
        assertThrows(EntityNotFoundException.class, () -> service.consumeProduct("M-1", "P-1", 1));
        assertThrows(EntityNotFoundException.class, () -> service.restockProduct("M-1", "P-1", 1));
        assertThrows(EntityNotFoundException.class, () -> service.updateEstimatedDailyConsumption("M-1", "P-1", 1.0));
    }

    @Test
    @DisplayName("Rechaza consumos y reposiciones invalidas")
    void debeRechazarOperacionesConUnidadesInvalidas() {
        // Preparacion.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        dao.save(new MachineInventoryItem("M-1", new Product("P-1", "Agua", "Botella"), 10, 2.0));
        StockManagementService service = new StockManagementService(dao);

        // Comprobacion: no hay ventas negativas, ventas de cero ni ventas por encima del stock.
        assertThrows(InvalidOperationException.class, () -> service.consumeProduct("M-1", "P-1", 0));
        assertThrows(InvalidOperationException.class, () -> service.consumeProduct("M-1", "P-1", -1));
        assertThrows(InvalidOperationException.class, () -> service.consumeProduct("M-1", "P-1", 11));
        assertThrows(InvalidOperationException.class, () -> service.restockProduct("M-1", "P-1", 0));
    }
}
