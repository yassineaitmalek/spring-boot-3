package com.yatmk.infrastructure.security.filter;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yatmk.infrastructure.security.service.UserContainer;
import com.yatmk.infrastructure.security.service.UserLoadService;
import com.yatmk.infrastructure.services.JwtService;
import com.yatmk.persistence.exception.config.ClientSideException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserLoadService userLoadService;

    public static record UserToken(String token, String userId) {

    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        Optional.ofNullable("Authorization")
                .map(request::getHeader)
                .filter(Objects::nonNull)
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .map(this::extractUserToken)
                .ifPresent(e -> addAuthentication(request, e));

        filterChain.doFilter(request, response);

    }

    public UserToken extractUserToken(String authHeader) {
        String token = jwtService.extractToken(authHeader);
        String userId = jwtService.extractUserId(token);
        return new UserToken(token, userId);
    }

    public void addAuthentication(HttpServletRequest request, UserToken userToken) {

        UserContainer userDetails = userLoadService.loadUserById(userToken.userId());
        if (!jwtService.validateToken(userToken.token(), userDetails)) {
            throw new ClientSideException("Expired Token");
        }
        Optional.of(userDetails)
                .map(e -> new UsernamePasswordAuthenticationToken(e, null, e.getAuthorities()))
                .map(e -> {
                    e.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    return e;
                })
                .ifPresent(SecurityContextHolder.getContext()::setAuthentication);

    }

}
