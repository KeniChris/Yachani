package com.yachaniapi.recursos.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProgresoRequest(

        @NotNull(message = "Indica si aprendiste la tarjeta")
        Boolean aprendida
) {
}