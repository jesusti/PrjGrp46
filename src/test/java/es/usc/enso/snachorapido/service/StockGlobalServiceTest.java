package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.memory.InMemoryMachineInventoryDao;
import es.usc.enso.snachorapido.model.MachineInventoryItem;
import es.usc.enso.snachorapido.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Sprint 2 - Caixa negra de stock global")
class StockGlobalServiceTest {

    @Test
    @DisplayName("Lista o stock completo de todas as máquinas ordenado")
    void debeListarStockCompletoOrdenadoPorMaquinaYProducto() {
        // Prepárase o sistema con varias máquinas para validar a saída observable do servizo.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        dao.save(new MachineInventoryItem("M-2", new Product("P-2", "Zumo", "Laranxa"), 7, 1.0));
        dao.save(new MachineInventoryItem("M-1", new Product("P-2", "Zumo", "Laranxa"), 4, 1.0));
        dao.save(new MachineInventoryItem("M-1", new Product("P-1", "Auga", "Botella"), 10, 2.0));

        StockGlobalService service = new StockGlobalService(dao);

        List<MachineInventoryItem> stock = service.obterStockCompleto();

        assertEquals(3, stock.size());
        assertEquals("M-1", stock.get(0).getMachineId());
        assertEquals("P-1", stock.get(0).getProduct().getId());
        assertEquals("M-1", stock.get(1).getMachineId());
        assertEquals("P-2", stock.get(1).getProduct().getId());
        assertEquals("M-2", stock.get(2).getMachineId());
    }

    @Test
    @DisplayName("Devolve a lista vacía se non existe stock cargado")
    void debeDevolverListaVaciaCuandoNoHayStock() {
        // Caso de caixa negra: sen datos de entrada cargados, a saída esperada é unha lista baleira.
        StockGlobalService service = new StockGlobalService(new InMemoryMachineInventoryDao());

        assertTrue(service.obterStockCompleto().isEmpty());
    }

    @Test
    @DisplayName("Filtra o stock dunha máquina concreta")
    void debeFiltrarStockPorMaquina() {
        // Compróbase que o filtro público non devolve productos de outras máquinas.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        dao.save(new MachineInventoryItem("M-1", new Product("P-1", "Auga", "Botella"), 10, 2.0));
        dao.save(new MachineInventoryItem("M-2", new Product("P-2", "Zumo", "Laranxa"), 7, 1.0));

        StockGlobalService service = new StockGlobalService(dao);

        List<MachineInventoryItem> stock = service.obterStockDeMaquina("M-1");

        assertEquals(1, stock.size());
        assertEquals("M-1", stock.get(0).getMachineId());
        assertEquals("P-1", stock.get(0).getProduct().getId());
    }
}
