package com.unibuc.inventory.service;

import com.unibuc.inventory.exception.DuplicateEntityException;
import com.unibuc.inventory.exception.InsufficientStockException;
import com.unibuc.inventory.exception.ResourceNotFoundException;
import com.unibuc.inventory.model.Inventory;
import com.unibuc.inventory.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public Inventory save(Inventory inventory) {
        log.info("Check if the sku code is not present in the database: {}", inventory.getSkuCode());

        if (inventoryRepository.existsBySkuCode(inventory.getSkuCode())) {
            throw new DuplicateEntityException("There is already a product with the sku code " + inventory.getSkuCode() + " in the stock. Update that one.");
        }

        log.info("Check passed successfully!");
        log.info("Saving product with sku code: {}", inventory.getSkuCode());
        log.info("Product saved with sku code: {}", inventory.getSkuCode());
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory updateBySkuCode(Inventory updatedInventory) {
        String skucode = updatedInventory.getSkuCode();

        Optional<Inventory> optionalInventory = inventoryRepository.findBySkuCode(skucode);
        if (optionalInventory.isEmpty()) {
            throw new ResourceNotFoundException("There is no product with SKU code " + skucode);
        }

        Inventory existingInventory = optionalInventory.get();
        existingInventory.setRetailer(updatedInventory.getRetailer());
        existingInventory.setBrand(updatedInventory.getBrand());
        existingInventory.setQuantity(updatedInventory.getQuantity());

        return inventoryRepository.save(existingInventory);
    }

    public boolean isInStock(String skuCode) {
        log.info("Checking stock for product with sku code: {}", skuCode);
        boolean found = inventoryRepository.findBySkuCode(skuCode).isPresent();

        if(found){
            log.info("Product is in stock");
            return true;
        }

        log.info("Product is not in stock");
        return false;
    }

    public Integer getQuantityBySkuCode(String skuCode) {
        log.info("Fetching quantity for product with sku code: {}", skuCode);
        Optional<Inventory> inventoryOptional = inventoryRepository.findBySkuCode(skuCode);

        if (inventoryOptional.isPresent()) {
            log.info("Quantity found: {}", inventoryOptional.get().getQuantity());
            return inventoryOptional.get().getQuantity();
        }

        log.info("Quantity found: 0");
        return 0;
    }

    @Transactional
    public Inventory reduceQuantityForProductByProductSkuCode(String skuCode, int quantityToReduce) {
        log.info("Reducing quantity for product with sku code: {}", skuCode);
        Optional<Inventory> inventoryOptional = inventoryRepository.findBySkuCode(skuCode);

        if (inventoryOptional.isEmpty()) {
            log.error("The product with sku code {} is not on the stock.", skuCode);
            throw new ResourceNotFoundException("The product with sku code " + skuCode + " is not on the stock.");
        }

        if (quantityToReduce > inventoryOptional.get().getQuantity()) {
            log.error("Insufficient stock for the product with sku code: {}, requested: {}, available: {}", skuCode, quantityToReduce, inventoryOptional.get().getQuantity());
            throw new InsufficientStockException("Insufficient stock for the product with sku code: " + skuCode
                    + ", requested: " + quantityToReduce + ", available: " + inventoryOptional.get().getQuantity());
        }

        Inventory existingInventory = inventoryOptional.get();
        existingInventory.setQuantity(existingInventory.getQuantity() - quantityToReduce);

        inventoryRepository.save(existingInventory);
        log.info("Stock reduced successfully");
        return existingInventory;
    }

}