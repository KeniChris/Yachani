package com.yachaniapi.grupoestudio.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginaResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static <T> PaginaResponse<T> desde(Page<T> pagina) {
        return new PaginaResponse<>(
                pagina.getContent(), pagina.getNumber(), pagina.getSize(),
                pagina.getTotalElements(), pagina.getTotalPages(), pagina.isLast()
        );
    }
}
