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

    // =====================================================
    // ================= USER QUERIES ======================
    // =====================================================

    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Long countByUser(User user);

    // =====================================================
    // ================= ADMIN FILTERS =====================
    // =====================================================

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    Page<Order> findByStatusAndCreatedAtBetween(
            OrderStatus status,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // =====================================================
    // ================= REVENUE ANALYTICS =================
    // =====================================================

    // 🔥 TOTAL REVENUE (DELIVERED + PAID ONLY)
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.status = 'DELIVERED'
        AND o.paymentStatus = 'PAID'
    """)
    BigDecimal getTotalRevenue();

    // 🔥 REVENUE SINCE DATE (DELIVERED + PAID ONLY)
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.createdAt >= :date
        AND o.status = 'DELIVERED'
        AND o.paymentStatus = 'PAID'
    """)
    BigDecimal getRevenueSince(@Param("date") LocalDateTime date);

    // 🔥 CUSTOMER LIFETIME VALUE (DELIVERED + PAID ONLY)
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.user = :user
        AND o.status = 'DELIVERED'
        AND o.paymentStatus = 'PAID'
    """)
    BigDecimal getUserLifetimeSpend(@Param("user") User user);

    // 🔥 MONTHLY REVENUE (DELIVERED + PAID ONLY)
    @Query("""
        SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m'),
               COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.status = 'DELIVERED'
        AND o.paymentStatus = 'PAID'
        GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m')
        ORDER BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m')
    """)
    List<Object[]> getMonthlyRevenue();

    // 🔥 DAILY REVENUE (DELIVERED + PAID ONLY)
    @Query("""
        SELECT FUNCTION('DATE', o.createdAt),
               COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.status = 'DELIVERED'
        AND o.paymentStatus = 'PAID'
        GROUP BY FUNCTION('DATE', o.createdAt)
        ORDER BY FUNCTION('DATE', o.createdAt)
    """)
    List<Object[]> getDailyRevenue();

    // 🔥 WEEKLY REVENUE (DELIVERED + PAID ONLY)
    @Query("""
        SELECT FUNCTION('YEARWEEK', o.createdAt),
               COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.status = 'DELIVERED'
        AND o.paymentStatus = 'PAID'
        GROUP BY FUNCTION('YEARWEEK', o.createdAt)
        ORDER BY FUNCTION('YEARWEEK', o.createdAt)
    """)
    List<Object[]> getWeeklyRevenue();
}