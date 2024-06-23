package com.unibuc.inventory.controller;

import com.unibuc.inventory.config.PropertiesConfig;
import com.unibuc.inventory.model.Inventory;
import com.unibuc.inventory.service.InventoryService;
import com.unibuc.inventory.service.ProductServiceProxy;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    private final PropertiesConfig configuration;
    private final ProductServiceProxy productServiceProxy;

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    public InventoryController(InventoryService inventoryService, PropertiesConfig configuration, ProductServiceProxy productServiceProxy) {
        this.inventoryService = inventoryService;
        this.configuration = configuration;
        this.productServiceProxy = productServiceProxy;
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

    @GetMapping("/barcode/{barcode}")
    public String getNameOfProductByProductBarcode(@PathVariable String barcode,
                                            @RequestHeader("unibuc-id") String correlationId) {
        logger.info("correlation-id inventory: {}", correlationId);
        return productServiceProxy.getNameOfProductByProductBarcode(barcode, correlationId);
    }

    @PutMapping("/{sku-code}/reduce-quantity-by/{quantity}") // return remained quantity
    public Integer reduceQuantityOfProductBySkuCode(@PathVariable(name = "sku-code") String skuCode,
                                                    @PathVariable int quantity) {
        Inventory updatedInventory = inventoryService.reduceQuantityForProductByProductSkuCode(skuCode, quantity);

        if (updatedInventory.getQuantity() == 0) {
            System.out.println("test here");
            productServiceProxy.deleteProductWhenEmptyInventory(skuCode);
        }

        return updatedInventory.getQuantity();
    }

    @PutMapping("/update")
    public Inventory updateBySkuCode(@RequestBody @Valid Inventory newInventory) {
        return inventoryService.updateBySkuCode(newInventory);
    }
}