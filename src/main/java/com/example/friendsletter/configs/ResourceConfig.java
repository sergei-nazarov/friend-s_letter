package com.example.friendsletter.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.List;
import java.util.Map;

@Configuration
public class ResourceConfig {


    @Bean
    public SimpleUrlHandlerMapping staticResourceHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MIN_VALUE);
        ResourceHttpRequestHandler resourceHandler = staticResourceHandler();
        mapping.setUrlMap(Map.of("/static/**", resourceHandler,
                "/favicon.ico", resourceHandler));
        return mapping;
    }

    @Bean
    public ResourceHttpRequestHandler staticResourceHandler() {
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        requestHandler.setLocations(List.of(new ClassPathResource("/static/")));
        return requestHandler;
    }

}
