package es.usc.enso.snachorapido.dao;

import es.usc.enso.snachorapido.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {

    void save(Product product);

    Optional<Product> findById(String id);

    List<Product> findAll();
}

