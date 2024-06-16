package com.unibuc.inventory.service;

import com.unibuc.inventory.helper.BeanHelper;
import com.unibuc.inventory.model.Inventory;
import com.unibuc.inventory.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public boolean isInStock(String skuCode) {
        return inventoryRepository.findBySkuCode(skuCode).isPresent();
    }

    public Integer getQuantityBySkuCode(String skuCode) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findBySkuCode(skuCode);

        if (inventoryOptional.isPresent()) {
            return inventoryOptional.get().getQuantity();
        }

        return 0;
    }

    @Transactional
    public Inventory reduceQuantityForProductByProductSkuCode(String skuCode, int quantityToReduce) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findBySkuCode(skuCode);

        if (inventoryOptional.isEmpty()) {
            throw new RuntimeException("The product with sku code " + skuCode + " is not on the stock.");
        }

        if (quantityToReduce > inventoryOptional.get().getQuantity()) {
            throw new RuntimeException("This quantity is not available for the product with sku code: " + skuCode);
        }

        Inventory existingInventory = inventoryOptional.get();
        existingInventory.setQuantity(existingInventory.getQuantity() - quantityToReduce);

        inventoryRepository.save(existingInventory);

        return existingInventory;
    }

}