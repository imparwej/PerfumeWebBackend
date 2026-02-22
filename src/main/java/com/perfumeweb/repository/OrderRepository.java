package com.perfumeweb.repository;

import com.perfumeweb.model.Order;
import com.perfumeweb.model.OrderStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // =========================
    // USER ORDER HISTORY
    // =========================
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // =========================
    // ADMIN FILTER BY STATUS
    // =========================
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // =========================
    // ADMIN: LATEST ORDERS
    // =========================
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // =====================================================
    // ================== REVENUE ANALYTICS =================
    // =====================================================

    // 🔥 TOTAL REVENUE (ALL TIME)
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    BigDecimal getTotalRevenue();

    // 🔥 REVENUE SINCE DATE
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.createdAt >= :date")
    BigDecimal getRevenueSince(@Param("date") LocalDateTime date);

    // 🔥 MONTHLY REVENUE (YYYY-MM)
    @Query("""
        SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m'),
               COALESCE(SUM(o.totalAmount),0)
        FROM Order o
        GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m')
        ORDER BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m')
    """)
    List<Object[]> getMonthlyRevenue();

    // 🔥 DAILY REVENUE (YYYY-MM-DD)
    @Query("""
        SELECT FUNCTION('DATE', o.createdAt),
               COALESCE(SUM(o.totalAmount),0)
        FROM Order o
        GROUP BY FUNCTION('DATE', o.createdAt)
        ORDER BY FUNCTION('DATE', o.createdAt)
    """)
    List<Object[]> getDailyRevenue();

    // 🔥 WEEKLY REVENUE (YEAR-WEEK format)
    @Query("""
        SELECT FUNCTION('YEARWEEK', o.createdAt),
               COALESCE(SUM(o.totalAmount),0)
        FROM Order o
        GROUP BY FUNCTION('YEARWEEK', o.createdAt)
        ORDER BY FUNCTION('YEARWEEK', o.createdAt)
    """)
    List<Object[]> getWeeklyRevenue();
}
