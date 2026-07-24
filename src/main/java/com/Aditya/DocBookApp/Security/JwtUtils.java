package com.Aditya.DocBookApp.Security;

import com.Aditya.DocBookApp.Configuration.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils
{

    private final JwtConfig jwtConfig;
    private SecretKey getSigningKey()
    {
        return Keys.hmacShaKeyFor(jwtConfig.getJwtSecret().getBytes());
    }

    public String generateAccessToken(String email, String role)
    {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String email)
    {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshTokenExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    public String getEmailFromToken(String token)
    {
        return getClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e)
        {
            return false;
        }
    }

    private Claims getClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
