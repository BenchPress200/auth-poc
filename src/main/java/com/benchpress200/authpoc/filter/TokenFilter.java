package com.benchpress200.authpoc.filter;

import com.benchpress200.authpoc.filter.dto.CustomUserDetails;
import com.benchpress200.authpoc.filter.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 토큰 검증 및 시큐리티 컨텍스트 홀더 적재
        extractToken(request)
                .flatMap(jwtUtil::validateToken)
                .ifPresent(tokenClaims -> {

                    Long userId = tokenClaims.getUserId();
                    String role = tokenClaims.getRole();
                    String jti = tokenClaims.getJti();

                    // 블랙 리스트에 있다면 인증 정보 적재 X
                    if(redisTemplate.hasKey("blacklist:" + jti)) {
                        return;
                    }

                    CustomUserDetails customUserDetails = CustomUserDetails.builder()
                            .userId(userId)
                            .role(role)
                            .build();

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            customUserDetails,
                            null,
                            customUserDetails.getAuthorities()
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });

        // 다음 필터 진행
        filterChain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader("Authorization");

        if (authenticationHeader != null && authenticationHeader.startsWith("Bearer")) {
            return Optional.of(authenticationHeader.substring(7));
        }

        return Optional.empty();
    }
}
