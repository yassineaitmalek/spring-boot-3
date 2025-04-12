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

import com.yatmk.persistence.models.CartItem;
import com.yatmk.persistence.models.Product;
import com.yatmk.persistence.models.ShoppingCart;
import com.yatmk.persistence.models.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ShoppingCartRepositoryTest {

  @Autowired

  private ShoppingCartRepository shoppingCartRepository;

  @Autowired

  private ProductRepository productRepository;

  @Autowired
  private CartItemRepository cartItemRepository;

  @Autowired

  private UserRepository userRepository;

  private Product product1;

  private Product product2;

  private Product product3;

  private Product product4;

  private User user1;

  private User user2;

  private ShoppingCart shoppingCart1;

  private ShoppingCart shoppingCart2;

  private ShoppingCart shoppingCart3;

  private CartItem cartItem1;

  private CartItem cartItem2;

  private CartItem cartItem3;

  private CartItem cartItem4;

  private String product1String;

  private String product2String;

  private String product3String;

  private String product4String;

  private String user1String;

  private String user2String;

  @BeforeEach
  void setup() {

    cartItemRepository.deleteAll();
    shoppingCartRepository.deleteAll();
    productRepository.deleteAll();
    userRepository.deleteAll();

    this.product1String = "product1";
    this.product2String = "product2";
    this.product3String = "product3";
    this.product4String = "product4";
    this.user1String = "user1@example.com";
    this.user2String = "user2@example.com";

    this.product1 = Product.builder().name(product1String).build();
    this.product2 = Product.builder().name(product2String).build();
    this.product3 = Product.builder().name(product3String).build();
    this.product4 = Product.builder().name(product4String).build();
    productRepository.saveAll(Arrays.asList(product1, product2, product3, product4));

    this.user1 = User.builder().email(user1String).build();
    this.user2 = User.builder().email(user2String).build();
    userRepository.saveAll(Arrays.asList(user1, user2));

    this.shoppingCart1 = ShoppingCart.builder().user(user1)
        .build();
    this.shoppingCart2 = ShoppingCart.builder().user(user1)
        .build();
    this.shoppingCart3 = ShoppingCart.builder().user(user2)
        .build();

    this.cartItem1 = CartItem.builder().product(product1).shoppingCart(shoppingCart1).build();
    this.cartItem2 = CartItem.builder().product(product2).shoppingCart(shoppingCart1).build();
    shoppingCart1.setCartItems(Arrays.asList(cartItem1, cartItem2));
    this.cartItem3 = CartItem.builder().product(product3).shoppingCart(shoppingCart2).build();
    shoppingCart2.setCartItems(Arrays.asList(cartItem3));
    this.cartItem4 = CartItem.builder().product(product4).shoppingCart(shoppingCart3).build();
    shoppingCart3.setCartItems(Arrays.asList(cartItem4));

    shoppingCartRepository.saveAll(Arrays.asList(shoppingCart1, shoppingCart2, shoppingCart3));

  }

  @Test
  void testFindAll() {

    assertFalse(shoppingCartRepository.findAll().isEmpty());
  }

  @Test
  void testFindAllByUser() {

    Page<ShoppingCart> shoppingCarts = shoppingCartRepository.findAllByUser(user1, Pageable.unpaged());

    assertFalse(shoppingCarts.isEmpty());

    assertTrue(shoppingCarts.get().toList().stream().map(ShoppingCart::getUser).map(User::getEmail)
        .distinct()
        .toList().containsAll(Arrays.asList(user1String)));

    assertFalse(shoppingCarts.get().toList().stream().map(ShoppingCart::getUser).map(User::getEmail)
        .distinct()
        .toList().containsAll(Arrays.asList(user2String)));

    assertTrue(shoppingCarts.get().toList().stream().map(ShoppingCart::getCartItems)
        .flatMap(List::stream)
        .map(CartItem::getProduct)
        .map(Product::getName)
        .distinct()
        .toList().containsAll(Arrays.asList(product1String, product2String, product3String)));

    assertFalse(shoppingCarts.get().toList().stream().map(ShoppingCart::getCartItems)
        .flatMap(List::stream)
        .map(CartItem::getProduct)
        .map(Product::getName)
        .distinct()
        .toList().containsAll(Arrays.asList(product4String)));

  }

}
