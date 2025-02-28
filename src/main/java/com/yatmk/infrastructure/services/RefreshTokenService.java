package com.yatmk.infrastructure.services;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.yatmk.persistence.dto.AuthDTO;
import com.yatmk.persistence.dto.JwtResponse;
import com.yatmk.persistence.dto.RefreshTokenDTO;
import com.yatmk.persistence.models.RefreshToken;
import com.yatmk.persistence.models.User;
import com.yatmk.persistence.repositories.UserRepository;
import com.yatmk.persistence.repositories.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    public Optional<RefreshToken> createRefreshToken(String userId) {

        long now = System.currentTimeMillis();
        long expiration = 600000l;
        return Optional.ofNullable(userId)
                .map(e -> RefreshToken.builder()
                        .userId(e)
                        .token(jwtService.generateRefreshToken(userId, now, expiration))
                        .expiryDate(Instant.ofEpochMilli(now).plusMillis(expiration))// 10
                        .build())
                .map(e -> Optional.of(refreshTokenRepository.save(e)))
                .orElse(Optional.empty());

    }

    public Optional<RefreshToken> verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(
                    token.getToken() + " Refresh token was expired. Please make a new signin request");
        }
        return Optional.of(token);
    }

    public JwtResponse refreshToken(RefreshTokenDTO refreshTokenDTO) {
        return refreshTokenRepository.findTopByToken(refreshTokenDTO.getRefreshToken())
                .map(this::verifyExpiration)
                .flatMap(Function.identity())
                .map(RefreshToken::getUserId)
                .map(jwtService::generateToken)
                .map(accessToken -> JwtResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshTokenDTO.getRefreshToken())
                        .build())
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    public JwtResponse logIn(AuthDTO authDTO) {

        UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(authDTO.getEmail(),
                authDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(upat);
        if (authentication.isAuthenticated()) {
            return userRepository.findTopByEmail(authDTO.getEmail())
                    .map(this::generateJWT)
                    .orElseThrow(() -> new UsernameNotFoundException("invalid user request ! "));

        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    public void logOut(RefreshToken refreshToken) {
        verifyExpiration(refreshToken)
                .ifPresent(e -> {
                    e.setLoggedOut(true);
                    refreshTokenRepository.save(e);
                });

    }

    public JwtResponse generateJWT(User user) {
        return createRefreshToken(user.getId())
                .map(e -> JwtResponse.builder()
                        .accessToken(jwtService.generateToken(user.getId()))
                        .refreshToken(e.getToken()).build())
                .orElseThrow(() -> new UsernameNotFoundException("invalid user request !"));

    }
}
