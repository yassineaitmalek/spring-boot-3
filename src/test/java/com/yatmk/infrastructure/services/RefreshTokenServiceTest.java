package com.yatmk.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import com.yatmk.persistence.dto.AuthDTO;
import com.yatmk.persistence.dto.JwtResponse;
import com.yatmk.persistence.dto.RefreshTokenDTO;
import com.yatmk.persistence.exception.config.ClientSideException;
import com.yatmk.persistence.exception.config.ServerSideException;
import com.yatmk.persistence.models.RefreshToken;
import com.yatmk.persistence.models.User;
import com.yatmk.persistence.repositories.RefreshTokenRepository;
import com.yatmk.persistence.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

  @InjectMocks
  private RefreshTokenService refreshTokenService;

  @Mock
  private JwtService jwtService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private AuthenticationManager authenticationManager;

  private String userId;

  private String token;

  private String accessToken;

  @BeforeEach
  void setUp() {
    this.userId = "userId";
    this.token = "token";
    this.accessToken = "accessToken";
  }

  @Test
  void testCreateRefreshTokenNotNull() {

    RefreshToken refreshToken = RefreshToken.builder().userId(userId).token(token).build();

    lenient().when(jwtService.generateRefreshToken(anyString(), anyLong(), anyLong()))
        .thenReturn(token);
    lenient().when(refreshTokenRepository.save(any(RefreshToken.class)))
        .thenReturn(refreshToken);

    assertNotNull(refreshTokenService.createRefreshToken(userId));

  }

  @Test
  void testCreateRefreshTokenNull() {
    assertThrows(ServerSideException.class, () -> {
      refreshTokenService.createRefreshToken(null);
    });

  }

  @Test
  void testVerifyExpirationValid() {

    long now = System.currentTimeMillis();
    long expiration = 10l;
    RefreshToken refreshToken = RefreshToken.builder()
        .userId(userId)
        .token(token)
        .expiryDate(Instant.ofEpochMilli(now).plus(expiration, ChronoUnit.MINUTES))
        .build();
    assertNotNull(refreshTokenService.verifyExpiration(refreshToken));
    verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));

  }

  @Test
  void testVerifyExpirationNonValid() {

    long now = System.currentTimeMillis();
    long expiration = 10l;
    RefreshToken refreshToken = RefreshToken.builder()
        .userId(userId)
        .token(token)
        .expiryDate(Instant.ofEpochMilli(now).minus(expiration, ChronoUnit.MINUTES))
        .build();

    assertThrows(ClientSideException.class, () -> {

      refreshTokenService.verifyExpiration(refreshToken);

    });
    verify(refreshTokenRepository, times(1)).delete(refreshToken);

  }

  @Test
  void testLogOut_ShouldDeleteToken_WhenTokenIsNotNull() {

    RefreshToken refreshToken = RefreshToken.builder().userId(userId).token(token).build();
    refreshTokenService.logOut(refreshToken);
    verify(refreshTokenRepository, times(1)).delete(refreshToken);
  }

  @Test
  void testLogOut_ShouldNotDelete_WhenTokenIsNull() {
    refreshTokenService.logOut(null);
    verify(refreshTokenRepository, never()).delete(any());
  }

  @Test
  void testGenerateJWT() {

    User user = new User();
    user.setId(userId);
    RefreshToken refreshToken = RefreshToken.builder().userId(userId).token(token).build();

    when(jwtService.generateRefreshToken(anyString(), anyLong(), anyLong())).thenReturn(token);
    when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);
    when(jwtService.generateToken(anyString())).thenReturn(accessToken);
    JwtResponse jwtResponse = refreshTokenService.generateJWT(user);
    assertNotNull(jwtResponse);
    assertEquals(jwtResponse.getAccessToken(), accessToken);
    assertEquals(jwtResponse.getRefreshToken(), token);

  }

  @Test
  void testLogInWhenSuccessful() {
    String email = "user@example.com";
    String password = "password";
    AuthDTO dto = new AuthDTO(email, password);
    User user = new User();
    user.setId(userId);
    user.setEmail(email);
    RefreshToken refreshToken = RefreshToken.builder().userId(userId).token(token).build();

    Authentication auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(Boolean.TRUE);
    when(authenticationManager.authenticate(any())).thenReturn(auth);

    when(userRepository.findTopByEmail(email)).thenReturn(Optional.of(user));

    when(jwtService.generateRefreshToken(anyString(), anyLong(), anyLong())).thenReturn(token);
    when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);
    when(jwtService.generateToken(anyString())).thenReturn(accessToken);

    JwtResponse actualResponse = refreshTokenService.logIn(dto);

    assertNotNull(actualResponse);
    assertEquals(actualResponse.getAccessToken(), accessToken);
    assertEquals(actualResponse.getRefreshToken(), token);
  }

  @Test
  void testLogInWhenNotSuccessful() {
    String email = "user@example.com";
    String password = "password";
    AuthDTO dto = new AuthDTO(email, password);
    Authentication auth = mock(Authentication.class);
    when(authenticationManager.authenticate(any())).thenReturn(auth);
    when(auth.isAuthenticated()).thenReturn(Boolean.FALSE);

    assertThrows(ClientSideException.class, () -> {

      refreshTokenService.logIn(dto);

    });
  }

  @Test
  void testRefreshToken() {

    RefreshTokenDTO refreshTokenDTO = RefreshTokenDTO.builder().refreshToken(token).build();
    long now = System.currentTimeMillis();
    long expiration = 10l;
    RefreshToken refreshToken = RefreshToken.builder()
        .userId(userId)
        .token(token)
        .expiryDate(Instant.ofEpochMilli(now).plus(expiration, ChronoUnit.MINUTES))
        .build();

    when(refreshTokenRepository.findTopByToken(anyString())).thenReturn(Optional.of(refreshToken));

    when(jwtService.generateToken(anyString())).thenReturn(accessToken);

    JwtResponse actualResponse = refreshTokenService.refreshToken(refreshTokenDTO);
    verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));

    assertNotNull(actualResponse);
    assertEquals(actualResponse.getAccessToken(), accessToken);
    assertEquals(actualResponse.getRefreshToken(), token);

  }
}
