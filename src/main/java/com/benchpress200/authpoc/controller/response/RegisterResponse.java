package com.benchpress200.authpoc.controller.response;

import com.benchpress200.authpoc.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {
    private Long id;
    private String username;

    public static RegisterResponse from(User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
