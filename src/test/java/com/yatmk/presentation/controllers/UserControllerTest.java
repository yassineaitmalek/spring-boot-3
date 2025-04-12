package com.yatmk.presentation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Arrays;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yatmk.infrastructure.security.filter.JwtAuthFilter;
import com.yatmk.infrastructure.services.JwtService;
import com.yatmk.infrastructure.services.UserService;
import com.yatmk.persistence.dto.UserDTO;
import com.yatmk.persistence.dto.UserUpdateDTO;
import com.yatmk.persistence.models.Role;
import com.yatmk.persistence.models.User;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @MockBean
  private JwtService jwtService;

  @MockBean
  private JwtAuthFilter jwtAuthFilter;

  private User user1;

  private String id1;

  private String email1;

  private String password1;

  private Role role1;

  private Role role2;

  private String roleName1;

  private String roleName2;

  @BeforeEach
  void setup() {
    this.id1 = "id1";

    this.roleName1 = "role1";
    this.roleName2 = "role2";

    this.email1 = "user1@example.com";

    this.password1 = "password1";

    this.role1 = Role.builder().name(roleName1).build();
    this.role2 = Role.builder().name(roleName2).build();

    this.user1 = User.builder().email(email1).password(password1).roles(Arrays.asList(role1, role2))
        .build();
    user1.setId(id1);

  }

  @Test
  void testCreateUser() throws Exception {
    UserDTO userDTO = UserDTO.builder().email(email1).password(password1).build();

    given(userService.createUser(any(UserDTO.class)))
        .willReturn(user1);

    MockHttpServletRequestBuilder requestBuilder = post("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userDTO));

    mockMvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.email", CoreMatchers.is(user1.getEmail())))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.data.roles[0].name", CoreMatchers.is(user1.getRoles().get(0).getName())));

  }

  @Test
  void testDeleteUser() throws Exception {

    doNothing().when(userService).deleteUser(any(String.class));

    MockHttpServletRequestBuilder requestBuilder = delete("/api/users/" + user1.getId());

    mockMvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isNoContent());

  }

  @Test
  void testGetUser() throws Exception {

    given(userService.getUserById(any(String.class)))
        .willReturn(user1);

    MockHttpServletRequestBuilder requestBuilder = get("/api/users/" + user1.getId());

    mockMvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.email", CoreMatchers.is(user1.getEmail())));
  }

  @Test
  void testUpdateUser() throws Exception {

    UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder().firstName("firstName").build();

    given(userService.updateUser(any(String.class), any(UserUpdateDTO.class)))
        .willReturn(user1);
    user1.setFirstName(userUpdateDTO.getFirstName());

    MockHttpServletRequestBuilder requestBuilder = patch("/api/users/" + user1.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userUpdateDTO));

    mockMvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.firstName", CoreMatchers.is(user1.getFirstName())));
  }
}
