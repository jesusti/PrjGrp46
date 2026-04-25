package es.usc.enso.snachorapido.dao.memory;

import es.usc.enso.snachorapido.dao.MachineDao;
import es.usc.enso.snachorapido.exception.DuplicateEntityException;
import es.usc.enso.snachorapido.model.Machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryMachineDao implements MachineDao {

    private final List<Machine> machines = new ArrayList<>();

    @Override
    public void save(Machine machine) {
        if (findById(machine.getId()).isPresent()) {
            throw new DuplicateEntityException("Machine with id %s already exists".formatted(machine.getId()));
        }
        machines.add(machine);
    }

    @Override
    public Optional<Machine> findById(String id) {
        return machines.stream()
            .filter(machine -> machine.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<Machine> findAll() {
        return List.copyOf(machines);
    }
}

