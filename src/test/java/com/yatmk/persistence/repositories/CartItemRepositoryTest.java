package com.yatmk.persistence.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.yatmk.persistence.models.CartItem;
import com.yatmk.persistence.models.Product;
import com.yatmk.persistence.models.ShoppingCart;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CartItemRepositoryTest {

  @Autowired
  private CartItemRepository cartItemRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired

  private ShoppingCartRepository shoppingCartRepository;

  private ShoppingCart shoppingCart1;

  private CartItem cartItem1;

  private CartItem cartItem2;

  private CartItem cartItem3;

  private CartItem cartItem4;

  private Product product1;

  private Product product2;

  private Product product3;

  private Product product4;

  private String product1String;

  private String product2String;

  private String product3String;

  private String product4String;

  @BeforeEach
  void setup() {

    cartItemRepository.deleteAll();
    shoppingCartRepository.deleteAll();
    productRepository.deleteAll();

    this.product1String = "product1";
    this.product2String = "product2";
    this.product3String = "product3";
    this.product4String = "product4";

    this.product1 = Product.builder().name(product1String).build();
    this.product2 = Product.builder().name(product2String).build();
    this.product3 = Product.builder().name(product3String).build();
    this.product4 = Product.builder().name(product4String).build();
    productRepository.saveAll(Arrays.asList(product1, product2, product3, product4));

    this.shoppingCart1 = ShoppingCart.builder().build();
    shoppingCartRepository.saveAll(Arrays.asList(shoppingCart1));

    this.cartItem1 = CartItem.builder().product(product1).shoppingCart(shoppingCart1).build();
    this.cartItem2 = CartItem.builder().product(product2).shoppingCart(shoppingCart1).build();
    this.cartItem3 = CartItem.builder().product(product3).shoppingCart(shoppingCart1).build();
    this.cartItem4 = CartItem.builder().product(product4).shoppingCart(shoppingCart1).build();

    cartItemRepository.saveAll(Arrays.asList(cartItem1, cartItem2, cartItem3, cartItem4));

  }

  @Test
  void testFindAll() {

    assertFalse(cartItemRepository.findAll().isEmpty());
  }

  @Test
  void testFindByIdCartItemWithProduct() {
    Optional<CartItem> cartItem = cartItemRepository.findById(cartItem1.getId());
    assertFalse(cartItem.isEmpty());
    assertNotNull(cartItem.get().getProduct());
    assertNotNull(cartItem.get().getShoppingCart());

  }

  @Test
  void testFindByIdCartItemWithoutProduct() {
    CartItem cartItem = CartItem.builder().product(null).shoppingCart(shoppingCart1).build();
    assertThrows(DataIntegrityViolationException.class, () -> {
      cartItemRepository.save(cartItem);
      cartItemRepository.flush();
    });
  }

  @Test
  void testFindByIdCartItemWithoutShoppingCart() {
    CartItem cartItem = CartItem.builder().product(product1).shoppingCart(null).build();
    assertThrows(DataIntegrityViolationException.class, () -> {
      cartItemRepository.save(cartItem);
      cartItemRepository.flush();
    });
  }

}
