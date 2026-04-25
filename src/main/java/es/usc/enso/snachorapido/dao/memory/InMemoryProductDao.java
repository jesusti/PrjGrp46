package es.usc.enso.snachorapido.dao.memory;

import es.usc.enso.snachorapido.dao.ProductDao;
import es.usc.enso.snachorapido.exception.DuplicateEntityException;
import es.usc.enso.snachorapido.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryProductDao implements ProductDao {

    private final List<Product> products = new ArrayList<>();

    @Override
    public void save(Product product) {
        if (findById(product.getId()).isPresent()) {
            throw new DuplicateEntityException("Product with id %s already exists".formatted(product.getId()));
        }
        products.add(product);
    }

    @Override
    public Optional<Product> findById(String id) {
        return products.stream()
            .filter(product -> product.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(products);
    }
}

