package com.yachaniapi.recursos.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime fecha,
        int estado,
        String mensaje,
        List<String> errores
) {}