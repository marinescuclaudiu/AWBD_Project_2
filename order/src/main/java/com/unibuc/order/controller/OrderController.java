package com.unibuc.order.controller;

import com.unibuc.order.model.Order;
import com.unibuc.order.dto.OrderDTO;
import com.unibuc.order.model.OrderProduct;
import com.unibuc.order.service.InventoryServiceProxy;
import com.unibuc.order.service.OrderService;
import com.unibuc.order.service.ProductServiceProxy;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
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

    private final ProductServiceProxy productServiceProxy;

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final ModelMapper modelMapper;

    public OrderController(OrderService orderService,
                           InventoryServiceProxy inventoryServiceProxy,
                           ProductServiceProxy productServiceProxy,
                           ModelMapper modelMapper) {
        this.orderService = orderService;
        this.inventoryServiceProxy = inventoryServiceProxy;
        this.productServiceProxy = productServiceProxy;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @CircuitBreaker(name = "productFromInventoryByBarcode", fallbackMethod = "placeOrderFallback")
    public OrderDTO placeOrder(@RequestBody OrderDTO newOrder) {
        List<OrderProduct> orderItemList = newOrder.getOrderedProducts();

        float finalPrice = 0;

        for (OrderProduct item : orderItemList) {
            if (!inventoryServiceProxy.productIsInStockBySkuCode(item.getBarcode())) {
                throw new RuntimeException("The product " + item.getBarcode() + " is not in stock at the moment");
            }

            // calculate total amount for this order
            float price = productServiceProxy.findProductByBarcode(item.getBarcode()).getPrice();
            finalPrice += price * item.getQuantity();

            inventoryServiceProxy.reduceQuantityByProductSkuCode(item.getBarcode(), item.getQuantity());
        }

//        logger.info("correlation-id subscription: {}", correlationId);

        newOrder.setTotalAmount(finalPrice);
        Order order = orderService.placeOrder(modelMapper.map(newOrder, Order.class));

        return modelMapper.map(order, OrderDTO.class);
    }

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
