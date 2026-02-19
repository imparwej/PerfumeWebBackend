package com.perfumeweb.controller;

import com.perfumeweb.model.Perfume;
import com.perfumeweb.service.PerfumeService;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/admin/perfumes")
    public class AdminPerfumeController {

        private final PerfumeService perfumeService;

        public AdminPerfumeController(PerfumeService perfumeService) {
            this.perfumeService = perfumeService;
        }

        @PostMapping
        public Perfume create(@RequestBody Perfume perfume) {
            return perfumeService.savePerfume(perfume);
        }

        @PutMapping("/{id}")
        public Perfume update(@PathVariable Long id, @RequestBody Perfume perfume) {
            perfume.setId(id);
            return perfumeService.savePerfume(perfume);
        }

        @DeleteMapping("/{id}")
        public void delete(@PathVariable Long id) {
            perfumeService.deletePerfume(id);
        }
    }
