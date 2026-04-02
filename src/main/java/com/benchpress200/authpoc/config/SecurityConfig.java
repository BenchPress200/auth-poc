package com.benchpress200.authpoc.config;

import com.benchpress200.authpoc.filter.SessionLoginFilter;
import com.benchpress200.authpoc.filter.TokenFilter;
import com.benchpress200.authpoc.filter.TokenLoginFilter;
import com.benchpress200.authpoc.filter.handler.SessionLogoutHandler;
import com.benchpress200.authpoc.filter.handler.SessionLogoutSuccessHandler;
import com.benchpress200.authpoc.filter.handler.TokenLogoutHandler;
import com.benchpress200.authpoc.filter.handler.TokenLogoutSuccessHandler;
import com.benchpress200.authpoc.filter.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final SessionLogoutHandler sessionLogoutHandler;
    private final SessionLogoutSuccessHandler sessionLogoutSuccessHandler;
    private final TokenLogoutHandler tokenLogoutHandler;
    private final TokenLogoutSuccessHandler tokenLogoutSuccessHandler;

    private final TokenFilter tokenFilter;
    private final JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }


    /**
     * 회원가입(POST "/users")을 위한 필터체인
     */
    @Bean
    public SecurityFilterChain noFilterChain(HttpSecurity http) {
        // 필터 체인 매핑
        http
                .securityMatcher("/users");

        // 불필요한 기본 설정 disable
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        // 허용 URL 명시
        http
                .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/users").permitAll()
        );

        return http.build();
    }

    /**
     * 세션 인증 필터 체인
     */
    @Bean
    public SecurityFilterChain sessionFilterChain(HttpSecurity http) {
        // 세션 로그인 필터 생성 및 엔드포인트 커스텀
        SessionLoginFilter loginFilter = new SessionLoginFilter(authenticationManager(authenticationConfiguration));
        loginFilter.setFilterProcessesUrl("/session/login");

        http // 필터 체인 매핑
                .securityMatcher("/session/**");

        http // 불필요한 기본 설정 disable
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http // 로그인 시 세션 저장 및 로그인 필터 등록
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        http // 세션 로그아웃 핸들러 커스텀
                .logout((logout) -> logout
                        .logoutUrl("/session/logout")
                        .addLogoutHandler(sessionLogoutHandler)
                        .logoutSuccessHandler(sessionLogoutSuccessHandler)
                );

        http // 인증 필요 URL 추가 => 매핑 URL 추가함으로써 ExceptionTranslationFilter 이후 AuthorizationFilter 추가됨
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/session/me").authenticated()
                );

        return http.build();
    }

    /**
     * 토큰 인증 필터 체인
     */
    @Bean
    public SecurityFilterChain tokenFilterChain(HttpSecurity http) {
        // 토큰 로그인 필터 생성 및 엔드포인트 커스텀
        TokenLoginFilter loginFilter = new TokenLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil);
        loginFilter.setFilterProcessesUrl("/token/login");

        // 필터 체인 매핑
        http
                .securityMatcher("/token/**");

        // 불필요한 기본 설정 disable
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http // 로그인 시 세션 stateless 및 로그인 필터 등록
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class) // 토큰 인증 필터 추가
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        http // 토큰 로그아웃 핸들러 커스텀
                .logout((logout) -> logout
                        .logoutUrl("/token/logout")
                        .addLogoutHandler(tokenLogoutHandler)
                        .logoutSuccessHandler(tokenLogoutSuccessHandler)
                );

        http // 인증 필요 URL 추가 => 매핑 URL 추가함으로써 ExceptionTranslationFilter 이후 AuthorizationFilter 추가됨
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/token/me").authenticated()
                );

        return http.build();
    }
}
