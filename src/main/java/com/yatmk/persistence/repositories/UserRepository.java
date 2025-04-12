package com.yatmk.persistence.repositories;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.yatmk.persistence.models.User;
import com.yatmk.persistence.repositories.config.AbstractRepository;

@Repository
public interface UserRepository extends AbstractRepository<User, String> {

    Optional<User> findTopByEmail(String email);

}
