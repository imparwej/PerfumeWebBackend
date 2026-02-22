package com.perfumeweb.dto;

import com.perfumeweb.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminOrderResponse {

    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private String paymentMethod;
    private String city;
    private String pincode;
    private String userName;
    private String userEmail;

    public AdminOrderResponse(Order order) {
        this.id = order.getId();
        this.status = order.getStatus().name();
        this.totalAmount = order.getTotalAmount();
        this.createdAt = order.getCreatedAt();
        this.paymentMethod = order.getPaymentMethod();
        this.city = order.getCity();
        this.pincode = order.getPincode();

        if (order.getUser() != null) {
            this.userName = order.getUser().getName();
            this.userEmail = order.getUser().getEmail();
        }
    }

    // Getters

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getCity() { return city; }
    public String getPincode() { return pincode; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
}