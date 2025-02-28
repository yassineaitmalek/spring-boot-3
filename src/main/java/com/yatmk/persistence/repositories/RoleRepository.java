package com.yatmk.persistence.repositories;

import java.util.List;
import java.util.Optional;

import com.yatmk.persistence.models.Role;
import com.yatmk.persistence.repositories.config.AbstractRepository;

public interface RoleRepository extends AbstractRepository<Role, String> {

    Optional<Role> findTopByName(String name);

    List<Role> findAllByNameIn(List<String> values);

}
