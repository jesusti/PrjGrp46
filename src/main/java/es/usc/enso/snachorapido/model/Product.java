package es.usc.enso.snachorapido.model;

import java.util.Objects;

public class Product {

    private final String id;
    private final String name;
    private final String description;

    public Product(String id, String name, String description) {
        this.id = validateText(id, "Product id cannot be blank");
        this.name = validateText(name, "Product name cannot be blank");
        this.description = description == null ? "" : description.trim();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    private static String validateText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Product product)) {
            return false;
        }
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

