package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.memory.InMemoryMachineInventoryDao;
import es.usc.enso.snachorapido.model.MachineInventoryItem;
import es.usc.enso.snachorapido.model.Product;
import es.usc.enso.snachorapido.model.ReplenishmentNeed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Pruebas unitarias de ReplenishmentService")
class ReplenishmentServiceTest {

    @Test
    @DisplayName("Calcula fecha prevista de agotamiento y fecha limite de reposicion")
    void debeCalcularFechaDeAgotamientoYFechaLimite() {
        // Preparacion: 10 unidades con consumo 4/dia se agotan en ceil(10/4) = 3 dias.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        dao.save(new MachineInventoryItem("M-1", new Product("P-1", "Agua", "Botella"), 10, 4.0));
        ReplenishmentService service = new ReplenishmentService(dao);

        // Ejecucion.
        ReplenishmentNeed need = service.getReplenishmentNeeds(LocalDate.of(2026, 4, 24)).getFirst();

        // Comprobacion: se debe reponer como maximo el dia anterior al agotamiento.
        assertEquals(LocalDate.of(2026, 4, 27), need.getPredictedDepletionDate());
        assertEquals(LocalDate.of(2026, 4, 26), need.getLatestReplenishmentDate());
    }

    @Test
    @DisplayName("Devuelve solo productos que necesitan reposicion en la fecha de referencia")
    void debeDevolverSoloProductosQueNecesitanReposicion() {
        // Preparacion: P-1 vence pronto, P-2 tiene stock suficiente para mas dias.
        InMemoryMachineInventoryDao dao = new InMemoryMachineInventoryDao();
        dao.save(new MachineInventoryItem("M-1", new Product("P-1", "Agua", "Botella"), 2, 2.0));
        dao.save(new MachineInventoryItem("M-1", new Product("P-2", "Zumo", "Naranja"), 10, 1.0));
        ReplenishmentService service = new ReplenishmentService(dao);

        // Ejecucion.
        List<ReplenishmentNeed> needs = service.getProductsNeedingReplenishment(LocalDate.of(2026, 4, 25));

        // Comprobacion.
        assertEquals(1, needs.size());
        assertEquals("P-1", needs.getFirst().getProductId());
    }
}
