package com.benchpress200.authpoc.filter.handler;

import com.benchpress200.authpoc.filter.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TokenLogoutHandler implements LogoutHandler {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
           @Nullable Authentication authentication
    ) {
        extractToken(request)
                .flatMap(jwtUtil::validateToken)
                .ifPresent(tokenClaims -> {
                    String jti = tokenClaims.getJti();
                    Date expiration = tokenClaims.getExpiration();
                    long now = System.currentTimeMillis();
                    long ttl = (expiration.getTime() - now) / 1000;

                    String key = "blacklist:" + jti;

                    redisTemplate.opsForValue().set(
                            key,
                            "logout",
                        Duration.ofSeconds(ttl)
                        );
                });
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader("Authorization");

        if (authenticationHeader != null && authenticationHeader.startsWith("Bearer")) {
            return Optional.of(authenticationHeader.substring(7));
        }

        return Optional.empty();
    }
}
