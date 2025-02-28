package com.yatmk.infrastructure.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.yatmk.persistence.dto.RoleDTO;
import com.yatmk.persistence.exception.config.ServerSideException;
import com.yatmk.persistence.models.User;
import com.yatmk.persistence.models.Role;
import com.yatmk.persistence.repositories.UserRepository;
import com.yatmk.persistence.repositories.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    public static final String REGULAR_USER = "REGULAR_USER";

    public static final String ADMIN = "ADMIN";

    public Page<Role> getRoles(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    public String formatRoleName(String roleName) {
        return roleName.trim().toUpperCase();
    }

    public List<Role> createRoles(String... names) {
        return createRoles(Arrays.asList(names));

    }

    public List<Role> createRoles(List<String> names) {
        return Optional.ofNullable(names)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(this::createRole)
                .collect(Collectors.toList());
    }

    public Role createRole(RoleDTO roleDTO) {

        return createRole(roleDTO.getRoleName());

    }

    public Role createRole(String name) {
        return Optional.of(name)
                .map(this::formatRoleName)
                .map(roleRepository::findTopByName)
                .filter(e -> !e.isPresent())
                .map(e -> Role.builder().name(formatRoleName(name)).build())
                .map(roleRepository::save)
                .orElseThrow(() -> new ServerSideException("Role already exists"));

    }

    public List<Role> getRoles(List<String> rolesName) {

        List<String> rolesAndDefault = Stream.of(Arrays.asList(REGULAR_USER),
                Optional.ofNullable(rolesName).orElseGet(Collections::emptyList))
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        return Optional.ofNullable(rolesAndDefault)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .map(this::formatRoleName)
                .distinct()
                .collect(Collectors.collectingAndThen(Collectors.toList(), Optional::ofNullable))
                .filter(e -> !e.isEmpty())
                .map(roleRepository::findAllByNameIn)
                .orElseGet(Collections::emptyList);

    }

    public User affectRoleToUser(User user, String name) {

        Optional.of(name)
                .map(this::formatRoleName)
                .flatMap(roleRepository::findTopByName)
                .ifPresent(e -> user.setRoles(
                        Stream.of(Arrays.asList(e), user.getRoles())
                                .flatMap(List::stream)
                                .distinct()
                                .collect(Collectors.toList())));
        return userRepository.save(user);

    }

    public User revokeRoleToUser(User user, String name) {

        Optional.of(name)
                .map(this::formatRoleName)
                .flatMap(roleRepository::findTopByName)
                .ifPresent(e -> user.setRoles(

                        Stream.of(user.getRoles())
                                .flatMap(List::stream)
                                .filter(r -> !r.getId().equals(e.getId()))
                                .distinct()
                                .collect(Collectors.toList())));
        return userRepository.save(user);

    }

}
