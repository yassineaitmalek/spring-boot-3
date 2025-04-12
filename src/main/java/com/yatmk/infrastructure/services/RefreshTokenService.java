package com.yatmk.infrastructure.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.yatmk.persistence.dto.AuthDTO;
import com.yatmk.persistence.dto.JwtResponse;
import com.yatmk.persistence.dto.RefreshTokenDTO;
import com.yatmk.persistence.exception.config.ClientSideException;
import com.yatmk.persistence.exception.config.ResourceNotFoundException;
import com.yatmk.persistence.exception.config.ServerSideException;
import com.yatmk.persistence.models.RefreshToken;
import com.yatmk.persistence.models.User;
import com.yatmk.persistence.repositories.RefreshTokenRepository;
import com.yatmk.persistence.repositories.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    public RefreshToken createRefreshToken(String userId) {
        if (Objects.isNull(userId)) {
            throw new ServerSideException("user id can not be null");
        }
        long now = System.currentTimeMillis();
        long expiration = 10;
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(jwtService.generateRefreshToken(userId, now, expiration))
                .expiryDate(Instant.ofEpochMilli(now).plus(expiration, ChronoUnit.MINUTES))
                .build();
        return refreshTokenRepository.save(refreshToken);

    }

    public JwtResponse generateJWT(User user) {
        return Optional.ofNullable(user)
                .map(User::getId)
                .map(this::createRefreshToken)
                .map(e -> JwtResponse.builder()
                        .accessToken(jwtService.generateToken(user.getId()))
                        .refreshToken(e.getToken())
                        .build())
                .orElseThrow(() -> new ServerSideException("invalid user request !"));

    }

    public JwtResponse refreshToken(RefreshTokenDTO refreshTokenDTO) {
        return Optional.ofNullable(refreshTokenDTO)
                .map(RefreshTokenDTO::getRefreshToken)
                .flatMap(refreshTokenRepository::findTopByToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUserId)
                .map(jwtService::generateToken)
                .map(accessToken -> JwtResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshTokenDTO.getRefreshToken())
                        .build())
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token is not in database!"));
    }

    public JwtResponse logIn(@Valid AuthDTO authDTO) {

        if (Objects.isNull(authDTO)) {
            throw new ClientSideException("null input");
        }
        String email = authDTO.getEmail();
        String password = authDTO.getPassword();
        if (Objects.isNull(email) || Objects.isNull(password)) {
            throw new ClientSideException("email or password is null");
        }
        return Optional.ofNullable(new UsernamePasswordAuthenticationToken(email, password))
                .map(authenticationManager::authenticate)
                .filter(Authentication::isAuthenticated)
                .flatMap(e -> userRepository.findTopByEmail(email))
                .map(this::generateJWT)
                .orElseThrow(() -> new ClientSideException("email or password is incorrect"));

    }

    public void logOut(RefreshToken refreshToken) {

        Optional.ofNullable(refreshToken)
                .ifPresent(refreshTokenRepository::delete);

    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (Objects.isNull(token)) {
            throw new ServerSideException("Refresh token can not be null");
        }
        if (token.getExpiryDate().compareTo(Instant.now()) > 0) {
            return token;
        }
        logOut(token);
        throw new ClientSideException("Refresh token was expired. Please make a new signin request");
    }
}
