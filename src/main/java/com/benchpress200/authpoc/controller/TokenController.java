package com.benchpress200.authpoc.controller;

import com.benchpress200.authpoc.controller.response.MyDataResponse;
import com.benchpress200.authpoc.filter.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    @GetMapping("/token/me")
    public ResponseEntity<?> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        MyDataResponse response = MyDataResponse.from(customUserDetails);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
