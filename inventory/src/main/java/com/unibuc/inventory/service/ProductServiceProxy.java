package com.unibuc.inventory.service;

import com.unibuc.inventory.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "product")
public interface ProductServiceProxy {

    @GetMapping("/product/barcode/{barcode}")
    Product findProductByBarcode(@PathVariable String barcode);

    @DeleteMapping("/product/{barcode}/null-quantity")
    String deleteProductWhenEmptyInventory(@PathVariable String barcode);
}
