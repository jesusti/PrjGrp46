package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.memory.InMemoryLocationDao;
import es.usc.enso.snachorapido.dao.memory.InMemoryMachineDao;
import es.usc.enso.snachorapido.dao.memory.InMemoryMachineInventoryDao;
import es.usc.enso.snachorapido.dao.memory.InMemoryProductDao;
import es.usc.enso.snachorapido.exception.EntityNotFoundException;
import es.usc.enso.snachorapido.model.Location;
import es.usc.enso.snachorapido.model.Machine;
import es.usc.enso.snachorapido.model.MachineInventoryItem;
import es.usc.enso.snachorapido.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    @DisplayName("Rechaza maquinas con localizacion desconocida")
    void debeRechazarMaquinaConLocalizacionDesconocida() {
        // Preparacion: la maquina referencia una localizacion que no fue cargada.
        CatalogLoadService service = new CatalogLoadService(
            new InMemoryLocationDao(),
            new InMemoryProductDao(),
            new InMemoryMachineDao(),
            new InMemoryMachineInventoryDao()
        );
        Machine machine = new Machine("M-1", "Maquina 1", new Location("LOC-1", "Campus", 42.88, -8.54));

        // Comprobacion: el servicio protege la coherencia entre maquina y localizacion.
        assertThrows(EntityNotFoundException.class, () -> service.registerMachine(machine));
    }

    @Test
    @DisplayName("Rechaza inventario si la maquina no existe")
    void debeRechazarInventarioParaMaquinaDesconocida() {
        // Preparacion: se crea el producto, pero no la maquina.
        InMemoryProductDao productDao = new InMemoryProductDao();
        productDao.save(new Product("P-1", "Agua", "Botella"));
        CatalogLoadService service = new CatalogLoadService(
            new InMemoryLocationDao(),
            productDao,
            new InMemoryMachineDao(),
            new InMemoryMachineInventoryDao()
        );

        // Comprobacion: no se puede cargar stock en una maquina inexistente.
        assertThrows(EntityNotFoundException.class, () -> service.addInventoryItem("M-1", "P-1", 10, 2.0));
    }

    @Test
    @DisplayName("Rechaza inventario si el producto no existe")
    void debeRechazarInventarioParaProductoDesconocido() {
        // Preparacion: la maquina existe, pero el producto no fue cargado.
        InMemoryLocationDao locationDao = new InMemoryLocationDao();
        InMemoryMachineDao machineDao = new InMemoryMachineDao();
        Location location = new Location("LOC-1", "Campus", 42.88, -8.54);
        Machine machine = new Machine("M-1", "Maquina 1", location);
        locationDao.save(location);
        machineDao.save(machine);
        CatalogLoadService service = new CatalogLoadService(
            locationDao,
            new InMemoryProductDao(),
            machineDao,
            new InMemoryMachineInventoryDao()
        );

        // Comprobacion: no se puede cargar stock de un producto inexistente.
        assertThrows(EntityNotFoundException.class, () -> service.addInventoryItem("M-1", "P-1", 10, 2.0));
    }

    @Test
    @DisplayName("Rechaza cargas de inventario con lineas de otra maquina")
    void debeRechazarCargaDeInventarioConMaquinaDistinta() {
        // Preparacion: el catalogo esta cargado para M-1, pero la linea pertenece a M-2.
        InMemoryLocationDao locationDao = new InMemoryLocationDao();
        InMemoryProductDao productDao = new InMemoryProductDao();
        InMemoryMachineDao machineDao = new InMemoryMachineDao();
        InMemoryMachineInventoryDao inventoryDao = new InMemoryMachineInventoryDao();
        CatalogLoadService service = new CatalogLoadService(locationDao, productDao, machineDao, inventoryDao);

        Location location = new Location("LOC-1", "Campus", 42.88, -8.54);
        Product product = new Product("P-1", "Agua", "Botella");
        service.registerLocation(location);
        service.registerProduct(product);
        service.registerMachine(new Machine("M-1", "Maquina 1", location));
        MachineInventoryItem item = new MachineInventoryItem("M-2", product, 10, 2.0);

        // Comprobacion: la carga masiva debe ser coherente con la maquina indicada.
        assertThrows(IllegalArgumentException.class, () -> service.loadMachineInventory("M-1", List.of(item)));
    }
}
