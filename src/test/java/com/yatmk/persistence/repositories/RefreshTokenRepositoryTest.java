package com.yatmk.persistence.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.yatmk.persistence.models.RefreshToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class RefreshTokenRepositoryTest {

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  private RefreshToken refreshToken1;

  private RefreshToken refreshToken2;

  @BeforeEach
  void setup() {
    refreshTokenRepository.deleteAll();
    this.refreshToken1 = RefreshToken.builder().token("token1").build();
    this.refreshToken2 = RefreshToken.builder().token("token2").build();
    refreshTokenRepository.saveAll(Arrays.asList(refreshToken1, refreshToken2));

  }

  @Test
  void testFindAll() {
    log.info("refreshToken1 id is : " + refreshToken1.getId());
    assertFalse(refreshTokenRepository.findAll().isEmpty());
  }

  @Test
  void testFindByTokenExists() {
    log.info("refreshToken1 id is : " + refreshToken1.getId());
    assertTrue(refreshTokenRepository.findTopByToken("token1").isPresent());
  }

  @Test
  void testFindByTokenNotExists() {
    log.info("refreshToken1 id is : " + refreshToken1.getId());
    assertFalse(refreshTokenRepository.findTopByToken("token3").isPresent());
  }

}
