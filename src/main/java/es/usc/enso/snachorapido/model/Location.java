package es.usc.enso.snachorapido.model;

import java.util.Objects;

public class Location {

    private final String id;
    private final String name;
    private final double latitude;
    private final double longitude;

    public Location(String id, String name, double latitude, double longitude) {
        this.id = validateText(id, "Location id cannot be blank");
        this.name = validateText(name, "Location name cannot be blank");
        this.latitude = validateLatitude(latitude);
        this.longitude = validateLongitude(longitude);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private static String validateText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private static double validateLatitude(double value) {
        if (value < -90.0 || value > 90.0) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        return value;
    }

    private static double validateLongitude(double value) {
        if (value < -180.0 || value > 180.0) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
        return value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Location location)) {
            return false;
        }
        return Objects.equals(id, location.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

