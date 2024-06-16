package com.unibuc.product.exception;

public class ProductNotFoundException extends NotFoundException {
    public ProductNotFoundException(String barcodeOrName) {
        super("The product " + barcodeOrName + " was not found in the database");
    }
}
