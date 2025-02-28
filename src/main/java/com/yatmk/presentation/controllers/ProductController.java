package com.yatmk.presentation.controllers;

import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yatmk.infrastructure.services.ProductService;
import com.yatmk.persistence.dto.FileDTO;
import com.yatmk.persistence.dto.ProductDTO;
import com.yatmk.persistence.dto.ProductSearchDTO;
import com.yatmk.persistence.dto.ProductUpdateDTO;
import com.yatmk.persistence.models.Product;
import com.yatmk.persistence.presentation.ApiDataResponse;
import com.yatmk.presentation.config.AbstractController;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController implements AbstractController {

  private final ProductService productService;

  @PreAuthorize("hasAnyAuthority('ADMIN')")
  @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  public ResponseEntity<ApiDataResponse<Product>> create(@ModelAttribute ProductDTO productDTO) {
    return create(() -> productService.createProduct(productDTO));
  }

  @GetMapping
  public ResponseEntity<ApiDataResponse<Page<Product>>> search(@ModelAttribute ProductSearchDTO productSearchDTO,
      Pageable pageable) {
    return ok(() -> productService.search(productSearchDTO, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiDataResponse<Product>> get(@PathVariable String id) {
    return ok(() -> productService.get(id));
  }

  @PreAuthorize("hasAnyAuthority('ADMIN')")
  @PatchMapping("/{id}")
  public ResponseEntity<ApiDataResponse<Product>> update(@PathVariable String id,
      @RequestBody ProductUpdateDTO productUpdateDTO) {
    return ok(() -> productService.update(id, productUpdateDTO));
  }

  @PreAuthorize("hasAnyAuthority('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    return noContent(() -> productService.delete(id));
  }

  @PreAuthorize("hasAnyAuthority('ADMIN')")
  @PutMapping(value = "/{productId}/photo", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  public ResponseEntity<ApiDataResponse<Optional<Product>>> updateProductPicture(
      @Valid @NotNull @NotEmpty @PathVariable String productId,
      @ModelAttribute FileDTO fileDTO) {

    return ok(() -> productService.updateProductImage(productId, fileDTO));
  }

  @PreAuthorize("hasAnyAuthority('ADMIN')")
  @PutMapping(value = "/{productId}/video", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  public ResponseEntity<ApiDataResponse<Optional<Product>>> updateProductVideo(
      @Valid @NotNull @NotEmpty @PathVariable String productId,
      @ModelAttribute FileDTO fileDTO) {

    return ok(() -> productService.updateProductVideo(productId, fileDTO));
  }

}
