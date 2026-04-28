package es.usc.enso.snachorapido.dao.memory;

import es.usc.enso.snachorapido.model.MachineInventoryItem;
import es.usc.enso.snachorapido.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pruebas unitarias de InMemoryMachineInventoryDao")
class InMemoryMachineInventoryDaoTest {

    @Test
    @DisplayName("Guarda y consulta stock por maquina y producto")
    void debeGuardarYConsultarStockPorMaquinaYProducto() {
        // Preparacion: una linea de inventario identifica maquina y producto.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        MachineInventoryItem item = new MachineInventoryItem(
            "M-1",
            new Product("P-1", "Agua", "Botella"),
            12,
            3.0
        );

        // Ejecucion.
        dao.save(item);

        // Comprobacion: el DAO permite las consultas que usan los servicios.
        assertEquals(1, dao.findAll().size());
        assertEquals(1, dao.findByMachineId("M-1").size());
        assertTrue(dao.findByMachineIdAndProductId("M-1", "P-1").isPresent());
        assertTrue(dao.findByMachineIdAndProductId("M-1", "P-2").isEmpty());
    }

    @Test
    @DisplayName("Permite el mismo producto en maquinas distintas")
    void debePermitirMismoProductoEnMaquinasDistintas() {
        // Preparacion: el duplicado solo aplica a la pareja maquina-producto.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        Product product = new Product("P-1", "Agua", "Botella");

        // Ejecucion.
        dao.save(new MachineInventoryItem("M-1", product, 12, 3.0));
        dao.save(new MachineInventoryItem("M-2", product, 8, 2.0));

        // Comprobacion.
        assertEquals(2, dao.findAll().size());
        assertEquals(1, dao.findByMachineId("M-1").size());
        assertEquals(1, dao.findByMachineId("M-2").size());
    }
}
