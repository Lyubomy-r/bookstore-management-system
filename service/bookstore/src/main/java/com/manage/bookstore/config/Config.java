package com.manage.bookstore.config;

import com.manage.bookstore.mapper.BookMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public BookMapper bookMapper() {
        return BookMapper.INSTANCE;
    }
}
