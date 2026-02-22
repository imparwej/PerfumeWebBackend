package com.perfumeweb.controller;

import com.perfumeweb.dto.*;
import com.perfumeweb.model.*;
import com.perfumeweb.service.OrderPdfService;
import com.perfumeweb.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderPdfService orderPdfService;

    // =========================
    // Helper: require auth
    // =========================
    private String requireUser(String email) {
        if (email == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized"
            );
        }
        return email;
    }

    // =========================
    // PLACE ORDER
    // =========================
    @PostMapping
    public OrderResponse placeOrder(
            @AuthenticationPrincipal String email,
            @RequestBody OrderRequest request
    ) {
        email = requireUser(email);
        Order order = orderService.placeOrder(email, request);
        return toDto(order);
    }

    // =========================
    // CANCEL ORDER (USER)
    // =========================
    @PatchMapping("/{id}/cancel")
    public OrderResponse cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal String email
    ) {
        email = requireUser(email);
        Order order = orderService.cancelOrder(id, email);
        return toDto(order);
    }

    // =========================
    // ADMIN UPDATE ORDER STATUS
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public OrderResponse updateOrderStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest request
    ) {

        Order order = orderService.updateOrderStatus(id, request.getStatus());

        return toDto(order);
    }

    // =========================
    // EXPORT PDF (USER)
    // =========================
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportOrdersPdf(
            @AuthenticationPrincipal String email
    ) throws Exception {

        email = requireUser(email);

        List<Order> orders =
                orderService.getOrdersByUser(email, Pageable.unpaged())
                        .getContent();

        byte[] pdf = orderPdfService.generateInvoice(orders);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // =========================
    // GET SINGLE ORDER (USER SECURE)
    // =========================
    @GetMapping("/{id}")
    public OrderResponse getOrderById(
            @AuthenticationPrincipal String email,
            @PathVariable Long id
    ) {
        email = requireUser(email);
        Order order = orderService.getOrderByIdSecure(id, email);
        return toDto(order);
    }

    // =========================
    // GET MY ORDERS (USER)
    // =========================
    @GetMapping("/my")
    public PaginatedResponse<OrderResponse> getMyOrders(
            @AuthenticationPrincipal String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        email = requireUser(email);

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orderPage =
                orderService.getOrdersByUser(email, pageable);

        List<OrderResponse> content =
                orderPage.getContent()
                        .stream()
                        .map(this::toDto)
                        .toList();

        return new PaginatedResponse<>(
                content,
                orderPage.getNumber(),
                orderPage.getTotalPages(),
                orderPage.getTotalElements()
        );
    }

    // =========================
    // DTO MAPPERS
    // =========================
    private OrderResponse toDto(Order order) {

        OrderResponse dto = new OrderResponse();

        dto.setId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setPaymentMethod(order.getPaymentMethod());

        dto.setShippingAddress(order.getShippingAddress());
        dto.setCity(order.getCity());
        dto.setPincode(order.getPincode());

        if (order.getOrderItems() != null) {
            dto.setItems(
                    order.getOrderItems()
                            .stream()
                            .map(this::toItemDto)
                            .toList()
            );
        }

        return dto;
    }

    private OrderResponse.OrderItemResponse toItemDto(OrderItem item) {

        OrderResponse.OrderItemResponse dto =
                new OrderResponse.OrderItemResponse();

        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setPerfume(toPerfumeDto(item.getPerfume()));

        return dto;
    }

    private PerfumeDto toPerfumeDto(Perfume perfume) {

        PerfumeDto dto = new PerfumeDto();

        dto.setId(perfume.getId());
        dto.setName(perfume.getName());
        dto.setDescription(perfume.getDescription());
        dto.setPrice(perfume.getPrice());
        dto.setImageUrl(perfume.getImageUrl());

        dto.setCategoryName(
                perfume.getCategory() != null
                        ? perfume.getCategory().getName()
                        : null
        );

        return dto;
    }
}