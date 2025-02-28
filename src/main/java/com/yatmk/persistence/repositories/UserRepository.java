package com.yatmk.persistence.repositories;

import java.util.Optional;

import com.yatmk.persistence.models.User;
import com.yatmk.persistence.repositories.config.AbstractRepository;

public interface UserRepository extends AbstractRepository<User, String> {

    Optional<User> findTopByEmail(String email);

}
