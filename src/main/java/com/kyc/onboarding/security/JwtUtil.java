package com.kyc.onboarding.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.Base64;

@Component
public class JwtUtil {
    private static final String SECRET = "SurendraHarshPranitHyndaviBhavanSujeet";
    private final Key SECRET_KEY = Keys.hmacShaKeyFor(Base64.getEncoder().encode(SECRET.getBytes()));

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public String extractRole(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token)
                .getBody().get("role", String.class);
    }
}
