package com.yatmk.infrastructure.services;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import com.yatmk.common.utility.FileUtility;
import com.yatmk.persistence.dto.FileDTO;
import com.yatmk.persistence.dto.ProductDTO;
import com.yatmk.persistence.dto.ProductSearchDTO;
import com.yatmk.persistence.dto.ProductUpdateDTO;
import com.yatmk.persistence.exception.config.ClientSideException;
import com.yatmk.persistence.exception.config.ResourceNotFoundException;
import com.yatmk.persistence.models.Product;
import com.yatmk.persistence.models.attachement.Image;
import com.yatmk.persistence.models.attachement.Video;
import com.yatmk.persistence.models.enums.FileExtension;
import com.yatmk.persistence.repositories.ProductRepository;
import com.yatmk.persistence.specification.ProductSpecification;

import lombok.RequiredArgsConstructor;

@Validated
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  private final ProductSpecification productSpecification;

  private final AttachementService attachementService;

  private final ModelMapper modelMapper;

  public Page<Product> search(ProductSearchDTO searchDTO, Pageable pageable) {
    return productRepository.findAll(productSpecification.searchRequest(searchDTO), pageable);
  }

  public Product get(String id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("product does not exist"));
  }

  public List<Product> getByIdsIn(List<String> ids) {
    return productRepository.findAllByIdIn(ids);
  }

  public Product createProduct(ProductDTO productDTO) {

    Product product = modelMapper.map(productDTO, Product.class);

    Optional.ofNullable(productDTO.getImage())
        .filter(e -> FileExtension.getByValue(FileUtility.getExtention(e.getOriginalFilename())).getType().isImage())
        .ifPresent(e -> setImage(e, product));

    Optional.ofNullable(productDTO.getVideo())
        .filter(e -> FileExtension.getByValue(FileUtility.getExtention(e.getOriginalFilename())).getType().isVideo())
        .ifPresent(e -> setVideo(e, product));

    return productRepository.save(product);
  }

  public void setImage(@Valid @NotNull MultipartFile file, @Valid @NotNull Product product) {
    Image image = new Image();
    image.setProduct(product);
    product.setImage(image);
    attachementService.uploadAttachement(file, image);
  }

  public void setVideo(@Valid @NotNull MultipartFile file, @Valid @NotNull Product product) {
    Video video = new Video();
    video.setProduct(product);
    product.setVideo(video);
    attachementService.uploadAttachement(file, video);

  }

  public void delete(String id) {

    Product product = get(id);
    attachementService.deleteAttachement(product.getVideo());
    attachementService.deleteAttachement(product.getImage());
    productRepository.delete(product);

  }

  public Product update(String id, ProductUpdateDTO productUpdateDTO) {

    Product old = get(id);
    modelMapper.map(productUpdateDTO, old);
    return productRepository.save(old);

  }

  public Optional<Product> updateProductImage(@Valid @NotNull @NotEmpty String productId,
      @Valid @NotNull FileDTO fileDTO) {
    if (!FileExtension.getByValue(FileUtility.getExtention(fileDTO.getFile().getOriginalFilename())).getType()
        .isImage()) {
      throw new ClientSideException("the uploaded attachement is not an image");
    }
    return productRepository.findById(productId)
        .map(e -> reSetImageForProduct(fileDTO.getFile(), e))
        .orElse(Optional.empty());

  }

  public Optional<Product> updateProductVideo(@Valid @NotNull @NotEmpty String productId,
      @Valid @NotNull FileDTO fileDTO) {
    if (!FileExtension.getByValue(FileUtility.getExtention(fileDTO.getFile().getOriginalFilename())).getType()
        .isVideo()) {
      throw new ClientSideException("the uploaded attachement is not a video");
    }
    return productRepository.findById(productId)
        .map(e -> reSetVideoForProduct(fileDTO.getFile(), e))
        .orElse(Optional.empty());

  }

  public Optional<Product> reSetImageForProduct(@Valid @NotNull MultipartFile file, @Valid @NotNull Product product) {

    attachementService.deleteAttachement(product.getImage());
    setImage(file, product);
    return Optional.of(productRepository.save(product));

  }

  public Optional<Product> reSetVideoForProduct(@Valid @NotNull MultipartFile file, @Valid @NotNull Product product) {

    attachementService.deleteAttachement(product.getVideo());
    setVideo(file, product);
    return Optional.of(productRepository.save(product));

  }

}
