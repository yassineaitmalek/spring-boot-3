package com.yatmk.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.yatmk.persistence.dto.UserDTO;
import com.yatmk.persistence.exception.config.ResourceNotFoundException;
import com.yatmk.persistence.models.Role;
import com.yatmk.persistence.models.User;
import com.yatmk.persistence.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private RoleService roleService;

  @Mock
  private ModelMapper modelMapper;

  @Test
  void testGetUserByIdExists() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(new User()));
    assertNotNull(userService.getUserById("id"));
    verify(userRepository, times(1)).findById(anyString());
  }

  @Test
  void testGetUserByIdNotExists() {
    String id = "id";
    when(userRepository.findById(anyString())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> {
      userService.getUserById(id);
    });
    verify(userRepository, times(1)).findById(anyString());

  }

  @Test
  void testCreateUserValid() {

    String email = "user1@example.com";
    String password = "password";
    String role1 = "role1";
    String role2 = "role2";

    Role roleObj1 = Role.builder().name(role1).build();
    Role roleObj2 = Role.builder().name(role2).build();
    List<String> rolesString = Arrays.asList(role1, role2);

    List<Role> roles = Arrays.asList(roleObj1, roleObj2);
    User user = User.builder().email(email).password(password).roles(roles).build();
    UserDTO userDTO = UserDTO.builder().email(email).password(password).roles(rolesString).build();

    when(passwordEncoder.encode(anyString())).thenReturn(password);
    when(roleService.getRoles(anyList())).thenReturn(roles);
    when(userRepository.save(any(User.class))).thenReturn(user);

    assertNotNull(userService.createUser(userDTO));

  }

  @Test
  void testDeleteUserValid() {
    String id = "id";
    String email = "user1@example.com";
    String password = "password";
    User user = User.builder().email(email).password(password).build();

    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

    userService.deleteUser(id);
    verify(userRepository, times(1)).delete(any(User.class));

  }

  @Test
  void testDeleteUserNotValid() {
    String id = "id";

    when(userRepository.findById(anyString())).thenReturn(Optional.empty());

    userService.deleteUser(id);
    verify(userRepository, never()).delete(any(User.class));

  }

}
