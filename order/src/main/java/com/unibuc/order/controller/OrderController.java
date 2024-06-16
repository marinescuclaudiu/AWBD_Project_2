package com.unibuc.order.controller;

import com.unibuc.order.model.Order;
import com.unibuc.order.model.OrderDTO;
import com.unibuc.order.model.OrderProduct;
import com.unibuc.order.service.InventoryServiceProxy;
import com.unibuc.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final InventoryServiceProxy inventoryServiceProxy;

    private final ModelMapper modelMapper;

    public OrderController(OrderService orderService, InventoryServiceProxy inventoryServiceProxy, ModelMapper modelMapper) {
        this.orderService = orderService;
        this.inventoryServiceProxy = inventoryServiceProxy;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @CircuitBreaker(name = "productFromInventoryByBarcode", fallbackMethod = "placeOrderFallback")
    public OrderDTO placeOrder(@RequestBody OrderDTO newOrder) {
        List<OrderProduct> orderItemList = newOrder.getOrderedProducts();

        for (OrderProduct item : orderItemList) {
            if (!inventoryServiceProxy.productIsInStockBySkuCode(item.getBarcode())) {
                throw new RuntimeException("The product " + item.getBarcode() + " is not in stock at the moment");
            }

            inventoryServiceProxy.reduceQuantityByProductSkuCode(item.getBarcode(), item.getQuantity());

            // calculate total amount

        }

        Order order = orderService.placeOrder(modelMapper.map(newOrder, Order.class));

        return modelMapper.map(order, OrderDTO.class);
    }

//    private OrderDTO convertToDto(Order order) {
//        OrderDTO dto = new OrderDTO();
//        dto.setUsername(order.getUsername());
//        dto.setOrderNumber(order.getOrderNumber());
//        dto.setOrderedProducts(order.getOrderedProducts());
//        dto.setAddress(order.getAddress());
//        dto.setOrderDate(order.getOrderDate());
//        dto.setPaymentMethod(order.getPaymentMethod());
//        dto.setTotalAmount(order.getTotalAmount());
//        return dto;
//    }
//
//    private Order convertToEntity(OrderDTO dto) {
//        Order order = new Order();
//        order.setUsername(dto.getUsername());
//        order.setOrderNumber(dto.getOrderNumber());
//        order.setOrderedProducts(dto.getOrderedProducts());
//        order.setAddress(dto.getAddress());
//        order.setOrderDate(dto.getOrderDate());
//        order.setPaymentMethod(dto.getPaymentMethod());
//        order.setTotalAmount(dto.getTotalAmount());
//        return order;
//    }

    // if placing an order without starting inventory service or any other issue from inventory service
    // the function placeOrder() will redirect to this function placeOrderFallback()
    // no changed in the inventory service
    Order placeOrderFallback(Order newOrder, Throwable throwable) {
        return orderService.placeOrder(newOrder);
    }

    @GetMapping("/{id}")
    public Order findById(@PathVariable String id) {
        return orderService.findById(id);
    }

    @GetMapping
    public CollectionModel<OrderDTO> findAll() {
        List<Order> orders = orderService.findAll();
        for (final Order currentOrder : orders) {
            OrderDTO currentOrderDTO = modelMapper.map(currentOrder, OrderDTO.class);

            Link selfLink = linkTo(methodOn(OrderController.class).findByOrderNumber(currentOrderDTO.getOrderNumber())).withSelfRel();
            currentOrderDTO.add(selfLink);

            Link postLink = linkTo(methodOn(OrderController.class).placeOrder(currentOrderDTO)).withRel("saveSubscription");
            currentOrderDTO.add(postLink);
        }

        Link link = linkTo(methodOn(OrderController.class).findAll()).withSelfRel();
        List<OrderDTO> orderDTOList = orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
        return CollectionModel.of(orderDTOList, link);
    }

    @GetMapping("/number/{order-number}")
    public OrderDTO findByOrderNumber(@PathVariable(name = "order-number") String orderNumber) {
        Order order = orderService.findByOrderNumber(orderNumber);
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

        Link selfLink = linkTo(methodOn(OrderController.class).findByOrderNumber(orderNumber)).withSelfRel();
        orderDTO.add(selfLink);

        return orderDTO;
    }
}
