package com.yatmk.persistence.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.yatmk.persistence.models.User;
import com.yatmk.persistence.models.WishList;
import com.yatmk.persistence.repositories.config.AbstractRepository;

@Repository
public interface WishListRepository extends AbstractRepository<WishList, String> {

  Page<WishList> findAllByUser(User user, Pageable pageable);

}
