package com.perfumeweb.controller;

import com.perfumeweb.model.Category;
import com.perfumeweb.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
        private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CategoryController.class);
    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "Get all categories", description = "Returns all perfume categories (public API)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categories fetched successfully")
    })
    @GetMapping
    public List<com.perfumeweb.dto.CategoryResponse> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            log.info("Fetched categories: total={}", categories.size());
            return categories.stream().map(this::toDto).toList();
        } catch (Exception e) {
            log.error("Error fetching categories: {}", e.getMessage());
            throw e;
        }
    }

    private com.perfumeweb.dto.CategoryResponse toDto(Category category) {
        com.perfumeweb.dto.CategoryResponse dto = new com.perfumeweb.dto.CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}


