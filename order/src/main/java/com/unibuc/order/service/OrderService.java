package com.unibuc.order.service;

import com.unibuc.order.exception.ResourceNotFoundException;
import com.unibuc.order.model.Order;
import com.unibuc.order.model.OrderProduct;
import com.unibuc.order.model.Product;
import com.unibuc.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductServiceProxy productServiceProxy;

    private final InventoryServiceProxy inventoryServiceProxy;

    public OrderService(OrderRepository orderRepository,
                        ProductServiceProxy productServiceProxy,
                        InventoryServiceProxy inventoryServiceProxy) {
        this.orderRepository = orderRepository;
        this.productServiceProxy = productServiceProxy;
        this.inventoryServiceProxy = inventoryServiceProxy;
    }

    public Order placeOrder(Order order) {
        log.info("Placing the order for: {}", order.getUsername() );
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderDate(LocalDate.now());

        float finalPrice = 0;

        for (OrderProduct orderProduct: order.getOrderedProducts()) {
            Product currentProduct = productServiceProxy.findProductByBarcode(orderProduct.getBarcode());
            float price = currentProduct.getPrice();
            orderProduct.setPrice(price);

            if (!inventoryServiceProxy.productIsInStockBySkuCode(currentProduct.getBarcode())) {
                throw new RuntimeException("The product " + currentProduct.getBarcode() + " is not in stock at the moment.");
            } else {
                // calculate total amount for this order
                finalPrice += price * orderProduct.getQuantity();
                inventoryServiceProxy.reduceQuantityByProductSkuCode(orderProduct.getBarcode(), orderProduct.getQuantity());
            }
        }

        order.setTotalAmount(finalPrice);

        log.info("Order saved with number: {}", order.getOrderNumber());
        return orderRepository.save(order);
    }

    public Order placeOrderInCaseOfError(Order order) {
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderDate(LocalDate.now());
        return orderRepository.save(order);
    }

    public Order findById(String id) {
        log.info("Fetching order with id: {}", id);
        Optional<Order> optionalOrder = orderRepository.findById(id);

        if (optionalOrder.isEmpty()) {
            log.error("Order with id {} not found", id);
            throw new ResourceNotFoundException("The order with id " + id + " was not found");
        }

        log.info("Order found: {}", optionalOrder.get().get_id());
        return optionalOrder.get();
    }

    public List<Order> findAll() {
        log.info("Fetching all orders");
        return orderRepository.findAll();
    }

    public Order findByOrderNumber(String orderNumber) {
        log.info("Fetching order with number: {}", orderNumber);
        Optional<Order>optionalOrder = orderRepository.findByOrderNumber(orderNumber);

        if (optionalOrder.isEmpty()) {
            log.error("Order with number {} not found", orderNumber);
            throw new ResourceNotFoundException("The order with number " + orderNumber + " was not found");
        }

        log.info("Order found: {}", optionalOrder.get().getOrderNumber());
        return optionalOrder.get();
    }
}
