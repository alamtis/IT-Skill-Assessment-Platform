package com.skillassessment.platform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * General application configuration class.
 * This class is used to define beans that are used across the application,
 * such as the password encoder and JSON object mapper.
 */
@Configuration
public class AppConfig {

    /**
     * Creates a PasswordEncoder bean using the BCrypt hashing algorithm.
     * BCrypt is the industry standard for securely hashing passwords.
     * This bean will be injected wherever password encoding is needed,
     * such as in the AuthService and AdminUserInitializer.
     *
     * @return A singleton instance of BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates a singleton ObjectMapper bean for JSON serialization and deserialization.
     * Defining it as a bean allows for central configuration. For instance,
     * we register the JavaTimeModule to ensure Java 8+ time objects like
     * LocalDateTime are serialized/deserialized correctly to/from ISO-8601 strings.
     *
     * @return A configured singleton instance of ObjectMapper.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}