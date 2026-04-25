package es.usc.enso.snachorapido.service;

import es.usc.enso.snachorapido.dao.LocationDao;
import es.usc.enso.snachorapido.dao.MachineDao;
import es.usc.enso.snachorapido.dao.MachineInventoryDao;
import es.usc.enso.snachorapido.dao.ProductDao;
import es.usc.enso.snachorapido.exception.EntityNotFoundException;
import es.usc.enso.snachorapido.model.Location;
import es.usc.enso.snachorapido.model.Machine;
import es.usc.enso.snachorapido.model.MachineInventoryItem;
import es.usc.enso.snachorapido.model.Product;

import java.util.List;

public class CatalogLoadService {

    private final LocationDao locationDao;
    private final ProductDao productDao;
    private final MachineDao machineDao;
    private final MachineInventoryDao machineInventoryDao;

    public CatalogLoadService(
        LocationDao locationDao,
        ProductDao productDao,
        MachineDao machineDao,
        MachineInventoryDao machineInventoryDao
    ) {
        this.locationDao = locationDao;
        this.productDao = productDao;
        this.machineDao = machineDao;
        this.machineInventoryDao = machineInventoryDao;
    }

    public void registerLocation(Location location) {
        locationDao.save(location);
    }

    public void loadLocations(List<Location> locations) {
        locations.forEach(this::registerLocation);
    }

    public void registerProduct(Product product) {
        productDao.save(product);
    }

    public void loadProducts(List<Product> products) {
        products.forEach(this::registerProduct);
    }

    public void registerMachine(Machine machine) {
        String locationId = machine.getLocation().getId();
        locationDao.findById(locationId)
            .orElseThrow(() -> new EntityNotFoundException("Location with id %s was not found".formatted(locationId)));
        machineDao.save(machine);
    }

    public void loadMachines(List<Machine> machines) {
        machines.forEach(this::registerMachine);
    }

    public void addInventoryItem(String machineId, String productId, int quantity, double estimatedDailyConsumption) {
        Machine machine = machineDao.findById(machineId)
            .orElseThrow(() -> new EntityNotFoundException("Machine with id %s was not found".formatted(machineId)));
        Product product = productDao.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product with id %s was not found".formatted(productId)));

        machineInventoryDao.save(new MachineInventoryItem(
            machine.getId(),
            product,
            quantity,
            estimatedDailyConsumption
        ));
    }

    public void loadMachineInventory(String machineId, List<MachineInventoryItem> stockItems) {
        machineDao.findById(machineId)
            .orElseThrow(() -> new EntityNotFoundException("Machine with id %s was not found".formatted(machineId)));

        stockItems.forEach(stockItem -> {
            if (!stockItem.getMachineId().equals(machineId)) {
                throw new IllegalArgumentException("All stock items must belong to machine %s".formatted(machineId));
            }
            productDao.findById(stockItem.getProduct().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                    "Product with id %s was not found".formatted(stockItem.getProduct().getId())
                ));
            machineInventoryDao.save(stockItem);
        });
    }
}

