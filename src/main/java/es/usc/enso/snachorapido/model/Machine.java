package es.usc.enso.snachorapido.model;

import java.util.Objects;

public class Machine {

    private final String id;
    private final String name;
    private final Location location;

    public Machine(String id, String name, Location location) {
        this.id = validateText(id, "Machine id cannot be blank");
        this.name = validateText(name, "Machine name cannot be blank");
        this.location = validateLocation(location);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    private static String validateText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private static Location validateLocation(Location value) {
        if (value == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        return value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Machine machine)) {
            return false;
        }
        return Objects.equals(id, machine.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

