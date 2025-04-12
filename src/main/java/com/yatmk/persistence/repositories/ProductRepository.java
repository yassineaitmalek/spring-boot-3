package com.yatmk.persistence.repositories;

import org.springframework.stereotype.Repository;
import com.yatmk.persistence.models.Product;
import com.yatmk.persistence.repositories.config.AbstractRepository;

@Repository
public interface ProductRepository extends AbstractRepository<Product, String> {

}
