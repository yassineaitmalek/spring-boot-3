package com.yatmk.persistence.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  private String email1;

  private String email2;

  private String email3;

  private String password1;

  private String password2;

  private String password3;

  private Role role1;

  private Role role2;

  private Role role3;

  private String roleName1;

  private String roleName2;

  private String roleName3;

  @BeforeEach
  void setup() {

    userRepository.deleteAll();
    roleRepository.deleteAll();

    this.roleName1 = "role1";
    this.roleName2 = "role2";
    this.roleName3 = "role3";
    this.email1 = "user1@example.com";
    this.email2 = "user2@example.com";
    this.email3 = "user3@example.com";
    this.password1 = "password1";
    this.password2 = "password2";
    this.password3 = "password3";

    this.role1 = Role.builder().name(roleName1).build();
    this.role2 = Role.builder().name(roleName2).build();
    this.role3 = Role.builder().name(roleName3).build();
    roleRepository.saveAll(Arrays.asList(role1, role2, role3));

    this.user1 = User.builder().email(email1).password(password1).roles(Arrays.asList(role1, role3))
        .build();
    this.user2 = User.builder().email(email2).password(password2).roles(Arrays.asList(role2)).build();
    userRepository.saveAll(Arrays.asList(user1, user2));

  }

  @Test
  void testFindAll() {

    assertFalse(userRepository.findAll().isEmpty());
  }

  @Test
  void testFindByEmailExists() {

    assertFalse(userRepository.findTopByEmail(email1).isEmpty());

  }

  @Test
  void testFindByEmailNotExists() {

    assertTrue(userRepository.findTopByEmail(email3).isEmpty());

  }

  @Test
  void testSaveExistingEmail() {
    User user3 = User.builder().email(email2).password(password3).build();
    assertThrows(DataIntegrityViolationException.class, () -> {
      userRepository.save(user3);
      userRepository.flush();
    });
  }

  @Test
  void testFindByEmailAndRoleExists() {

    Optional<User> user = userRepository.findTopByEmail(email1);
    Optional<String> role = user.map(User::getRoles).orElseGet(Collections::emptyList)
        .stream().map(Role::getName).filter(roleName1::equals).findFirst();

    assertFalse(user.isEmpty());
    assertFalse(role.isEmpty());

  }

}
