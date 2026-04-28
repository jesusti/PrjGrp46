package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.memory.InMemoryLocationDao;
import es.usc.enso.snachorapido.dao.memory.InMemoryMachineDao;
import es.usc.enso.snachorapido.dao.memory.InMemoryMachineInventoryDao;
import es.usc.enso.snachorapido.dao.memory.InMemoryProductDao;
import es.usc.enso.snachorapido.model.Location;
import es.usc.enso.snachorapido.model.Machine;
import es.usc.enso.snachorapido.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Pruebas unitarias de CatalogLoadService")
class CatalogLoadServiceTest {

    @Test
    @DisplayName("Carga localizaciones, productos, maquinas e inventario")
    void debeCargarCatalogoEInventario() {
        // Preparacion: DAOs vacios que simulan la base de datos en memoria.
        InMemoryLocationDao locationDao = new InMemoryLocationDao();
        InMemoryProductDao productDao = new InMemoryProductDao();
        InMemoryMachineDao machineDao = new InMemoryMachineDao();
        InMemoryMachineInventoryDao inventoryDao = new InMemoryMachineInventoryDao();
        CatalogLoadService service = new CatalogLoadService(locationDao, productDao, machineDao, inventoryDao);

        Location location = new Location("LOC-1", "Campus", 42.88, -8.54);
        Product product = new Product("P-1", "Agua", "Botella");
        Machine machine = new Machine("M-1", "Maquina 1", location);

        // Ejecucion: se carga primero el catalogo y luego el stock.
        service.loadLocations(List.of(location));
        service.loadProducts(List.of(product));
        service.loadMachines(List.of(machine));
        service.addInventoryItem("M-1", "P-1", 10, 2.0);

        // Comprobacion: todas las entidades quedan registradas.
        assertEquals(1, locationDao.findAll().size());
        assertEquals(1, productDao.findAll().size());
        assertEquals(1, machineDao.findAll().size());
        assertEquals(1, inventoryDao.findByMachineId("M-1").size());
    }
}
