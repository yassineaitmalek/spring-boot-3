package com.yatmk.presentation.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yatmk.infrastructure.services.ShoppingCartService;
import com.yatmk.persistence.dto.CartItemsDTO;
import com.yatmk.persistence.dto.ShoppingCartDTO;
import com.yatmk.persistence.models.ShoppingCart;
import com.yatmk.persistence.presentation.ApiDataResponse;
import com.yatmk.presentation.config.AbstractController;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/shopping-carts")
@RequiredArgsConstructor
public class ShoppingCartController implements AbstractController {

  private final ShoppingCartService shoppingCartService;

  @PostMapping
  public ResponseEntity<ApiDataResponse<ShoppingCart>> createShoppingCart(@RequestBody ShoppingCartDTO wishListDTO) {
    return create(() -> shoppingCartService.createShoppingCart(wishListDTO));
  }

  @GetMapping
  public ResponseEntity<ApiDataResponse<Page<ShoppingCart>>> getShoppingCarts(Pageable pageable) {
    return ok(() -> shoppingCartService.getMyShoppingCarts(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiDataResponse<ShoppingCart>> get(@PathVariable String id) {
    return ok(() -> shoppingCartService.getShoppingCart(id));
  }

  @PatchMapping("/{id}/add")
  public ResponseEntity<ApiDataResponse<ShoppingCart>> updateShoppingCartAdd(@PathVariable String id,
      @RequestBody ShoppingCartDTO shoppingCartDTO) {
    return ok(() -> shoppingCartService.addToExistingShoppingCart(id, shoppingCartDTO));
  }

  @PatchMapping("/{id}/remove")
  public ResponseEntity<ApiDataResponse<ShoppingCart>> updateShoppingCartRemove(@PathVariable String id,
      @RequestBody CartItemsDTO cartItemsDTO) {
    return ok(() -> shoppingCartService.removeFromExistingShoppingCart(id, cartItemsDTO));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    return noContent(() -> shoppingCartService.deleteShoppingCart(id));
  }

}
