package com.unibuc.order.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class OrderProduct {

    @NotBlank(message = "Barcode name is required")
    private String barcode;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private Float price;
}
