package com.realnest.security;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Value("${CLOUDINARY_CLOUD_NAME:${cloudinary.cloud-name:}}")
    private String cloudName;

    @Value("${CLOUDINARY_API_KEY:${cloudinary.api-key:}}")
    private String apiKey;

    @Value("${CLOUDINARY_API_SECRET:${cloudinary.api-secret:}}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        config.put("secure", true);
        return new Cloudinary(config);
    }
}
