package com.unibuc.order.controller;

import com.unibuc.order.model.Order;
import com.unibuc.order.dto.OrderDTO;
import com.unibuc.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    private final ModelMapper modelMapper;

    public OrderController(OrderService orderService,
                           ModelMapper modelMapper) {
        this.orderService = orderService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @CircuitBreaker(name = "productFromInventoryByBarcode", fallbackMethod = "placeOrderFallback")
    public OrderDTO placeOrder(@RequestBody @Valid OrderDTO newOrder) {
        logger.info("placeOrder called with: {}", newOrder);
        Order order = orderService.placeOrder(modelMapper.map(newOrder, Order.class));
        return modelMapper.map(order, OrderDTO.class);
    }

    // if placing an order without starting inventory service or product service
    // the function placeOrder() will redirect to this function placeOrderFallback()
    // no changed in the inventory or product service
    OrderDTO placeOrderFallback(OrderDTO newOrder, Throwable throwable) {
        logger.error("placeOrderFallback called due to: {}", throwable.getMessage(), throwable);
        Order order = orderService.placeOrderInCaseOfError(modelMapper.map(newOrder, Order.class));
        return modelMapper.map(order, OrderDTO.class);
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
