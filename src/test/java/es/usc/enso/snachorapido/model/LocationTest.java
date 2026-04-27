package es.usc.enso.snachorapido.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Probas unitarias de Location")
class LocationTest {

    @Test
    @DisplayName("Crea unha localizacion válida con coordenadas")
    void debeCrearLocalizacionValida() {
        // Establecemos unhas coordenadas
        Location loc = new Location(" LOC-1 ", " Campus Norte ", 45.8805, -8.5457);

        assertEquals("LOC-1", loc.getId());
        assertEquals("Campus Norte", loc.getName());
        assertEquals(45.8805, loc.getLatitude(), 0.0001);
        assertEquals(-8.5457, loc.getLongitude(), 0.0001);
    }

    @Test
    @DisplayName("Acepta os límites válidos de latitude e lonxitude")
    void debeAceptarLimitesDeCoordenadas() {
        
        Location suroeste = new Location("LOC-SW", "Límite suroeste", -90.0, -180.0);
        Location noreste = new Location("LOC-NE", "Límite noreste", 90.0, 180.0);

        assertEquals(-90.0, suroeste.getLatitude(), 0.0001);
        assertEquals(-180.0, suroeste.getLongitude(), 0.0001);
        assertEquals(90.0, noreste.getLatitude(), 0.0001);
        assertEquals(180.0, noreste.getLongitude(), 0.0001);
    }

    @Test
    @DisplayName("Rechaza textos obligatorios vacios")
    void debeRechazarCamposObligatoriosVacios() {
        // Sin id o nome da localización non é válida para asociala a máquinas.
        assertThrows(IllegalArgumentException.class, () -> new Location(" ", "Campus", 42.88, -8.54));
        assertThrows(IllegalArgumentException.class, () -> new Location("LOC-1", " ", 42.88, -8.54));
    }

    

    @Test
    @DisplayName("Compara localizacións por identificador")
    void debeCompararLocalizacionesPorIdentificador() {
        // Dúas localizacións co mesmo id representan a mesma entidade.
        Location loc = new Location("LOC-1", "Campus Norte", 42.88, -8.54);
        Location mesmoLoc = new Location("LOC-1", "Campus Sur", 42.87, -8.55);

        assertEquals(loc, mesmoLoc);
        assertEquals(loc.hashCode(), mesmoLoc.hashCode());
    }
    
    @Test
    @DisplayName("Rechaza coordenadas fora de rango")
    void debeRechazarCoordenadasFueraDeRango() {
        // A latitude debe estar en [-90, 90] e a lonxitude en [-180, 180].
        assertThrows(IllegalArgumentException.class, () -> new Location("LOC-1", "Campus", -90.1, -8.54));
        assertThrows(IllegalArgumentException.class, () -> new Location("LOC-1", "Campus", 90.1, -8.54));
        assertThrows(IllegalArgumentException.class, () -> new Location("LOC-1", "Campus", 42.88, -180.1));
        assertThrows(IllegalArgumentException.class, () -> new Location("LOC-1", "Campus", 42.88, 180.1));
    }
}
