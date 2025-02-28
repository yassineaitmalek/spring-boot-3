package com.yatmk.persistence.dto;

import com.yatmk.persistence.models.enums.InventoryStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {

  private String code;

  private String name;

  private String description;

  private String category;

  private Double price;

  private Long quantity;

  private String internalReference;

  private Long shellId;

  private InventoryStatus inventoryStatus;

  private Double rating;

}
