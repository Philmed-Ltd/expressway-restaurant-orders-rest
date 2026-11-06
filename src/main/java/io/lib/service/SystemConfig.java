package io.lib.service;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Configuration
public class SystemConfig {
    public static final String INTERNAL_USER_BASE_URL = "/internal";
    public static final String CUSTOMER_USER_BASE_URL = "/customer";

    @Bean
    public MessageSource messageSource() {
        var source = new ReloadableResourceBundleMessageSource();
        source.addBasenames("classpath:msg/messages");
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JsonMapper jsonMapper() {
        var module = new SimpleModule();

        module.addSerializer(LocalDateTime.class, new ValueSerializer<LocalDateTime>() {
            @Override
            public void serialize(
                    LocalDateTime value,
                    JsonGenerator gen,
                    SerializationContext context
            ) throws JacksonException {
                var utcValue = DateTimeFormatter.ISO_INSTANT.format(value.toInstant(ZoneOffset.UTC));
                gen.writeString(utcValue);
            }
        });

        module.addDeserializer(LocalDateTime.class, new ValueDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(
                    JsonParser parser,
                    DeserializationContext context
            ) throws JacksonException {
                var value = parser.getText();

                if (value == null || value.isBlank()) {
                    return null;
                }

                if (value.endsWith("Z")) {
                    return LocalDateTime.ofInstant(Instant.parse(value), ZoneOffset.UTC);
                }

                return LocalDateTime.parse(value);
            }
        });

        return JsonMapper.builder()
                .addModule(module)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper(JsonMapper jsonMapper) {
        return jsonMapper;
    }

    @Bean
    public JacksonJsonHttpMessageConverter jacksonJsonHttpMessageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonHttpMessageConverter(jsonMapper);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}