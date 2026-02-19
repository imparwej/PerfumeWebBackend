package com.perfumeweb.repository;

import com.perfumeweb.model.Order;
import com.perfumeweb.model.OrderStatus;
import com.perfumeweb.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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

}
