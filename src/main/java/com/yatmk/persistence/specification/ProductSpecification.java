package com.yatmk.persistence.specification;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.yatmk.persistence.dto.ProductSearchDTO;
import com.yatmk.persistence.models.Product;
import com.yatmk.persistence.models.Product_;
import com.yatmk.persistence.models.enums.InventoryStatus;

@Component
public class ProductSpecification implements AbstractSpecification<Product> {

  private Specification<Product> hasCode(String code) {
    return (root, query, builder) -> builder.like(root.get(Product_.CODE), like(code));
  }

  public Specification<Product> hasName(String name) {
    return (root, query, builder) -> builder.like(root.get(Product_.NAME), like(name));
  }

  public Specification<Product> hasDescription(String description) {
    return (root, query, builder) -> builder.like(root.get(Product_.DESCRIPTION), like(description));
  }

  public Specification<Product> hasCategory(String category) {
    return (root, query, builder) -> builder.like(root.get(Product_.CATEGORY), like(category));
  }

  public Specification<Product> hasPrice(Double price) {
    return (root, query, builder) -> builder.equal(root.get(Product_.PRICE), price);
  }

  public Specification<Product> hasQuantity(Long quantity) {
    return (root, query, builder) -> builder.equal(root.get(Product_.QUANTITY), quantity);
  }

  public Specification<Product> hasInternalReference(String internalReference) {
    return (root, query, builder) -> builder.like(root.get(Product_.INTERNAL_REFERENCE), like(internalReference));
  }

  public Specification<Product> hasShellId(Long shellId) {
    return (root, query, builder) -> builder.equal(root.get(Product_.SHELL_ID), shellId);
  }

  public Specification<Product> hasInventoryStatus(InventoryStatus inventoryStatus) {
    return (root, query, builder) -> builder.equal(root.get(Product_.INVENTORY_STATUS), inventoryStatus);
  }

  public Specification<Product> hasRating(Double rating) {
    return (root, query, builder) -> builder.equal(root.get(Product_.RATING), rating);
  }

  public Specification<Product> searchRequest(ProductSearchDTO search) {

    return Optional.ofNullable(search)
        .map(this::searchRequestImpl)
        .orElseGet(this::unitSpecification);
  }

  private Specification<Product> searchRequestImpl(ProductSearchDTO search) {

    return Stream.of(
        transformer(search.getCode(), this::hasCode),
        transformer(search.getName(), this::hasName),
        transformer(search.getDescription(), this::hasDescription),
        transformer(search.getCategory(), this::hasCategory),
        transformer(search.getPrice(), this::hasPrice),
        transformer(search.getQuantity(), this::hasQuantity),
        transformer(search.getInternalReference(), this::hasInternalReference),
        transformer(search.getShellId(), this::hasShellId),
        transformer(search.getInventoryStatus(), this::hasInventoryStatus),
        transformer(search.getRating(), this::hasRating)

    )
        .filter(Optional::isPresent)
        .map(Optional::get)
        .reduce(Specification.where(distinct()), Specification::and);
  }
}
