package com.example.SmartLogi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.client.RestTemplate;

/**
 * Custom JWT Decoder configuration to handle WSL2/Docker networking issues.
 * 
 * When running Docker Desktop with WSL2 backend on Windows, there's a known
 * issue
 * where Java's HTTP client resolves 'localhost' to '127.0.0.1', but WSL2's port
 * forwarding only works correctly with the 'localhost' hostname (not the IP).
 * 
 * This configuration uses a custom RestTemplate that works around this issue.
 */
@Configuration
public class JwtDecoderConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public JwtDecoder jwtDecoder() {
        // Create a RestTemplate with simple settings
        RestTemplate restTemplate = new RestTemplate();

        // Configure the request factory with appropriate timeouts
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(10000);

        restTemplate.setRequestFactory(requestFactory);

        // Build the JWT decoder with our custom RestTemplate
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .restOperations(restTemplate)
                .build();
    }
}
