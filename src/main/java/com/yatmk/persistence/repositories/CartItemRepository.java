package com.yatmk.persistence.repositories;

import org.springframework.stereotype.Repository;

import com.yatmk.persistence.models.CartItem;
import com.yatmk.persistence.repositories.config.AbstractRepository;

@Repository
public interface CartItemRepository extends AbstractRepository<CartItem, String> {

}
