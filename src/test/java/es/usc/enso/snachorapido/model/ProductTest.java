package es.usc.enso.snachorapido.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pruebas unitarias de Product")
class ProductTest {

    @Test
    @DisplayName("Crea un producto válido e normaliza os textos")
    void debeCrearProductoValidoConTextosNormalizados() {
        // Empréganse espazos para comprobar que o constructor limpa os textos.
        Product product = new Product(" P-1 ", " Auga ", "  Auga mineral sin gas  ");

        assertEquals("P-1", product.getId());
        assertEquals("Auga", product.getName());
        assertEquals("Auga mineral sin gas", product.getDescription());
    }

    @Test
    @DisplayName("Convirte unha descrición nula en texto vacio")
    void debeAceptarDescripcionNulaComoTextoVacio() {
        // A descrición é opcional no  dominio actual.
        Product product = new Product("P-1", "Auga", null);

        assertEquals("", product.getDescription());
    }

    @Test
    @DisplayName("Rechaza identificadores e nomes vacios")
    void debeRechazarCamposObligatoriosVacios() {
        // O id e o nome son obligatorios para poder usar o produto no catálogo.
        assertThrows(IllegalArgumentException.class, () -> new Product(" ", "Auga", "Botella"));
        assertThrows(IllegalArgumentException.class, () -> new Product("P-1", " ", "Botella"));
    }

    @Test
    @DisplayName("Considera iguais dous productos co mesmo identificador")
    void debeCompararProductosPorIdentificador() {
        // A identidade do producto depende do seu identificador.
        Product product = new Product("P-1", "Auga", "Botella pequena");
        Product mesmoProduct = new Product("P-1", "Auga fria", "Botella grande");

        assertEquals(product, mesmoProduct);
        assertEquals(product.hashCode(), mesmoProduct.hashCode());
        assertTrue(product.equals(product));
    }
}
