package com.yatmk.persistence.models.enums;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InventoryStatus {

  INSTOCK(1l, "In Stock"),
  LOWSTOCK(2l, "Low Stock"),
  OUTOFSTOCK(3l, "Out of Stock");

  private final Long code;

  private final String label;

  public static Optional<InventoryStatus> of(Long code) {

    return Stream.of(values())
        .filter(e -> e.getCode().equals(code))
        .findFirst();

  }

  public static Optional<InventoryStatus> of(String label) {

    return Stream.of(values())
        .filter(e -> e.getLabel().equalsIgnoreCase(label))
        .findFirst();

  }
}
