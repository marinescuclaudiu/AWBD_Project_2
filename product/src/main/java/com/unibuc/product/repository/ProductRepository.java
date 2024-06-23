package com.unibuc.product.repository;

import com.unibuc.product.dto.ProductDTO;
import com.unibuc.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String>{
    List<Product> findByName(String productName);

    boolean existsByBarcode(String barcode);

    void deleteByBarcode(String barcode);

    Optional<Product> findByBarcode(String barcode);
}
