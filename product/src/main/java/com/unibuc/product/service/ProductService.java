package com.unibuc.product.service;

import com.unibuc.product.dto.ProductDTO;
import com.unibuc.product.exception.ProductNotFoundException;
import com.unibuc.product.helper.BeanHelper;
import com.unibuc.product.model.Product;
import com.unibuc.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public Product save(Product product) {
        log.info("Saving product: {}", product.getName());
        return productRepository.save(product);
    }

    public List<ProductDTO> findAll() {
        log.info("Fetching all products");
        List<Product> products = productRepository.findAll();

        productRepository.findAll().iterator().forEachRemaining(products::add);

        log.info("Found {} products", products.size());
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteByBarcode(String barcode) {
        log.info("Deleting product by barcode: {}", barcode);

        if (!productRepository.existsByBarcode(barcode)) {
            log.error("Product with barcode {} not found", barcode);
            throw new ProductNotFoundException(barcode);
        }

        productRepository.deleteByBarcode(barcode);
        log.info("Product deleted successfully");

        return true;
    }

    @Transactional
    public Product update(String barcode, Product newProduct) {
        log.info("Updating product with ID: {}", barcode);
        Optional<Product> product = productRepository.findByBarcode(barcode);

        if (product.isEmpty()) {
            log.error("Product with barcode {} not found", barcode);
            throw new ProductNotFoundException(barcode);
        }

        BeanUtils.copyProperties(newProduct, product.get(), BeanHelper.getNullPropertyNames(newProduct));

        productRepository.save(product.get());

        log.info("Product updated: {}", product.get().getName());
        return newProduct;
    }

    public Product findProductByProductName(String productName) {
        return productRepository.findByName(productName);
    }

    public void deleteProductWhenEmptyInventory(String barcode) {
        if (productRepository.findByBarcode(barcode).isEmpty()) {
            log.error("Product with barcode {} not found", barcode);
            throw new ProductNotFoundException(barcode);
        }

        productRepository.deleteByBarcode(barcode);
    }

    public Product findProductByBarcode(String barcode) {
        Optional<Product> optionalProduct = productRepository.findByBarcode(barcode);

        if (optionalProduct.isEmpty()) {
            throw new ProductNotFoundException(barcode);
        }

        return optionalProduct.get();
    }

}
