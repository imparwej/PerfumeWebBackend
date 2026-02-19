package com.perfumeweb.controller;

import com.perfumeweb.dto.PerfumeDto;
import com.perfumeweb.model.Perfume;
import com.perfumeweb.service.PerfumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/perfumes")
@CrossOrigin(origins = "http://localhost:3000")
public class PerfumeController {

    @Autowired
    private PerfumeService perfumeService;

    // ✅ LIST PERFUMES (pagination + sorting handled by Spring)
    @GetMapping
    public Page<PerfumeDto> getAllPerfumes(Pageable pageable) {
        return perfumeService.getAllPerfumes(pageable)
                .map(this::toDto);
    }

    // ✅ SINGLE PERFUME
    @GetMapping("/{id}")
    public PerfumeDto getPerfumeById(@PathVariable Long id) {
        Perfume perfume = perfumeService.getPerfumeById(id)
                .orElseThrow(() -> new RuntimeException("Perfume not found"));
        return toDto(perfume);
    }

    // DTO mapper
    private PerfumeDto toDto(Perfume perfume) {
        PerfumeDto dto = new PerfumeDto();
        dto.setId(perfume.getId());
        dto.setName(perfume.getName());
        dto.setDescription(perfume.getDescription());
        dto.setPrice(perfume.getPrice());
        dto.setImageUrl(perfume.getImageUrl());
        dto.setCategoryName(
                perfume.getCategory() != null ? perfume.getCategory().getName() : null
        );
        dto.setNotes(perfume.getNotes());
        dto.setSize(perfume.getSize());
        return dto;
    }
    @GetMapping("/featured")
    public List<PerfumeDto> getFeaturedPerfumes() {
        return perfumeService.getFeaturedPerfumes()
                .stream()
                .map(this::toDto)
                .toList();
    }

}
