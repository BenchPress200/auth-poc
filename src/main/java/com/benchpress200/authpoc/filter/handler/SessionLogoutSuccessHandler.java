package com.benchpress200.authpoc.filter.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class SessionLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        Cookie tokenCookie = new Cookie("SESSION", null);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(0);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
