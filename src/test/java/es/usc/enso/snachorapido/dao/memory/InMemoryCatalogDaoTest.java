package es.usc.enso.snachorapido.dao.memory;

import es.usc.enso.snachorapido.exception.DuplicateEntityException;
import es.usc.enso.snachorapido.model.Location;
import es.usc.enso.snachorapido.model.Machine;
import es.usc.enso.snachorapido.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pruebas unitarias de los DAOs de catalogo en memoria")
class InMemoryCatalogDaoTest {

    @Test
    @DisplayName("Guarda y busca localizaciones")
    void debeGuardarYBuscarLocalizaciones() {
        // Preparacion: el DAO simula la persistencia del catalogo en memoria.
        InMemoryLocationDao dao = new InMemoryLocationDao();
        Location location = new Location("LOC-1", "Campus", 42.88, -8.54);

        // Ejecucion.
        dao.save(location);

        // Comprobacion: la localizacion se puede recuperar por id y en listado.
        assertEquals(1, dao.findAll().size());
        assertTrue(dao.findById("LOC-1").isPresent());
        assertTrue(dao.findById("LOC-2").isEmpty());
    }

    @Test
    @DisplayName("Rechaza localizaciones duplicadas")
    void debeRechazarLocalizacionesDuplicadas() {
        // Preparacion.
        InMemoryLocationDao dao = new InMemoryLocationDao();
        Location location = new Location("LOC-1", "Campus", 42.88, -8.54);
        dao.save(location);

        // Comprobacion: no puede haber dos localizaciones con el mismo id.
        assertThrows(DuplicateEntityException.class, () -> dao.save(location));
    }

    @Test
    @DisplayName("Guarda y busca productos")
    void debeGuardarYBuscarProductos() {
        // Preparacion.
        InMemoryProductDao dao = new InMemoryProductDao();
        Product product = new Product("P-1", "Agua", "Botella");

        // Ejecucion.
        dao.save(product);

        // Comprobacion: el catalogo mantiene el producto en memoria.
        assertEquals(1, dao.findAll().size());
        assertTrue(dao.findById("P-1").isPresent());
        assertTrue(dao.findById("P-2").isEmpty());
    }

    @Test
    @DisplayName("Rechaza productos duplicados")
    void debeRechazarProductosDuplicados() {
        // Preparacion.
        InMemoryProductDao dao = new InMemoryProductDao();
        Product product = new Product("P-1", "Agua", "Botella");
        dao.save(product);

        // Comprobacion: el identificador del producto es unico.
        assertThrows(DuplicateEntityException.class, () -> dao.save(product));
    }

    @Test
    @DisplayName("Guarda y busca maquinas")
    void debeGuardarYBuscarMaquinas() {
        // Preparacion.
        InMemoryMachineDao dao = new InMemoryMachineDao();
        Machine machine = new Machine("M-1", "Maquina 1", new Location("LOC-1", "Campus", 42.88, -8.54));

        // Ejecucion.
        dao.save(machine);

        // Comprobacion: la maquina queda disponible para consultas de inventario.
        assertEquals(1, dao.findAll().size());
        assertTrue(dao.findById("M-1").isPresent());
        assertTrue(dao.findById("M-2").isEmpty());
    }

    @Test
    @DisplayName("Rechaza maquinas duplicadas")
    void debeRechazarMaquinasDuplicadas() {
        // Preparacion.
        InMemoryMachineDao dao = new InMemoryMachineDao();
        Machine machine = new Machine("M-1", "Maquina 1", new Location("LOC-1", "Campus", 42.88, -8.54));
        dao.save(machine);

        // Comprobacion: el identificador de maquina es unico.
        assertThrows(DuplicateEntityException.class, () -> dao.save(machine));
    }
}
