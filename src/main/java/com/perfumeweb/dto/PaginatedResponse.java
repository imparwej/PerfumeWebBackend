package com.perfumeweb.dto;

import java.util.List;

public class PaginatedResponse<T> {
    private java.util.List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;

    public PaginatedResponse(java.util.List<T> content, int currentPage, int totalPages, long totalElements) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
    public java.util.List<T> getContent() {
        return content;
    }
    public void setContent(java.util.List<T> content) {
        this.content = content;
    }
    public int getCurrentPage() {
        return currentPage;
    }
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    public long getTotalElements() {
        return totalElements;
    }
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
