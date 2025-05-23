package com.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;




    @Configuration
    public class WebClientConfig {

        @Bean
        public WebClient webClient() {
            return WebClient.builder()
                    .baseUrl("https://imdb-movies-shows-persons-api.p.rapidapi.com")
                    .build(); // Build the WebClient here
        }
    }
