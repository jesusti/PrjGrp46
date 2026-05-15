package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.MachineInventoryDao;
import es.usc.enso.snachorapido.exception.EntityNotFoundException;
import es.usc.enso.snachorapido.exception.InvalidOperationException;
import es.usc.enso.snachorapido.model.MachineInventoryItem;
import es.usc.enso.snachorapido.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de StockManagementService con Mockito")
class StockManagementServiceMockTest {

    @Mock
    private MachineInventoryDao inventoryDao;

    @InjectMocks
    private StockManagementService service;

    private MachineInventoryItem item;

    @BeforeEach
    void setUp() {
        item = new MachineInventoryItem("m1", new Product("p1", "Cola", ""), 10, 1.0);
    }

    @Test
    @DisplayName("Consumir unidades reduce el stock cuando hay existencias suficientes")
    void consumirUnidadesReduceElStock() {
        when(inventoryDao.findByMachineIdAndProductId("m1", "p1")).thenReturn(Optional.of(item));

        MachineInventoryItem result = service.consumeProduct("m1", "p1", 3);

        assertEquals(7, result.getQuantity());
        verify(inventoryDao).findByMachineIdAndProductId("m1", "p1");
    }

    @Test
    @DisplayName("registerSale delega exactamente igual que consumeProduct")
    void registerSaleDelegaIgualQueConsumeProduct() {
        when(inventoryDao.findByMachineIdAndProductId("m1", "p1")).thenReturn(Optional.of(item));

        service.registerSale("m1", "p1", 2);

        assertEquals(8, item.getQuantity());
        // Captura del argumento para confirmar que se busca exactamente esta combinacion
        ArgumentCaptor<String> machineIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> productIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(inventoryDao).findByMachineIdAndProductId(machineIdCaptor.capture(), productIdCaptor.capture());
        assertEquals("m1", machineIdCaptor.getValue());
        assertEquals("p1", productIdCaptor.getValue());
    }

    @Test
    @DisplayName("Repone unidades aumentando el stock")
    void reponeUnidadesAumentandoElStock() {
        when(inventoryDao.findByMachineIdAndProductId("m1", "p1")).thenReturn(Optional.of(item));

        MachineInventoryItem result = service.restockProduct("m1", "p1", 5);

        assertEquals(15, result.getQuantity());
    }

    @Test
    @DisplayName("Lanza EntityNotFoundException si la linea de inventario no existe")
    void lanzaSiLaLineaDeInventarioNoExiste() {
        when(inventoryDao.findByMachineIdAndProductId(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
            () -> service.consumeProduct("m-fantasma", "p-fantasma", 1));
    }

    @Test
    @DisplayName("Rechaza consumir mas unidades de las disponibles")
    void rechazaConsumirMasUnidadesDeLasDisponibles() {
        when(inventoryDao.findByMachineIdAndProductId("m1", "p1")).thenReturn(Optional.of(item));

        assertThrows(InvalidOperationException.class,
            () -> service.consumeProduct("m1", "p1", 50));

        // El stock no se modifica si la operacion es invalida
        assertEquals(10, item.getQuantity());
    }

    @Test
    @DisplayName("Actualiza el consumo diario estimado sin cambiar la cantidad")
    void actualizaElConsumoSinCambiarCantidad() {
        when(inventoryDao.findByMachineIdAndProductId("m1", "p1")).thenReturn(Optional.of(item));

        MachineInventoryItem result = service.updateEstimatedDailyConsumption("m1", "p1", 2.5);

        assertEquals(2.5, result.getEstimatedDailyConsumption());
        assertEquals(10, result.getQuantity());
    }
}
