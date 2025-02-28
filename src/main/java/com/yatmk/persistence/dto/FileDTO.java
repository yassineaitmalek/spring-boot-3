package com.yatmk.persistence.dto;

import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.yatmk.persistence.validation.FileDTOValidator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@FileDTOValidator
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {

  @NotNull
  private MultipartFile file;

}
