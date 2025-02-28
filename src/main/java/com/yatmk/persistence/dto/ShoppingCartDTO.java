package com.yatmk.persistence.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartDTO {

    private List<SubShoppingCartDTO> subShoppingCartDTOs;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubShoppingCartDTO {
        private String productId;
        private Long quantity;
    }
}
