package com.perfumeweb.repository;

import com.perfumeweb.model.Perfume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerfumeRepository extends JpaRepository<Perfume, Long> {

    //  Get all featured products
    List<Perfume> findByFeaturedTrue();

    //  Count featured products (for dashboard analytics)
    long countByFeaturedTrue();
}
