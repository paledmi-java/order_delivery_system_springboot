package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class PagedResponseDto<T> {
    private List<T> content;

    private int page;
    private int size;

    private Long totalElements;
    private int totalPages;

    private boolean last;

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
