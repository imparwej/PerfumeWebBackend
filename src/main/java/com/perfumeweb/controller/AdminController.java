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

        // 🔒 FINAL STATE LOCK handled inside entity
        order.updateStatus(status);

        return orderRepository.save(order);
    }

    // ===================== UPDATE TRACKING =====================
    @PatchMapping("/orders/{id}/tracking")
    public Order updateTracking(
            @PathVariable Long id,
            @RequestBody TrackingUpdateRequest request
    ) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.isFinalState()) {
            throw new RuntimeException("Cannot modify finalized order");
        }

        order.setTrackingNumber(request.getTrackingNumber());
        order.setCourierName(request.getCourierName());

        return orderRepository.save(order);
    }

    // ===================== UPDATE PAYMENT STATUS =====================
    @PatchMapping("/orders/{id}/payment")
    public Order updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String paymentStatus
    ) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.markPaymentPaid(); // customize if needed

        return orderRepository.save(order);
    }
}