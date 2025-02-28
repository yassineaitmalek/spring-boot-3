package com.yatmk.persistence.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.yatmk.persistence.models.User;
import com.yatmk.persistence.models.ShoppingCart;
import com.yatmk.persistence.repositories.config.AbstractRepository;

@Repository
public interface ShoppingCartRepository extends AbstractRepository<ShoppingCart, String> {

  Page<ShoppingCart> findAllByUser(User user, Pageable pageable);

}
