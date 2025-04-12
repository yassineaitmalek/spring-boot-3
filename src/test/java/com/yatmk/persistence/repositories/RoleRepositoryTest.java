package com.yatmk.persistence.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.yatmk.persistence.models.Role;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class RoleRepositoryTest {

  @Autowired
  private RoleRepository roleRepository;

  private Role role1;

  private Role role2;

  private Role role3;

  @BeforeEach
  void setup() {

    roleRepository.deleteAll();
    this.role1 = Role.builder().name("role1").build();
    this.role2 = Role.builder().name("role2").build();
    this.role3 = Role.builder().name("role3").build();
    roleRepository.saveAll(Arrays.asList(role1, role2, role3));

  }

  @Test
  void testFindAll() {

    assertFalse(roleRepository.findAll().isEmpty());
  }

  @Test
  void testFindByRoleExists() {

    assertFalse(roleRepository.findTopByName(role1.getName()).isEmpty());

  }

  @Test
  void testFindByRoleNotExists() {

    assertTrue(roleRepository.findTopByName("role4").isEmpty());

  }

  @Test
  void testSaveExistingRole() {
    Role role = Role.builder().name("role1").build();
    assertThrows(DataIntegrityViolationException.class, () -> {
      roleRepository.save(role);
      roleRepository.flush();
    });
  }

  @Test
  void testFindAllByNameInFull() {
    List<Role> roles = roleRepository.findAllByNameIn(Arrays.asList("role1", "role2"));
    assertFalse(roles.isEmpty());
    assertEquals(2, roles.size());
  }

  @Test
  void testFindAllByNameInHalf() {
    List<Role> roles = roleRepository.findAllByNameIn(Arrays.asList("role1", "role4"));
    assertFalse(roles.isEmpty());
    assertEquals(1, roles.size());
  }

  @Test
  void testFindAllByNameInEmpty() {
    List<Role> roles = roleRepository.findAllByNameIn(Arrays.asList("role5", "role6"));
    assertTrue(roles.isEmpty());
    assertEquals(0, roles.size());
  }

}
