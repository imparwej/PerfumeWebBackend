package com.perfumeweb.dto;

import java.util.List;

public class RevenueResponse {

    private Double totalRevenue;
    private Double dailyRevenue;
    private Double weeklyRevenue;
    private Double monthlyRevenue;

    private List<RevenuePoint> dailyData;
    private List<RevenuePoint> weeklyData;
    private List<RevenuePoint> monthlyData;

    // =========================
    // INNER CLASS FOR GRAPH
    // =========================
    public static class RevenuePoint {

        private String label;
        private Double revenue;

        public RevenuePoint(String label, Double revenue) {
            this.label = label;
            this.revenue = revenue;
        }

        public String getLabel() {
            return label;
        }

        public Double getRevenue() {
            return revenue;
        }
    }

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Double getDailyRevenue() {
        return dailyRevenue;
    }

    public void setDailyRevenue(Double dailyRevenue) {
        this.dailyRevenue = dailyRevenue;
    }

    public Double getWeeklyRevenue() {
        return weeklyRevenue;
    }

    public void setWeeklyRevenue(Double weeklyRevenue) {
        this.weeklyRevenue = weeklyRevenue;
    }

    public Double getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(Double monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public List<RevenuePoint> getDailyData() {
        return dailyData;
    }

    public void setDailyData(List<RevenuePoint> dailyData) {
        this.dailyData = dailyData;
    }

    public List<RevenuePoint> getWeeklyData() {
        return weeklyData;
    }

    public void setWeeklyData(List<RevenuePoint> weeklyData) {
        this.weeklyData = weeklyData;
    }

    public List<RevenuePoint> getMonthlyData() {
        return monthlyData;
    }

    public void setMonthlyData(List<RevenuePoint> monthlyData) {
        this.monthlyData = monthlyData;
    }
}
