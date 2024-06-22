package com.unibuc.inventory.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "product")
public interface ProductServiceProxy {
    @DeleteMapping("/product/{barcode}/null-quantity")
    String deleteProductWhenEmptyInventory(@PathVariable String barcode);

    @GetMapping("/product/barcode/{barcode}/name")
    String getNameOfProductByProductBarcode(@PathVariable String barcode,
                                            @RequestHeader("unibuc-id") String correlationId);
}
