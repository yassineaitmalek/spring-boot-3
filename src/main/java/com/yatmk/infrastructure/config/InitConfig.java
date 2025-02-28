package com.yatmk.infrastructure.config;

import java.util.Arrays;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import com.yatmk.infrastructure.services.RoleService;
import com.yatmk.infrastructure.services.UserService;
import com.yatmk.persistence.dto.UserDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitConfig {

  private final RoleService roleService;

  private final UserService userService;

  @PostConstruct
  public void init() {
    try {
      roleService.createRoles(RoleService.ADMIN, RoleService.REGULAR_USER);

      UserDTO admin = UserDTO.builder()
          .email("admin@admin.com")
          .password("admin")
          .firstName("ADMIN")
          .lastName("ADMIN")
          .userName("ADMIN")
          .roles(Arrays.asList(RoleService.ADMIN))
          .build();
      userService.createUser(admin);
    } catch (Exception e) {
      log.error("admin is already created");

    }

  }

}
