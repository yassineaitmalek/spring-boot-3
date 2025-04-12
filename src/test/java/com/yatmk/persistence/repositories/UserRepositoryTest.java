package com.yatmk.persistence.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.yatmk.persistence.models.Role;
import com.yatmk.persistence.models.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  private User user1;

  private User user2;

  @BeforeEach
  void setup() {

    userRepository.deleteAll();
    roleRepository.deleteAll();
    Role role1 = Role.builder().name("role1").build();
    Role role2 = Role.builder().name("role2").build();
    Role role3 = Role.builder().name("role3").build();
    roleRepository.saveAll(Arrays.asList(role1, role2, role3));

    this.user1 = User.builder().email("user1@example.com").password("password1").roles(Arrays.asList(role1, role3))
        .build();
    this.user2 = User.builder().email("user2@example.com").password("password2").roles(Arrays.asList(role2)).build();
    userRepository.saveAll(Arrays.asList(user1, user2));

  }

  @Test
  void testFindAll() {

    assertFalse(userRepository.findAll().isEmpty());
  }

  @Test
  void testFindByEmailExists() {

    assertFalse(userRepository.findTopByEmail("user1@example.com").isEmpty());

  }

  @Test
  void testFindByEmailNotExists() {

    assertFalse(userRepository.findTopByEmail("user3@example.com").isEmpty());

  }

  @Test
  void testSaveExistingEmail() {
    User user3 = User.builder().email("user1@example.com").password("password3").build();
    assertThrows(DataIntegrityViolationException.class, () -> {
      userRepository.save(user3);
      userRepository.flush();
    });
  }

  @Test
  void testFindByEmailAndRoleExists() {

    Optional<User> user = userRepository.findTopByEmail("user1@example.com");
    Optional<String> role = user.map(User::getRoles).orElseGet(Collections::emptyList)
        .stream().map(Role::getName).filter("role1"::equals).findFirst();

    assertFalse(user.isEmpty());
    assertFalse(role.isEmpty());

  }

}
