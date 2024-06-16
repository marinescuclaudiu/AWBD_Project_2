package com.unibuc.order.service;

import com.unibuc.order.model.Order;
import com.unibuc.order.repository.OrderRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order placeOrder(Order order) {
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderDate(LocalDate.now());

        return orderRepository.save(order);
    }

    public Order findById(String id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);

        if (optionalOrder.isEmpty()) {
            throw new RuntimeException("The order with id " + id + " was not found");
        }

        return optionalOrder.get();
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findByOrderNumber(String orderNumber) {
        Optional<Order>optionalOrder = orderRepository.findByOrderNumber(orderNumber);

        if (optionalOrder.isEmpty()) {
            throw new RuntimeException("The order with number " + orderNumber + " was not found");
        }

        return optionalOrder.get();
    }
}
