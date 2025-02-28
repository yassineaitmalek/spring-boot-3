package com.yatmk.persistence.models.attachement;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

import com.yatmk.persistence.models.Attachement;
import com.yatmk.persistence.models.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Video extends Attachement {

  @ToString.Exclude
  @JsonIgnore
  @OneToOne
  private Product product;

}
