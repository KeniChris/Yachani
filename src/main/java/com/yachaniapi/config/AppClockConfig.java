package com.yachaniapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class AppClockConfig {
    @Bean
    Clock appClock(@Value("${app.time-zone:America/Lima}") String zona) {
        return Clock.system(ZoneId.of(zona));
    }
}
