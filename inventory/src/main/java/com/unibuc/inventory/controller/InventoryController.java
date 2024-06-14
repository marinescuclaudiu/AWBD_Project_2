package com.unibuc.inventory.controller;

import com.unibuc.inventory.config.PropertiesConfig;
import com.unibuc.inventory.model.Inventory;
import com.unibuc.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    private final PropertiesConfig configuration;

    public InventoryController(InventoryService inventoryService, PropertiesConfig configuration) {
        this.inventoryService = inventoryService;
        this.configuration = configuration;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Inventory addToInventory(@RequestBody @Valid Inventory inventory){
        return inventoryService.save(inventory);
    }

    @GetMapping("/{sku-code}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable("sku-code") String skuCode) {
        return inventoryService.isInStock(skuCode);
    }

    @GetMapping("/{sku-code}/quantity")
    @ResponseStatus(HttpStatus.OK)
    public Integer getQuantityBySkuCode(@PathVariable("sku-code") String skuCode) {
        return inventoryService.getQuantityBySkuCode(skuCode);
    }

    @GetMapping("/retailer")
    public String getRetailer() {
        Inventory inventory = new Inventory(configuration.getRetailer(), configuration.getBrand());
        return inventory.getRetailer();
    }

    @PutMapping("/{sku-code}/reduce-quantity-by/{quantity}") // return remained quantity
    public Integer reduceQuantityOfProductBySkuCode(@PathVariable(name = "sku-code") String skuCode,
                                                    @PathVariable int quantity) {
        Inventory updatedInventory = inventoryService.reduceQuantityForProductByProductSkuCode(skuCode, quantity);

        return updatedInventory.getQuantity();
    }
}