package com.yatmk.persistence.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.yatmk.persistence.models.Product;
import com.yatmk.persistence.models.attachement.Video;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class VideoRepositoryTest {

  @Autowired
  private VideoRepository videoRepository;

  @Autowired
  private ProductRepository productRepository;

  private Video video1;

  private Video video2;

  private Product product1;

  @BeforeEach
  void setup() {
    productRepository.deleteAll();
    videoRepository.deleteAll();
    this.video1 = new Video();
    this.video2 = new Video();
    this.product1 = Product.builder().name("product1").build();
    productRepository.save(product1);
    video1.setProduct(product1);
    videoRepository.save(video1);
    videoRepository.save(video2);

  }

  @Test
  void testFindAll() {

    assertFalse(videoRepository.findAll().isEmpty());
  }

  @Test
  void testFindByIdVideoWithProduct() {
    Optional<Video> video = videoRepository.findById(video1.getId());
    assertFalse(video.isEmpty());
    assertNotNull(video.get().getProduct());

  }

  @Test
  void testFindByIdVideoWithoutProduct() {
    Optional<Video> video = videoRepository.findById(video2.getId());
    assertFalse(video.isEmpty());
    assertNull(video.get().getProduct());

  }

}
