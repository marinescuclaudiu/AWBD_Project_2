package com.unibuc.inventory.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "product")
public interface ProductServiceProxy {
    @DeleteMapping("/product/{barcode}/null-quantity")
    String deleteProductWhenEmptyInventory(@PathVariable String barcode);
}
