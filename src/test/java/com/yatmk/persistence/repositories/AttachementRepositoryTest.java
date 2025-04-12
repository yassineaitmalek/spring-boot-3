package com.yatmk.persistence.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.yatmk.persistence.models.Attachement;
import com.yatmk.persistence.models.attachement.Image;
import com.yatmk.persistence.models.attachement.Video;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class AttachementRepositoryTest {

  @Autowired
  private AttachementRepository attachementRepository;

  private Attachement video;

  private Attachement image;

  @BeforeEach
  void setup() {
    attachementRepository.deleteAll();
    this.video = new Video();
    this.image = new Image();
    attachementRepository.save(video);
    attachementRepository.save(image);

  }

  @Test
  void testFindAll() {

    assertFalse(attachementRepository.findAll().isEmpty());
  }

  @Test
  void testFindByIdVideo() {
    Optional<Attachement> attachement = attachementRepository.findById(video.getId());
    assertFalse(attachement.isEmpty());
    assertTrue(Video.class.isInstance(attachement.get()));
  }

  @Test
  void testFindByIdImage() {
    Optional<Attachement> attachement = attachementRepository.findById(image.getId());
    assertFalse(attachement.isEmpty());
    assertTrue(Image.class.isInstance(attachement.get()));
  }
}
