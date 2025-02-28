package com.yatmk.infrastructure.services;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yatmk.persistence.dto.UserDTO;
import com.yatmk.persistence.dto.UserUpdateDTO;
import com.yatmk.persistence.exception.config.ResourceNotFoundException;
import com.yatmk.persistence.exception.config.ServerSideException;
import com.yatmk.persistence.models.User;
import com.yatmk.persistence.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    private final ModelMapper modelMapper;

    public User getUserById(String id) {
        return Optional.ofNullable(id)
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new ResourceNotFoundException("User NOT FOUND"));
    }

    public User getCurrentUser() {

        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(UserDetails.class::isInstance)
                .map(UserDetails.class::cast)
                .map(UserDetails::getUsername)
                .flatMap(userRepository::findTopByEmail)
                .orElseThrow(() -> new ServerSideException("the current user is not  authenticated"));

    }

    public User createUser(UserDTO userDTO) {

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(roleService.getRoles(userDTO.getRoles()));
        user.setLastName(userDTO.getLastName());
        user.setFirstName(userDTO.getFirstName());
        user.setUsername(userDTO.getUserName());
        return userRepository.save(user);

    }

    public void deleteUser(String id) {
        userRepository.findById(id)
                .ifPresent(userRepository::delete);
    }

    public User updateUser(String id, UserUpdateDTO userUpdateDTO) {

        User user = getUserById(id);
        modelMapper.map(userUpdateDTO, user);
        return userRepository.save(user);

    }

}
