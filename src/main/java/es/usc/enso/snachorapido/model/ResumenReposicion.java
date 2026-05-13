package es.usc.enso.snachorapido.model;

import java.time.LocalDate;
import java.util.Objects;

public class ResumenReposicion {

    private final String idMaquina;
    private final String idProducto;
    private final String nombreProducto;
    private final int cantidadActual;
    private final LocalDate fechaLimiteReposicion;

    public ResumenReposicion(
        String idMaquina,
        String idProducto,
        String nombreProducto,
        int cantidadActual,
        LocalDate fechaLimiteReposicion
    ) {
        this.idMaquina = validarTexto(idMaquina, "El id de maquina no puede estar vacio");
        this.idProducto = validarTexto(idProducto, "El id de producto no puede estar vacio");
        this.nombreProducto = validarTexto(nombreProducto, "El nombre de producto no puede estar vacio");
        this.cantidadActual = cantidadActual;
        this.fechaLimiteReposicion = Objects.requireNonNull(
            fechaLimiteReposicion,
            "La fecha limite de reposicion no puede ser nula"
        );
    }

    public String getIdMaquina() {
        return idMaquina;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidadActual() {
        return cantidadActual;
    }

    public LocalDate getFechaLimiteReposicion() {
        return fechaLimiteReposicion;
    }

    private static String validarTexto(String value, String message) {
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
        if (!(object instanceof ResumenReposicion other)) {
            return false;
        }
        return Objects.equals(idMaquina, other.idMaquina)
            && Objects.equals(idProducto, other.idProducto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMaquina, idProducto);
    }
}
