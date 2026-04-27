package es.usc.enso.snachorapido.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Probas unitarias de Machine")
class MachineTest {

    @Test
    @DisplayName("Crea unha máquina válida asociada a unha localización")
    void debeCrearMaquinaValidaConLocalizacion() {
        // Toda máquina debe estar asociada a unha localización concreta.
        Location location = new Location("LOC-1", "Estacion", 42.88, -8.54);

        Machine machine = new Machine(" M-1 ", " Maquina bebidas ", location);

        assertEquals("M-1", machine.getId());
        assertEquals("Maquina bebidas", machine.getName());
        assertEquals(location, machine.getLocation());
    }



    @Test
    @DisplayName("Compara maquinas por identificador")
    void debeCompararMaquinasPorIdentificador() {
        // O id é a identidade da máquina.
        Location location = new Location("LOC-1", "Estacion", 42.88, -8.54);
        Machine machine = new Machine("M-1", "Maquina bebidas", location);
        Machine mesmaMachine = new Machine("M-1", "Maquina snacks", location);

        assertEquals(machine, mesmaMachine);
        assertEquals(machine.hashCode(), mesmaMachine.hashCode());
    }
}
