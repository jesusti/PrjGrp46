package es.usc.enso.snachorapido.dao;

import es.usc.enso.snachorapido.model.Machine;

import java.util.List;
import java.util.Optional;

public interface MachineDao {

    void save(Machine machine);

    Optional<Machine> findById(String id);

    List<Machine> findAll();
}

