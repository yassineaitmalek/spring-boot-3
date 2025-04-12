package com.yatmk.persistence.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.yatmk.persistence.models.Product;
import com.yatmk.persistence.models.User;
import com.yatmk.persistence.models.WishList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class WishListRepositoryTest {

  @Autowired

  private WishListRepository wishListRepository;

  @Autowired

  private ProductRepository productRepository;

  @Autowired

  private UserRepository userRepository;

  private Product product1;

  private Product product2;

  private Product product3;

  private Product product4;

  private User user1;

  private User user2;

  private WishList wishList1;

  private WishList wishList2;

  private WishList wishList3;

  private String product1String;

  private String product2String;

  private String product3String;

  private String product4String;

  private String user1String;

  private String user2String;

  private String wishList1String;

  private String wishList2String;

  private String wishList3String;

  @BeforeEach
  void setup() {

    wishListRepository.deleteAll();
    productRepository.deleteAll();
    userRepository.deleteAll();

    this.product1String = "product1";
    this.product2String = "product2";
    this.product3String = "product3";
    this.product4String = "product4";
    this.user1String = "user1@example.com";
    this.user2String = "user2@example.com";
    this.wishList1String = "wishList1";
    this.wishList2String = "wishList2";
    this.wishList3String = "wishList3";

    this.product1 = Product.builder().name(product1String).build();
    this.product2 = Product.builder().name(product2String).build();
    this.product3 = Product.builder().name(product3String).build();
    this.product4 = Product.builder().name(product4String).build();

    this.user1 = User.builder().email(user1String).build();
    this.user2 = User.builder().email(user2String).build();

    productRepository.saveAll(Arrays.asList(product1, product2, product3, product4));
    userRepository.saveAll(Arrays.asList(user1, user2));

    this.wishList1 = WishList.builder().name(
        wishList1String).products(Arrays.asList(product1, product2)).user(user1)
        .build();
    this.wishList2 = WishList.builder().name(wishList2String).products(Arrays.asList(product3)).user(user1).build();
    this.wishList3 = WishList.builder().name(wishList3String).products(Arrays.asList(product4)).user(user2).build();
    wishListRepository.saveAll(Arrays.asList(wishList1, wishList2, wishList3));

  }

  @Test
  void testFindAll() {

    assertFalse(wishListRepository.findAll().isEmpty());
  }

  @Test
  void testFindAllByUser() {

    Page<WishList> wishLists = wishListRepository.findAllByUser(user1, Pageable.unpaged());

    assertFalse(wishLists.isEmpty());

    assertTrue(wishLists.get().toList().stream().map(WishList::getName).toList()
        .containsAll(Arrays.asList(wishList2String, wishList1String)));

    assertFalse(wishLists.get().toList().stream().map(WishList::getName).toList()
        .containsAll(Arrays.asList(wishList3String)));

    assertTrue(wishLists.get().toList().stream().map(WishList::getUser).map(User::getEmail)
        .distinct()
        .toList().containsAll(Arrays.asList(user1String)));

    assertFalse(wishLists.get().toList().stream().map(WishList::getUser).map(User::getEmail)
        .distinct()
        .toList().containsAll(Arrays.asList(user2String)));

    assertTrue(wishLists.get().toList().stream().map(WishList::getProducts)
        .flatMap(List::stream)
        .map(Product::getName)
        .distinct()
        .toList().containsAll(Arrays.asList(product1String, product2String,
            product3String)));

    assertFalse(wishLists.get().toList().stream().map(WishList::getProducts)
        .flatMap(List::stream)
        .map(Product::getName)
        .distinct()
        .toList().containsAll(Arrays.asList(product4String)));

  }

}
