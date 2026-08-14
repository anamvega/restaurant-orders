package com.restaurant.orders.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;

/**
 * Configuration class for MongoDB reactive template.
 */
@Configuration
public class MongoConfig {

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(
            ReactiveMongoDatabaseFactory databaseFactory,
            MongoConverter converter) {
        return new ReactiveMongoTemplate(databaseFactory, converter);
    }
}
