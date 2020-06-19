package com.example.demo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Configuration
    static class ObjectMapperCustomizer {
        private ObjectMapper createObjectMapper() {
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
                        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                        objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
                        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
                        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                        objectMapper.registerModules(new JavaTimeModule());
                        return objectMapper;
                    }

                private MappingJackson2HttpMessageConverter createMappingJacksonHttpMessageConverter() {
            
                                MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
                        converter.setObjectMapper(createObjectMapper());
                        return converter;
                    }
        @Bean
        public RestTemplate restTemplate() {
            RestTemplate _restTemplate = new RestTemplate();
            List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
            messageConverters.add(createMappingJacksonHttpMessageConverter());
            _restTemplate.setMessageConverters(messageConverters);
            return _restTemplate;
        }
    }

}
