package com.yatmk.persistence.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import com.yatmk.persistence.models.config.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class CartItem extends BaseEntity {

  private Long quantity;

  @ManyToOne
  private Product product;

  @JsonIgnore
  @ManyToOne
  private ShoppingCart shoppingCart;

}
