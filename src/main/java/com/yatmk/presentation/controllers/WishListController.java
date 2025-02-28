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

import com.yatmk.infrastructure.services.WishListService;
import com.yatmk.persistence.dto.WishListDTO;
import com.yatmk.persistence.models.WishList;
import com.yatmk.persistence.presentation.ApiDataResponse;
import com.yatmk.presentation.config.AbstractController;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/wish-lists")
@RequiredArgsConstructor
public class WishListController implements AbstractController {

  private final WishListService wishListService;

  @PostMapping
  public ResponseEntity<ApiDataResponse<WishList>> createWishList(@RequestBody WishListDTO wishListDTO) {
    return create(() -> wishListService.createWishList(wishListDTO));
  }

  @GetMapping
  public ResponseEntity<ApiDataResponse<Page<WishList>>> getWishLists(Pageable pageable) {
    return ok(() -> wishListService.getMyWishLists(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiDataResponse<WishList>> get(@PathVariable String id) {
    return ok(() -> wishListService.getWishList(id));
  }

  @PatchMapping("/{id}/add/{productId}")
  public ResponseEntity<ApiDataResponse<WishList>> updateWishListAdd(@PathVariable String id,
      @PathVariable String productId) {
    return ok(() -> wishListService.addToExistingWishList(id, productId));
  }

  @PatchMapping("/{id}/remove/{productId}")
  public ResponseEntity<ApiDataResponse<WishList>> updateWishListRemove(@PathVariable String id,
      @PathVariable String productId) {
    return ok(() -> wishListService.removeFromExistingWishList(id, productId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    return noContent(() -> wishListService.deleteWishList(id));
  }

}
