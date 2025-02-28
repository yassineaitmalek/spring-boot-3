package com.yatmk.persistence.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

import com.yatmk.persistence.models.attachement.Image;
import com.yatmk.persistence.models.attachement.Video;
import com.yatmk.persistence.models.config.BaseEntity;
import com.yatmk.persistence.models.enums.InventoryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {

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

  @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
  private Image image;

  @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
  private Video video;
}
