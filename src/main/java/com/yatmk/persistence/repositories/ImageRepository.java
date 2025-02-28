package com.yatmk.persistence.repositories;

import org.springframework.stereotype.Repository;

import com.yatmk.persistence.models.attachement.Image;
import com.yatmk.persistence.repositories.config.AbstractRepository;

@Repository
public interface ImageRepository extends AbstractRepository<Image, String> {

}
