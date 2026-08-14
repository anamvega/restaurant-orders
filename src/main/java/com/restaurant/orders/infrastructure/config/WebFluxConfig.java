package com.restaurant.orders.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * WebFlux configuration.
 * Adds custom converters, formatters, or CORS settings if needed.
 */
@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    // CORS configuration can be added here if needed
    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    //     registry.addMapping("/**")
    //             .allowedOrigins("*")
    //             .allowedMethods("GET", "POST", "PATCH", "DELETE")
    //             .allowedHeaders("*");
    // }
}
