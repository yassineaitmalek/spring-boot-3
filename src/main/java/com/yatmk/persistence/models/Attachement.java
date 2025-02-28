package com.yatmk.persistence.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

import com.yatmk.persistence.models.config.BaseEntity;
import com.yatmk.persistence.models.enums.FileExtension;
import com.yatmk.persistence.models.enums.FileType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Attachement extends BaseEntity {

  @Schema(description = "Extention of the file")
  private String ext;

  @Schema(description = "Extention type of the file")
  private FileExtension extension;

  @Schema(description = "Original name of the file")
  private String originalFileName;

  @Schema(description = "Path where the file is stored")
  private String path;

  @Schema(description = "Size of the file")
  private Long fileSize;

  @Schema(description = "Type of the file")
  private FileType fileType;

}
