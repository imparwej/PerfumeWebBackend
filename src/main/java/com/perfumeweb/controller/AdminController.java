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

    // ===================== GET ALL ORDERS (PAGED) =====================
    @GetMapping("/orders")
    public Page<AdminOrderResponse> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(AdminOrderResponse::new);
    }

    // ===================== UPDATE ORDER STATUS =====================
    @PatchMapping("/orders/{id}/status")
    public Order updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        return orderRepository.save(order);
    }
}
