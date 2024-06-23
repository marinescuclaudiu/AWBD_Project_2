package com.unibuc.product.service;

import com.unibuc.product.dto.ProductDTO;
import com.unibuc.product.exception.DuplicateEntityException;
import com.unibuc.product.exception.ResourceNotFoundException;
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
        log.info("Checking if there is already another product with the same barcode: {}", product.getBarcode());

        if (productRepository.existsByBarcode(product.getBarcode())) {
            throw new DuplicateEntityException("A product with the same barcode is already present in the database");
        }

        log.info("Check passed successfully for barcode: {}", product.getBarcode());
        log.info("Saving product: {}", product.getName());
        log.info("Product saved: {}", product.getName());

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
            throw new ResourceNotFoundException(barcode);
        }

        productRepository.deleteByBarcode(barcode);
        log.info("Product deleted successfully");

        return true;
    }

    @Transactional
    public Product update(String barcode, Product newProduct) {
        log.info("Updating product with barcode: {}", barcode);
        Optional<Product> product = productRepository.findByBarcode(barcode);

        if (product.isEmpty()) {
            log.error("Product with barcode {} not found", barcode);
            throw new ResourceNotFoundException(barcode);
        }

        BeanUtils.copyProperties(newProduct, product.get(), BeanHelper.getNullPropertyNames(newProduct));

        productRepository.save(product.get());

        log.info("Product updated: {}", product.get().getName());
        return newProduct;
    }

    public List<ProductDTO> findProductByProductName(String productName) {
        log.info("Fetching product with name: {}", productName);
        List<Product> productsWithSameName = productRepository.findByName(productName);

        return productsWithSameName.stream().map(
                product -> modelMapper.map(product, ProductDTO.class)
        ).collect(Collectors.toList());
    }

    public void deleteProductWhenEmptyInventory(String barcode) {
        log.info("Deleting product with barcode: {}", barcode);

        if (productRepository.findByBarcode(barcode).isEmpty()) {
            log.error("Product with barcode {} not found", barcode);
            throw new ResourceNotFoundException("Product with barcode " + barcode + " not found");
        }

        productRepository.deleteByBarcode(barcode);
        log.info("Product deleted successfully");
    }

    public Product findProductByBarcode(String barcode) {
        log.info("Fetching product with barcode: {}", barcode);
        Optional<Product> optionalProduct = productRepository.findByBarcode(barcode);

        if (optionalProduct.isEmpty()) {
            log.error("Product with barcode {} not found", barcode);
            throw new ResourceNotFoundException("Product with barcode " + barcode + " not found");
        }

        log.info("Product found: {}", optionalProduct.get().getName());
        return optionalProduct.get();
    }

}
