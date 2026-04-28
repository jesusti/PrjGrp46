package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.memory.InMemoryMachineDao;
import es.usc.enso.snachorapido.dao.memory.InMemoryMachineInventoryDao;
import es.usc.enso.snachorapido.exception.EntityNotFoundException;
import es.usc.enso.snachorapido.model.Location;
import es.usc.enso.snachorapido.model.Machine;
import es.usc.enso.snachorapido.model.MachineInventoryItem;
import es.usc.enso.snachorapido.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Pruebas unitarias de InventoryQueryService")
class InventoryQueryServiceTest {

    @Test
    @DisplayName("Devuelve el inventario de una maquina existente")
    void debeDevolverInventarioDeMaquinaExistente() {
        // Preparacion: la maquina existe y tiene un producto cargado.
        InMemoryMachineDao machineDao = new InMemoryMachineDao();
        InMemoryMachineInventoryDao inventoryDao = new InMemoryMachineInventoryDao();
        machineDao.save(new Machine("M-1", "Maquina 1", new Location("LOC-1", "Campus", 42.88, -8.54)));
        inventoryDao.save(new MachineInventoryItem("M-1", new Product("P-1", "Agua", "Botella"), 10, 2.0));
        InventoryQueryService service = new InventoryQueryService(machineDao, inventoryDao);

        // Ejecucion y comprobacion: se puede consultar la lista y una linea concreta.
        assertEquals(1, service.getMachineInventory("M-1").size());
        assertEquals(10, service.getMachineStock("M-1", "P-1").getQuantity());
    }

    @Test
    @DisplayName("Rechaza consultas de maquinas desconocidas")
    void debeRechazarConsultaDeMaquinaDesconocida() {
        // Preparacion: DAOs vacios.
        InventoryQueryService service = new InventoryQueryService(new InMemoryMachineDao(), new InMemoryMachineInventoryDao());

        // Comprobacion: consultar una maquina inexistente es un error de dominio.
        assertThrows(EntityNotFoundException.class, () -> service.getMachineInventory("M-1"));
    }

    @Test
    @DisplayName("Rechaza consultas de productos no registrados en una maquina")
    void debeRechazarConsultaDeProductoNoRegistradoEnMaquina() {
        // Preparacion: la maquina existe pero no tiene el producto solicitado.
        InMemoryMachineDao machineDao = new InMemoryMachineDao();
        InMemoryMachineInventoryDao inventoryDao = new InMemoryMachineInventoryDao();
        machineDao.save(new Machine("M-1", "Maquina 1", new Location("LOC-1", "Campus", 42.88, -8.54)));
        InventoryQueryService service = new InventoryQueryService(machineDao, inventoryDao);

        // Comprobacion: el servicio distingue maquina inexistente de producto no cargado.
        assertThrows(EntityNotFoundException.class, () -> service.getMachineStock("M-1", "P-1"));
    }
}
