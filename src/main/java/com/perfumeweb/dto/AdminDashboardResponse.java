package com.perfumeweb.dto;

import java.math.BigDecimal;

public class AdminDashboardResponse {

    private Long totalUsers;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal last30DaysRevenue;
    private Long totalProducts;
    private Long featuredProducts;

    public Long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }

    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getLast30DaysRevenue() { return last30DaysRevenue; }
    public void setLast30DaysRevenue(BigDecimal last30DaysRevenue) { this.last30DaysRevenue = last30DaysRevenue; }

    public Long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(Long totalProducts) { this.totalProducts = totalProducts; }

    public Long getFeaturedProducts() { return featuredProducts; }
    public void setFeaturedProducts(Long featuredProducts) { this.featuredProducts = featuredProducts; }
}
