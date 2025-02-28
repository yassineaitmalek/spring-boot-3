package com.yatmk.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yatmk.infrastructure.services.UserService;
import com.yatmk.persistence.dto.UserDTO;
import com.yatmk.persistence.dto.UserUpdateDTO;
import com.yatmk.persistence.models.User;
import com.yatmk.persistence.presentation.ApiDataResponse;
import com.yatmk.presentation.config.AbstractController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements AbstractController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiDataResponse<User>> createUser(@RequestBody UserDTO employeeDTO) {
        return create(() -> userService.createUser(employeeDTO));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        return noContent(() -> userService.deleteUser(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiDataResponse<User>> getUser(@PathVariable String id) {

        return ok(() -> userService.getUserById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiDataResponse<User>> updateUser(@PathVariable String id,
            @RequestBody UserUpdateDTO employeeUpdateDTO) {

        return ok(() -> userService.updateUser(id, employeeUpdateDTO));
    }

}
