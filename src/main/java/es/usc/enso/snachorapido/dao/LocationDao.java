package es.usc.enso.snachorapido.dao;

import es.usc.enso.snachorapido.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationDao {

    void save(Location location);

    Optional<Location> findById(String id);

    List<Location> findAll();
}

