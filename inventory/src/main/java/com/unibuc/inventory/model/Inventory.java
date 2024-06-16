package com.unibuc.inventory.model;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Inventory")
public class Inventory {

    @Id
    Object _id;

    private String skuCode; // e.g. 5449000000897

    private String retailer;

    private String brand;

    @Min(value = 0, message = "The value must be positive")
    private Integer quantity;

    public Inventory(String retailer, String brand) {
        this.retailer = retailer;
        this.brand = brand;
    }
}