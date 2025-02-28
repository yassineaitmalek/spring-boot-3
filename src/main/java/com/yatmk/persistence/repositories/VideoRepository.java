package com.yatmk.persistence.repositories;

import org.springframework.stereotype.Repository;

import com.yatmk.persistence.models.attachement.Video;
import com.yatmk.persistence.repositories.config.AbstractRepository;

@Repository
public interface VideoRepository extends AbstractRepository<Video, String> {

}
