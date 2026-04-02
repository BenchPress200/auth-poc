package com.benchpress200.authpoc.filter.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class SessionLogoutHandler implements LogoutHandler {

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
                       @Nullable Authentication authentication
    ) {
        HttpSession session = request.getSession(false); // false 전달해야 없어도 생성안함

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();
    }
}
