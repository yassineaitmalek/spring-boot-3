package com.yatmk.persistence.repositories;

import org.springframework.stereotype.Repository;

import com.yatmk.persistence.models.Attachement;
import com.yatmk.persistence.repositories.config.AbstractRepository;

@Repository
public interface AttachementRepository extends AbstractRepository<Attachement, String> {

}
