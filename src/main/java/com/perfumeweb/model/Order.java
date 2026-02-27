package com.perfumeweb.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= USER =================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ================= ORDER INFO =================
    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // ================= ADDRESS =================
    private String shippingAddress;
    private String city;
    private String pincode;

    // ================= TRACKING =================
    private String trackingNumber;
    private String courierName;

    // ================= TIMELINE =================
    private LocalDateTime packedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;

    // ================= ORDER ITEMS =================
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    // ================= AUTO DEFAULT =================
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = OrderStatus.PLACED;
        }
    }

    // =========================================================
    // ================= BUSINESS LOGIC =========================
    // =========================================================

    public void updateStatus(OrderStatus newStatus) {

        if (isFinalState()) {
            throw new IllegalStateException("Final state orders cannot be modified");
        }

        this.status = newStatus;

        switch (newStatus) {
            case SHIPPED -> this.shippedAt = LocalDateTime.now();
            case DELIVERED -> this.deliveredAt = LocalDateTime.now();
            case CANCELLED -> this.cancelledAt = LocalDateTime.now();
            default -> {}
        }
    }

    public boolean isFinalState() {
        return this.status == OrderStatus.CANCELLED
                || this.status == OrderStatus.DELIVERED;
    }

    public void markPaymentPaid() {
        this.paymentStatus = PaymentStatus.PAID;
    }

    public void markPaymentFailed() {
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public void markRefunded() {
        this.paymentStatus = PaymentStatus.REFUNDED;
    }

    public void addItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }

    // =========================================================
    // ================= GETTERS ================================
    // =========================================================

    public Long getId() { return id; }

    public User getUser() { return user; }

    public BigDecimal getTotalAmount() { return totalAmount; }

    public OrderStatus getStatus() { return status; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getShippingAddress() { return shippingAddress; }

    public String getCity() { return city; }

    public String getPincode() { return pincode; }

    public String getTrackingNumber() { return trackingNumber; }

    public String getCourierName() { return courierName; }

    public LocalDateTime getPackedAt() { return packedAt; }

    public LocalDateTime getShippedAt() { return shippedAt; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }

    public List<OrderItem> getOrderItems() { return orderItems; }

    // =========================================================
    // ================= SETTERS (COMPATIBILITY) ==============
    // =========================================================

    public void setUser(User user) { this.user = user; }

    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public void setCity(String city) { this.city = city; }

    public void setPincode(String pincode) { this.pincode = pincode; }

    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public void setCourierName(String courierName) { this.courierName = courierName; }

    // 👇 Compatibility setter
    public void setStatus(OrderStatus status) {
        updateStatus(status);
    }

    // 👇 Compatibility setter
    public void setOrderItems(List<OrderItem> items) {
        this.orderItems.clear();
        for (OrderItem item : items) {
            addItem(item);
        }
    }
}