package org.eletra.energy.converter.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public CsvMapper csvMapper() {
        return new CsvMapper();
    }
}
