package com.yatmk.infrastructure.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.yatmk.persistence.dto.WishListDTO;
import com.yatmk.persistence.exception.config.ResourceNotFoundException;
import com.yatmk.persistence.models.Product;
import com.yatmk.persistence.models.User;
import com.yatmk.persistence.models.WishList;
import com.yatmk.persistence.repositories.WishListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishListService {

  private final WishListRepository wishListRepository;

  private final UserService userService;

  private final ProductService productService;

  public Page<WishList> getMyWishLists(Pageable pageable) {
    User user = userService.getCurrentUser();
    return wishListRepository.findAllByUser(user, pageable);
  }

  public WishList getWishList(String id) {
    User user = userService.getCurrentUser();
    return wishListRepository.findById(id)
        .filter(e -> e.getUser().getId().equals(user.getId()))
        .orElseThrow(() -> new ResourceNotFoundException("wishList does not exist"));
  }

  public WishList createWishList(WishListDTO wishListDTO) {
    User user = userService.getCurrentUser();
    WishList wishlist = new WishList();
    wishlist.setName(wishListDTO.getName());
    wishlist.setUser(user);
    wishlist.setProducts(productService.getByIdsIn(wishListDTO.getProductIds()));
    return wishListRepository.save(wishlist);

  }

  public WishList addToExistingWishList(String id, String productId) {

    WishList wishList = getWishList(id);
    Product product = productService.get(productId);

    boolean exists = Optional.ofNullable(wishList)
        .map(WishList::getProducts)
        .orElseGet(Collections::emptyList)
        .stream()
        .map(Product::getId)
        .anyMatch(productId::equals);

    if (exists) {
      return wishList;
    }
    Optional.ofNullable(wishList)
        .map(WishList::getProducts)
        .ifPresent(e -> e.add(product));

    return wishListRepository.save(wishList);

  }

  public void deleteWishList(String id) {

    WishList wishList = getWishList(id);
    wishListRepository.delete(wishList);

  }

  public WishList removeFromExistingWishList(String id, String productId) {
    WishList wishList = getWishList(id);
    Product product = productService.get(productId);
    boolean exists = Optional.ofNullable(wishList)
        .map(WishList::getProducts)
        .orElseGet(Collections::emptyList)
        .stream()
        .map(Product::getId)
        .anyMatch(e -> product.getId().equals(e));

    if (!exists) {
      return wishList;
    }
    List<Product> products = Optional.ofNullable(wishList)
        .map(WishList::getProducts)
        .map(e -> e.stream().filter(p -> !p.getId().equals(productId)).collect(Collectors.toList()))
        .orElseGet(Collections::emptyList);
    wishList.setProducts(products);
    return wishListRepository.save(wishList);
  }

}