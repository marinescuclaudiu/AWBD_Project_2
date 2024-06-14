package com.unibuc.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO extends RepresentationModel<OrderDTO> {
    private String username;
    private String orderNumber;
    private List<OrderProduct> orderedProducts;
    private Address address;
    private LocalDate orderDate;
    private String paymentMethod;
    private Float totalAmount;
}
