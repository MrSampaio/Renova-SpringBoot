package com.renova.renova.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // habilita o cors de todas as rotas da api
        registry.addMapping("/**")
                .allowedOrigins("https://localhost:4200") // URL padrão do angular
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // verbos http permitidos
                .allowedHeaders("*"); // permite que o Angular envie headers, como o JWT
    }

}
