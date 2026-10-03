package com.yachaniapi.recursos.dto.response;

import java.util.List;

public record EnvioResponse(
        String mensaje,
        List<MaterialResponse> materiales
) {}