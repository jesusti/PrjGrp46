package es.usc.enso.snachorapido.model;

import es.usc.enso.snachorapido.exception.InvalidOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Probas unitarias de MachineInventoryItem")
class MachineInventoryItemTest {

    private final Product product = new Product("P-1", "Auga", "Botella");

    @Test
    @DisplayName("Crea unha linha de inventario válida")
    void debeCrearLineaDeInventarioValida() {
        // O stock pertence a unha máquina e a un producto.
        MachineInventoryItem item = new MachineInventoryItem(" M-1 ", product, 10, 2.5);

        assertEquals("M-1", item.getMachineId());
        assertEquals(product, item.getProduct());
        assertEquals(10, item.getQuantity());
        assertEquals(2.5, item.getEstimatedDailyConsumption(), 0.0001);
    }

    @Test
    @DisplayName("Permite repor unidades")
    void debeSumarUnidadesAlStock() {
        // Unha reposición aumenta o stock dispoñible.
        MachineInventoryItem item = new MachineInventoryItem("M-1", product, 10, 2.5);

        item.addUnits(5);

        assertEquals(15, item.getQuantity());
    }

    @Test
    @DisplayName("Permite consumir unidades disponhibles")
    void debeConsumirUnidadesDisponibles() {
        // Unha venta reduce o stock.
        MachineInventoryItem item = new MachineInventoryItem("M-1", product, 10, 2.5);

        item.consumeUnits(4);

        assertEquals(6, item.getQuantity());
    }

    @Test
    @DisplayName("Permite actualizar a velocidade estimada de consumo")
    void debeActualizarConsumoDiarioEstimado() {
        // O dato pódese axustar cando cambia a estimación do consumo.
        MachineInventoryItem item = new MachineInventoryItem("M-1", product, 10, 2.5);

        item.setEstimatedDailyConsumption(3.75);

        assertEquals(3.75, item.getEstimatedDailyConsumption(), 0.0001);
    }
    
    @Test
    @DisplayName("Rechaza datos iniciais inválidos")
    void debeRechazarDatosInicialesInvalidos() {
        // A linha de inventario debe ter datos mínimos consistentes.
        assertThrows(IllegalArgumentException.class, () -> new MachineInventoryItem(" ", product, 10, 2.5));
        assertThrows(IllegalArgumentException.class, () -> new MachineInventoryItem("M-1", null, 10, 2.5));
        assertThrows(IllegalArgumentException.class, () -> new MachineInventoryItem("M-1", product, -1, 2.5));
        assertThrows(IllegalArgumentException.class, () -> new MachineInventoryItem("M-1", product, 10, 0));
        assertThrows(IllegalArgumentException.class, () -> new MachineInventoryItem("M-1", product, 10, -0.1));
    }

    @Test
    @DisplayName("Rechaza operacións de stock inválidas")
    void debeRechazarOperacionesDeStockInvalidas() {
        // Non se permiten cantidades non positivas nin ventas por encima do stock.
        MachineInventoryItem item = new MachineInventoryItem("M-1", product, 10, 2.5);

        assertThrows(InvalidOperationException.class, () -> item.addUnits(0));
        assertThrows(InvalidOperationException.class, () -> item.addUnits(-1));
        assertThrows(InvalidOperationException.class, () -> item.consumeUnits(0));
        assertThrows(InvalidOperationException.class, () -> item.consumeUnits(-1));
        assertThrows(InvalidOperationException.class, () -> item.consumeUnits(11));
    }

}
