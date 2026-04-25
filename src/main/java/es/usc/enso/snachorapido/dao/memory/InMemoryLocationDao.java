package es.usc.enso.snachorapido.dao.memory;

import es.usc.enso.snachorapido.dao.LocationDao;
import es.usc.enso.snachorapido.exception.DuplicateEntityException;
import es.usc.enso.snachorapido.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryLocationDao implements LocationDao {

    private final List<Location> locations = new ArrayList<>();

    @Override
    public void save(Location location) {
        if (findById(location.getId()).isPresent()) {
            throw new DuplicateEntityException("Location with id %s already exists".formatted(location.getId()));
        }
        locations.add(location);
    }

    @Override
    public Optional<Location> findById(String id) {
        return locations.stream()
            .filter(location -> location.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<Location> findAll() {
        return List.copyOf(locations);
    }
}

