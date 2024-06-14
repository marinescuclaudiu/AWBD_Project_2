package com.unibuc.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Inventory {
    private String skuCode; // e.g. 5449000000897

    private String retailer;

    private String brand;

    private Integer quantity;

    private String versionId;
}