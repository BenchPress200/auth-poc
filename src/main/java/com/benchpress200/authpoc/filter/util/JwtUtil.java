package com.benchpress200.authpoc.filter.util;

import com.benchpress200.authpoc.filter.dto.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.lettuce.core.dynamic.annotation.CommandNaming;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secret;

    @Value("${jwt.expired-time}")
    private Long expiredTime;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    public String issueToken(CustomUserDetails customUserDetails) {
        long now = System.currentTimeMillis();
        String jti = UUID.randomUUID().toString();
        Long userId = customUserDetails.getUserId();
        String role = customUserDetails.getRole();

        return Jwts.builder()
                .id(jti)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiredTime))
                .signWith(secretKey)
                .compact();
    }

    public Optional<TokenClaims> validateToken(final String token) {
        try {
            Claims claims = Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String jti = claims.getId();
            Long userId = claims.get("userId", Long.class);
            String role = claims.get("role", String.class);
            Date expiration = claims.getExpiration();

            TokenClaims tokenClaims = TokenClaims.of(
                    jti,
                    userId,
                    role,
                    expiration
            );

            return Optional.of(tokenClaims);
        } catch (JwtException e) {
            return Optional.empty();
        }
    }
}
