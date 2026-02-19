package com.perfumeweb.service;

import com.perfumeweb.model.Perfume;
import com.perfumeweb.repository.PerfumeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class PerfumeService {

    private final PerfumeRepository perfumeRepository;

    public PerfumeService(PerfumeRepository perfumeRepository) {
        this.perfumeRepository = perfumeRepository;
    }

    // 🔹 Get all perfumes
    public Page<Perfume> getAllPerfumes(Pageable pageable) {
        return perfumeRepository.findAll(pageable);
    }

    // 🔹 Get perfume by ID
    public Optional<Perfume> getPerfumeById(Long id) {
        return perfumeRepository.findById(id);
    }

    // 🔹 Get featured perfumes
    public List<Perfume> getFeaturedPerfumes() {
        return perfumeRepository.findByFeaturedTrue();
    }

    // 🔹 Create perfume
    public Perfume savePerfume(Perfume perfume) {
        return perfumeRepository.save(perfume);
    }

    // 🔹 FULL SAFE UPDATE
    public Perfume updatePerfume(Long id, Perfume p) {

        Perfume existing = perfumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfume not found"));

        existing.setName(p.getName());
        existing.setDescription(p.getDescription());
        existing.setPrice(p.getPrice());
        existing.setImageUrl(p.getImageUrl());
        existing.setNotes(p.getNotes());
        existing.setSize(p.getSize());
        existing.setCategory(p.getCategory());
        existing.setFeatured(p.getFeatured()); // ⭐ featured fix

        return perfumeRepository.save(existing);
    }

    // 🔹 Delete
    public void deletePerfume(Long id) {
        perfumeRepository.deleteById(id);
    }
}
