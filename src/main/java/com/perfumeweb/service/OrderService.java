package com.perfumeweb.service;

import com.perfumeweb.dto.OrderRequest;
import com.perfumeweb.model.*;
import com.perfumeweb.repository.OrderRepository;
import com.perfumeweb.repository.PerfumeRepository;
import com.perfumeweb.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PerfumeRepository perfumeRepository;

    // =========================
    // PLACE ORDER
    // =========================
    public Order placeOrder(String email, OrderRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingAddress(request.getShippingAddress());
        order.setCity(request.getCity());
        order.setPincode(request.getPincode());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {

            Perfume perfume = perfumeRepository.findById(itemReq.getPerfumeId())
                    .orElseThrow(() -> new RuntimeException("Perfume not found"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setPerfume(perfume);
            item.setQuantity(itemReq.getQuantity());

            BigDecimal itemTotal =
                    perfume.getPrice()
                            .multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            item.setPrice(itemTotal);

            orderItems.add(item);
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }

    // =========================
    // CANCEL ORDER
    // =========================
    public Order cancelOrder(Long id, String email) {

        Order order = getOrderByIdSecure(id, email);

        if (order.getStatus() == OrderStatus.DELIVERED)
            throw new RuntimeException("Delivered order cannot be cancelled");

        if (order.getStatus() == OrderStatus.CANCELLED)
            throw new RuntimeException("Already cancelled");

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    // =========================
// GET USER ORDERS
// =========================
    public Page<Order> getOrdersByUser(String email, Pageable pageable) {
        System.out.println("Fetching orders for user: " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("User ID = " + user.getId());

        return orderRepository.findByUserIdOrderByCreatedAtDesc(
                user.getId(),  // ✅ FIX
                pageable
        );
    }

    // =========================
    // GET ORDER SECURE
    // =========================
    public Order getOrderByIdSecure(Long id, String email) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getEmail().equals(email))
            throw new RuntimeException("Unauthorized access");

        return order;
    }
}
