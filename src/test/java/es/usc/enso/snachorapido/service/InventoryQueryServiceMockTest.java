package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.MachineDao;
import es.usc.enso.snachorapido.dao.MachineInventoryDao;
import es.usc.enso.snachorapido.exception.EntityNotFoundException;
import es.usc.enso.snachorapido.model.Location;
import es.usc.enso.snachorapido.model.Machine;
import es.usc.enso.snachorapido.model.MachineInventoryItem;
import es.usc.enso.snachorapido.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de InventoryQueryService con Mockito")
class InventoryQueryServiceMockTest {

    @Mock
    private MachineDao machineDao;

    @Mock
    private MachineInventoryDao inventoryDao;

    @InjectMocks
    private InventoryQueryService service;

    private Machine buildMachine(String id) {
        return new Machine(id, "Maquina " + id, new Location("loc-" + id, "Localizacion " + id, 0.0, 0.0));
    }

    private MachineInventoryItem buildItem(String machineId, String productId) {
        return new MachineInventoryItem(machineId, new Product(productId, "Producto " + productId, ""), 5, 1.0);
    }

    @Test
    @DisplayName("Devuelve el inventario consultando los DAOs solo una vez")
    void devuelveElInventarioYConsultaLosDaosUnaSolaVez() {
        Machine machine = buildMachine("m1");
        MachineInventoryItem item = buildItem("m1", "p1");
        when(machineDao.findById("m1")).thenReturn(Optional.of(machine));
        when(inventoryDao.findByMachineId("m1")).thenReturn(List.of(item));

        List<MachineInventoryItem> result = service.getMachineInventory("m1");

        assertEquals(1, result.size());
        assertSame(item, result.get(0));
        verify(machineDao, times(1)).findById("m1");
        verify(inventoryDao, times(1)).findByMachineId("m1");
    }

    @Test
    @DisplayName("Si la maquina no existe no consulta el inventario")
    void siLaMaquinaNoExisteNoConsultaElInventario() {
        when(machineDao.findById("m-fantasma")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
            () -> service.getMachineInventory("m-fantasma"));

        verify(machineDao).findById("m-fantasma");
        verify(inventoryDao, never()).findByMachineId(anyString());
        verify(inventoryDao, never()).findByMachineIdAndProductId(anyString(), anyString());
    }

    @Test
    @DisplayName("Recupera el stock concreto cuando maquina y producto existen")
    void recuperaStockConcretoCuandoMaquinaYProductoExisten() {
        Machine machine = buildMachine("m1");
        MachineInventoryItem item = buildItem("m1", "p1");
        when(machineDao.findById("m1")).thenReturn(Optional.of(machine));
        when(inventoryDao.findByMachineIdAndProductId("m1", "p1")).thenReturn(Optional.of(item));

        MachineInventoryItem result = service.getMachineStock("m1", "p1");

        assertSame(item, result);
        verify(machineDao).findById("m1");
        verify(inventoryDao).findByMachineIdAndProductId("m1", "p1");
    }

    @Test
    @DisplayName("Lanza EntityNotFoundException si el producto no esta registrado en la maquina")
    void lanzaSiElProductoNoEstaRegistrado() {
        when(machineDao.findById("m1")).thenReturn(Optional.of(buildMachine("m1")));
        when(inventoryDao.findByMachineIdAndProductId("m1", "p-fantasma")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
            () -> service.getMachineStock("m1", "p-fantasma"));

        verify(inventoryDao).findByMachineIdAndProductId("m1", "p-fantasma");
    }

    @Test
    @DisplayName("Las interacciones se delegan en el orden y con los parametros esperados")
    void delegaEnElOrdenEsperado() {
        when(machineDao.findById(any())).thenReturn(Optional.of(buildMachine("m9")));
        when(inventoryDao.findByMachineId("m9")).thenReturn(List.of());

        service.getMachineInventory("m9");

        // Confirmamos que la consulta al inventario se hace despues de validar la existencia de la maquina
        var inOrder = org.mockito.Mockito.inOrder(machineDao, inventoryDao);
        inOrder.verify(machineDao).findById("m9");
        inOrder.verify(inventoryDao).findByMachineId("m9");
    }
}
