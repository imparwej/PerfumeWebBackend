package com.perfumeweb.repository;

import com.perfumeweb.model.Order;
import com.perfumeweb.model.OrderStatus;
import com.perfumeweb.model.User;

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
    // ADMIN FILTER BY DATE RANGE
    // =========================
    Page<Order> findByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    // =========================
    // ADMIN FILTER BY STATUS + DATE
    // =========================
    Page<Order> findByStatusAndCreatedAtBetween(
            OrderStatus status,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    // =========================
    // ADMIN: LATEST ORDERS
    // =========================
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // =====================================================
    // ================== REVENUE ANALYTICS =================
    // =====================================================

    // 🔥 TOTAL REVENUE (DELIVERED ONLY)
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.status = 'DELIVERED'
    """)
    BigDecimal getTotalRevenue();

    // 🔥 REVENUE SINCE DATE (DELIVERED ONLY)
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.createdAt >= :date
        AND o.status = 'DELIVERED'
    """)
    BigDecimal getRevenueSince(@Param("date") LocalDateTime date);

    // 🔥 CUSTOMER LIFETIME VALUE
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.user = :user
        AND o.status = 'DELIVERED'
    """)
    BigDecimal getUserLifetimeSpend(@Param("user") User user);

    // 🔥 CUSTOMER TOTAL ORDERS
    Long countByUser(User user);

    // 🔥 MONTHLY REVENUE (DELIVERED ONLY)
    @Query("""
        SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m'),
               COALESCE(SUM(o.totalAmount),0)
        FROM Order o
        WHERE o.status = 'DELIVERED'
        GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m')
        ORDER BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m')
    """)
    List<Object[]> getMonthlyRevenue();

    // 🔥 DAILY REVENUE (DELIVERED ONLY)
    @Query("""
        SELECT FUNCTION('DATE', o.createdAt),
               COALESCE(SUM(o.totalAmount),0)
        FROM Order o
        WHERE o.status = 'DELIVERED'
        GROUP BY FUNCTION('DATE', o.createdAt)
        ORDER BY FUNCTION('DATE', o.createdAt)
    """)
    List<Object[]> getDailyRevenue();

    // 🔥 WEEKLY REVENUE (DELIVERED ONLY)
    @Query("""
        SELECT FUNCTION('YEARWEEK', o.createdAt),
               COALESCE(SUM(o.totalAmount),0)
        FROM Order o
        WHERE o.status = 'DELIVERED'
        GROUP BY FUNCTION('YEARWEEK', o.createdAt)
        ORDER BY FUNCTION('YEARWEEK', o.createdAt)
    """)
    List<Object[]> getWeeklyRevenue();
}