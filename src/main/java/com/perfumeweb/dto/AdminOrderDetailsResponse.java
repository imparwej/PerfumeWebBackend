package com.perfumeweb.dto;

import com.perfumeweb.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AdminOrderDetailsResponse {

    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private String paymentMethod;

    // Address
    private String shippingAddress;
    private String city;
    private String pincode;

    // User Info
    private String userName;
    private String userEmail;

    // Tracking
    private String trackingNumber;
    private String courierName;

    // Timeline
    private LocalDateTime packedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;

    // Final State
    private boolean finalState;

    private List<OrderItemResponse> items;

    public AdminOrderDetailsResponse(Order order) {

        this.id = order.getId();
        this.status = order.getStatus().name();
        this.totalAmount = order.getTotalAmount();
        this.createdAt = order.getCreatedAt();
        this.paymentMethod = order.getPaymentMethod();

        // Address
        this.shippingAddress = order.getShippingAddress();
        this.city = order.getCity();
        this.pincode = order.getPincode();

        // User
        if (order.getUser() != null) {
            this.userName = order.getUser().getName();
            this.userEmail = order.getUser().getEmail();
        }

        // Tracking
        this.trackingNumber = order.getTrackingNumber();
        this.courierName = order.getCourierName();

        // Timeline
        this.packedAt = order.getPackedAt();
        this.shippedAt = order.getShippedAt();
        this.deliveredAt = order.getDeliveredAt();
        this.cancelledAt = order.getCancelledAt();

        // Final State
        this.finalState = order.isFinalState();

        // Items
        this.items = order.getOrderItems()
                .stream()
                .map(OrderItemResponse::new)
                .collect(Collectors.toList());
    }

    // ================= GETTERS =================

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getPaymentMethod() { return paymentMethod; }

    public String getShippingAddress() { return shippingAddress; }
    public String getCity() { return city; }
    public String getPincode() { return pincode; }

    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }

    public String getTrackingNumber() { return trackingNumber; }
    public String getCourierName() { return courierName; }

    public LocalDateTime getPackedAt() { return packedAt; }
    public LocalDateTime getShippedAt() { return shippedAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }

    public boolean isFinalState() { return finalState; }

    public List<OrderItemResponse> getItems() { return items; }
}