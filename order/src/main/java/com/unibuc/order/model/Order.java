package com.unibuc.order.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Orders")
public class Order{

    @Id
    ObjectId _id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "orderNumber is required")
    @Indexed(unique = true)
    private String orderNumber;

    private List<OrderProduct> orderedProducts;

    private Address address;

    private LocalDate orderDate;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    private Float totalAmount;
}
