package com.perfumeweb.dto;

import com.perfumeweb.model.OrderItem;

import java.math.BigDecimal;

public class OrderItemResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String image;

    public OrderItemResponse(OrderItem item) {
        this.id = item.getId();
        this.name = item.getPerfume().getName();
        this.price = item.getPrice();
        this.quantity = item.getQuantity();
        this.image = item.getPerfume().getImageUrl();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public String getImage() { return image; }
}