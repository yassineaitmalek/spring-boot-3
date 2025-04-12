package com.yatmk.persistence.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.yatmk.persistence.models.Product;
import com.yatmk.persistence.models.attachement.Image;
import com.yatmk.persistence.models.attachement.Video;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private VideoRepository videoRepository;

  @Autowired
  private ImageRepository imageRepository;

  private Product product1;

  private Product product2;

  @BeforeEach
  void setup() {

    productRepository.deleteAll();
    videoRepository.deleteAll();
    imageRepository.deleteAll();
    this.product1 = Product.builder().name("product1").build();
    Video video1 = new Video();
    video1.setProduct(product1);
    product1.setVideo(video1);
    Image image1 = new Image();
    image1.setProduct(product1);
    product1.setImage(image1);

    this.product2 = Product.builder().name("product2").build();
    productRepository.saveAll(Arrays.asList(product1, product2));

  }

  @Test
  void testFindAll() {

    assertFalse(productRepository.findAll().isEmpty());

  }

  @Test
  void testFindByIdWithImageAndVideoExisting() {

    Optional<Product> product = productRepository.findById(product1.getId());
    assertFalse(product.isEmpty());
    assertNotNull(product.get().getImage());
    assertNotNull(product.get().getVideo());

  }

  @Test
  void testFindByIdWithImageAndVideoNotExisting() {

    Optional<Product> product = productRepository.findById(product2.getId());
    assertFalse(product.isEmpty());
    assertNull(product.get().getImage());
    assertNull(product.get().getVideo());

  }

}
