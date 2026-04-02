package com.benchpress200.authpoc.controller.response;

import com.benchpress200.authpoc.filter.dto.CustomUserDetails;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyDataResponse {
    private Long id;
    private String role;

    public static MyDataResponse from(CustomUserDetails customUserDetails) {
        return MyDataResponse.builder()
                .id(customUserDetails.getUserId())
                .role(customUserDetails.getRole())
                .build();
    }
}
