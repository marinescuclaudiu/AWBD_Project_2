package com.unibuc.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@Document(collection = "Orders")
public class Order {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "AWB is required")
    private String awb;

    private List<OrderProduct> products;

    @NotNull(message = "Address is required")
    private Address address;

    private LocalDate orderDate;

    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private Float totalAmount;
}
