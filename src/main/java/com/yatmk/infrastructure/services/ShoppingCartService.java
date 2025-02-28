package com.yatmk.infrastructure.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.yatmk.persistence.dto.CartItemsDTO;
import com.yatmk.persistence.dto.ShoppingCartDTO;
import com.yatmk.persistence.dto.ShoppingCartDTO.SubShoppingCartDTO;
import com.yatmk.persistence.exception.config.ResourceNotFoundException;
import com.yatmk.persistence.models.CartItem;
import com.yatmk.persistence.models.Product;
import com.yatmk.persistence.models.ShoppingCart;
import com.yatmk.persistence.models.User;
import com.yatmk.persistence.repositories.CartItemRepository;
import com.yatmk.persistence.repositories.ShoppingCartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

  private final ShoppingCartRepository shoppingCartRepository;

  private final ProductService productService;

  private final CartItemRepository cartItemRepository;

  private final UserService userService;

  public Page<ShoppingCart> getMyShoppingCarts(Pageable pageable) {
    User user = userService.getCurrentUser();
    return shoppingCartRepository.findAllByUser(user, pageable).map(this::calculateShopingCart);
  }

  public ShoppingCart getShoppingCart(String id) {
    User user = userService.getCurrentUser();
    ShoppingCart shoppingCart = shoppingCartRepository.findById(id)
        .filter(e -> e.getUser().getId().equals(user.getId()))
        .orElseThrow(() -> new ResourceNotFoundException("ShoppingCart does not exist"));
    return calculateShopingCart(shoppingCart);
  }

  public CartItem createCartItems(ShoppingCart shoppingCart, SubShoppingCartDTO subShoppingCartDTO) {

    Product product = productService.get(subShoppingCartDTO.getProductId());
    CartItem cartItem = new CartItem();
    cartItem.setShoppingCart(shoppingCart);
    cartItem.setQuantity(subShoppingCartDTO.getQuantity());
    cartItem.setProduct(product);

    return cartItem;
  }

  public ShoppingCart createShoppingCart(ShoppingCartDTO shoppingCartDTO) {
    User user = userService.getCurrentUser();
    ShoppingCart shoppingCart = new ShoppingCart();
    shoppingCart.setUser(user);
    List<CartItem> cartItems = Optional.ofNullable(shoppingCartDTO)
        .map(ShoppingCartDTO::getSubShoppingCartDTOs)
        .orElseGet(Collections::emptyList)
        .stream()
        .map(e -> createCartItems(shoppingCart, e))
        .collect(Collectors.toList());
    shoppingCart.setCartItems(cartItems);
    shoppingCartRepository.save(shoppingCart);
    return calculateShopingCart(shoppingCart);

  }

  public ShoppingCart addToExistingShoppingCart(String id, ShoppingCartDTO shoppingCartDTO) {

    ShoppingCart shoppingCart = getShoppingCart(id);

    List<CartItem> cartItems = Optional.ofNullable(shoppingCartDTO)
        .map(ShoppingCartDTO::getSubShoppingCartDTOs)
        .orElseGet(Collections::emptyList)
        .stream()
        .map(e -> createCartItems(shoppingCart, e))
        .collect(Collectors.collectingAndThen(Collectors.toList(), Optional::ofNullable))
        .map(cartItemRepository::saveAll)
        .orElseGet(Collections::emptyList);

    shoppingCart.setCartItems(Stream.of(
        cartItems, shoppingCart.getCartItems())
        .flatMap(List::stream)
        .collect(Collectors.toList()));

    shoppingCartRepository.save(shoppingCart);
    return calculateShopingCart(shoppingCart);
  }

  public void deleteShoppingCart(String id) {

    ShoppingCart shoppingCart = getShoppingCart(id);
    shoppingCartRepository.delete(shoppingCart);

  }

  public ShoppingCart removeFromExistingShoppingCart(String id, CartItemsDTO cartItemsDTO) {
    ShoppingCart shoppingCart = getShoppingCart(id);

    List<String> cartItemsIds = Optional.ofNullable(cartItemsDTO)
        .map(CartItemsDTO::getCartItemsIds)
        .orElseGet(Collections::emptyList);
    shoppingCart.getCartItems()
        .stream()
        .filter(e -> cartItemsIds.contains(e.getId()))
        .collect(Collectors.collectingAndThen(Collectors.toList(), Optional::ofNullable))
        .ifPresent(cartItemRepository::deleteAll);
    shoppingCart.getCartItems().removeIf(e -> cartItemsIds.contains(e.getId()));
    shoppingCartRepository.save(shoppingCart);
    return calculateShopingCart(shoppingCart);

  }

  public ShoppingCart calculateShopingCart(ShoppingCart shoppingCart) {
    shoppingCart.setTotalPrice(shoppingCart.getCartItems()
        .stream()
        .map(e -> e.getProduct().getPrice() * e.getQuantity())
        .reduce(0.0, Double::sum));

    shoppingCart.setTotalItems(shoppingCart.getCartItems().stream().mapToLong(CartItem::getQuantity).sum());
    return shoppingCart;
  }

}