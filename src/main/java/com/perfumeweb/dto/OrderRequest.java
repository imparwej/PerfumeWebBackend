package com.perfumeweb.dto;

import java.util.List;

public class OrderRequest {

    private String paymentMethod;
    private List<OrderItemRequest> items;

    // shipping snapshot fields
    private String shippingAddress;
    private String city;
    private String pincode;

    // =========================
    // GETTERS & SETTERS
    // =========================

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    // =========================
    // INNER ITEM DTO
    // =========================
    public static class OrderItemRequest {

        private Long perfumeId;
        private int quantity;

        public Long getPerfumeId() {
            return perfumeId;
        }

        public void setPerfumeId(Long perfumeId) {
            this.perfumeId = perfumeId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
