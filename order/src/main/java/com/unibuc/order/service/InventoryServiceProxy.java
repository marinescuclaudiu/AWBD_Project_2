package com.unibuc.order.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(value = "inventory")
public interface InventoryServiceProxy {

    @GetMapping("/inventory/{sku-code}")
    boolean productIsInStockBySkuCode(@PathVariable("sku-code") String skuCode);

    @GetMapping("/inventory/{sku-code}/quantity")
    Integer getQuantityBySkuCode(@PathVariable("sku-code") String skuCode);

    @GetMapping("/inventory/retailer")
    Integer getRetailer();

    @PutMapping("/inventory/{sku-code}/reduce-quantity-by/{quantity}")
    Integer reduceQuantityByProductSkuCode(@PathVariable(name = "sku-code") String skuCode, @PathVariable int quantity);
}
