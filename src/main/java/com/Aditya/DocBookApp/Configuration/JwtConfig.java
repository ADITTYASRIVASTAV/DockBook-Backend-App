package com.Aditya.DocBookApp.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig
{
    private String jwtSecret;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;
}
