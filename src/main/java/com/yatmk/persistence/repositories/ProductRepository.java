package com.yatmk.persistence.repositories;

import java.util.List;

import com.yatmk.persistence.models.Product;
import com.yatmk.persistence.repositories.config.AbstractRepository;

public interface ProductRepository extends AbstractRepository<Product, String> {

  List<Product> findAllByIdIn(List<String> ids);

}
