package com.yatmk.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yatmk.infrastructure.services.RefreshTokenService;
import com.yatmk.infrastructure.services.UserService;
import com.yatmk.persistence.dto.AuthDTO;
import com.yatmk.persistence.dto.JwtResponse;
import com.yatmk.persistence.dto.RefreshTokenDTO;
import com.yatmk.persistence.models.User;
import com.yatmk.persistence.presentation.ApiDataResponse;
import com.yatmk.presentation.config.AbstractController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AbstractController {

    private final RefreshTokenService refreshTokenService;

    private final UserService userService;

    @GetMapping("/current-user")
    public ResponseEntity<ApiDataResponse<User>> getCurrentUser() {
        return ok(userService::getCurrentUser);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiDataResponse<JwtResponse>> login(@RequestBody AuthDTO authDTO) {
        return create(() -> refreshTokenService.logIn(authDTO));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiDataResponse<JwtResponse>> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        return create(() -> refreshTokenService.refreshToken(refreshTokenDTO));
    }

}
