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
import com.yatmk.persistence.models.attachement.Image;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ImageRepositoryTest {

  @Autowired
  private ImageRepository imageRepository;

  @Autowired
  private ProductRepository productRepository;

  private Image image1;

  private Image image2;

  private Product product1;

  @BeforeEach
  void setup() {
    productRepository.deleteAll();
    imageRepository.deleteAll();
    this.image1 = new Image();
    this.image2 = new Image();
    this.product1 = Product.builder().name("product1").build();
    productRepository.save(product1);
    image1.setProduct(product1);
    imageRepository.save(image1);
    imageRepository.save(image2);

  }

  @Test
  void testFindAll() {

    assertFalse(imageRepository.findAll().isEmpty());
  }

  @Test
  void testFindByIdImageWithProduct() {
    Optional<Image> image = imageRepository.findById(image1.getId());
    assertFalse(image.isEmpty());
    assertNotNull(image.get().getProduct());

  }

  @Test
  void testFindByIdImageWithoutProduct() {
    Optional<Image> image = imageRepository.findById(image2.getId());
    assertFalse(image.isEmpty());
    assertNull(image.get().getProduct());

  }

}
