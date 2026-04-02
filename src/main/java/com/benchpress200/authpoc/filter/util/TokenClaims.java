package com.benchpress200.authpoc.filter.util;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenClaims {
    private String jti;
    private Long userId;
    private String role;
    private Date expiration;

    public static TokenClaims of(
            String jti,
            Long userId,
            String role,
            Date expiration
    ) {
        return TokenClaims.builder()
                .jti(jti)
                .userId(userId)
                .role(role)
                .expiration(expiration)
                .build();
    }
}
