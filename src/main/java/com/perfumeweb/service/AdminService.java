package com.perfumeweb.service;

import com.perfumeweb.dto.AdminDashboardResponse;
import com.perfumeweb.dto.RevenueResponse;
import com.perfumeweb.repository.OrderRepository;
import com.perfumeweb.repository.PerfumeRepository;
import com.perfumeweb.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PerfumeRepository perfumeRepository;

    public AdminService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        PerfumeRepository perfumeRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.perfumeRepository = perfumeRepository;
    }

    // ================= DASHBOARD =================
    public AdminDashboardResponse getDashboard() {

        AdminDashboardResponse res = new AdminDashboardResponse();

        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        BigDecimal last30Revenue =
                orderRepository.getRevenueSince(LocalDateTime.now().minusDays(30));

        res.setTotalUsers(userRepository.count());
        res.setTotalOrders(orderRepository.count());
        res.setTotalProducts(perfumeRepository.count());
        res.setFeaturedProducts(perfumeRepository.countByFeaturedTrue());

        res.setTotalRevenue(BigDecimal.valueOf(totalRevenue != null ? totalRevenue.doubleValue() : 0.0));
        res.setLast30DaysRevenue(BigDecimal.valueOf(last30Revenue != null ? last30Revenue.doubleValue() : 0.0));

        return res;
    }

    // ================= REVENUE ANALYTICS =================
    public RevenueResponse getRevenueAnalytics() {

        RevenueResponse response = new RevenueResponse();

        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        BigDecimal dailyRevenue =
                orderRepository.getRevenueSince(LocalDateTime.now().minusDays(1));
        BigDecimal weeklyRevenue =
                orderRepository.getRevenueSince(LocalDateTime.now().minusWeeks(1));
        BigDecimal monthlyRevenue =
                orderRepository.getRevenueSince(LocalDateTime.now().minusMonths(1));

        response.setTotalRevenue(totalRevenue != null ? totalRevenue.doubleValue() : 0.0);
        response.setDailyRevenue(dailyRevenue != null ? dailyRevenue.doubleValue() : 0.0);
        response.setWeeklyRevenue(weeklyRevenue != null ? weeklyRevenue.doubleValue() : 0.0);
        response.setMonthlyRevenue(monthlyRevenue != null ? monthlyRevenue.doubleValue() : 0.0);

        // ---- Daily Graph ----
        List<RevenueResponse.RevenuePoint> dailyData =
                orderRepository.getDailyRevenue()
                        .stream()
                        .map(r -> new RevenueResponse.RevenuePoint(
                                r[0].toString(),
                                r[1] != null
                                        ? ((BigDecimal) r[1]).doubleValue()
                                        : 0.0
                        ))
                        .collect(Collectors.toList());

        // ---- Weekly Graph ----
        List<RevenueResponse.RevenuePoint> weeklyData =
                orderRepository.getWeeklyRevenue()
                        .stream()
                        .map(r -> new RevenueResponse.RevenuePoint(
                                r[0].toString(),
                                r[1] != null
                                        ? ((BigDecimal) r[1]).doubleValue()
                                        : 0.0
                        ))
                        .collect(Collectors.toList());

        // ---- Monthly Graph ----
        List<RevenueResponse.RevenuePoint> monthlyData =
                orderRepository.getMonthlyRevenue()
                        .stream()
                        .map(r -> new RevenueResponse.RevenuePoint(
                                r[0].toString(),
                                r[1] != null
                                        ? ((BigDecimal) r[1]).doubleValue()
                                        : 0.0
                        ))
                        .collect(Collectors.toList());

        response.setDailyData(dailyData);
        response.setWeeklyData(weeklyData);
        response.setMonthlyData(monthlyData);

        return response;
    }
}
