package com.yachaniapi.recursos.dto.response;

import java.time.Instant;

public record DescargaResponse(
        String url,
        Instant venceEn
) {}