package com.yachaniapi.recursos.dto.response;

public record OperacionFlashcardsResponse<T>(
        String mensaje,
        T datos
) {
}