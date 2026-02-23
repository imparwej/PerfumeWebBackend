package com.perfumeweb.controller;

import com.perfumeweb.dto.*;
import com.perfumeweb.model.Order;
import com.perfumeweb.model.OrderStatus;
import com.perfumeweb.repository.OrderRepository;
import com.perfumeweb.service.AdminService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final OrderRepository orderRepository;

    public AdminController(
            AdminService adminService,
            OrderRepository orderRepository
    ) {
        this.adminService = adminService;
        this.orderRepository = orderRepository;
    }

    // ===================== DASHBOARD =====================
    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard() {
        return adminService.getDashboard();
    }

    // ===================== REVENUE =====================
    @GetMapping("/revenue")
    public RevenueResponse getRevenue() {
        return adminService.getRevenueAnalytics();
    }

    // ===================== GET ALL ORDERS =====================
    @GetMapping("/orders")
    public Page<AdminOrderResponse> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(AdminOrderResponse::new);
    }

    // ===================== GET SINGLE ORDER DETAILS =====================
    @GetMapping("/orders/{id}")
    public AdminOrderDetailsResponse getOrderDetails(@PathVariable Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return new AdminOrderDetailsResponse(order);
    }

    // ===================== UPDATE ORDER STATUS =====================
    @PatchMapping("/orders/{id}/status")
    public Order updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔒 FINAL STATE LOCK
        if (order.isFinalState()) {
            throw new RuntimeException("Finalized orders cannot be modified");
        }

        // 🔄 VALID TRANSITIONS
        OrderStatus current = order.getStatus();

        if (!isValidTransition(current, status)) {
            throw new RuntimeException("Invalid status transition");
        }

        // 📌 UPDATE STATUS
        order.setStatus(status);

        // 🕒 TIMELINE AUTO UPDATE
        switch (status) {
            case SHIPPED -> order.markShipped();
            case DELIVERED -> order.markDelivered();
            case CANCELLED -> order.markCancelled();
        }

        return orderRepository.save(order);
    }

    // ===================== UPDATE TRACKING =====================
    @PatchMapping("/orders/{id}/tracking")
    public Order updateTracking(
            @PathVariable Long id,
            @RequestParam String trackingNumber,
            @RequestParam String courierName
    ) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.isFinalState()) {
            throw new RuntimeException("Cannot modify finalized order");
        }

        order.setTrackingNumber(trackingNumber);
        order.setCourierName(courierName);

        return orderRepository.save(order);
    }

    // ===================== STATUS TRANSITION RULES =====================
    private boolean isValidTransition(OrderStatus current, OrderStatus next) {

        if (current == OrderStatus.PLACED) {
            return next == OrderStatus.SHIPPED
                    || next == OrderStatus.CANCELLED;
        }

        if (current == OrderStatus.SHIPPED) {
            return next == OrderStatus.DELIVERED;
        }

        return false;
    }
}