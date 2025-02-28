package com.yatmk.persistence.presentation;

import java.util.Optional;

import com.yatmk.persistence.exception.config.ServerSideException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiDownloadInput {

  private byte[] bytes;

  private String fileName;

  private String ext;

  public String getValidName() {

    return Optional.ofNullable(fileName)
        .map(String::trim)
        .filter(e -> !e.isEmpty())
        .map(e -> e.concat(rightExt()))
        .orElseThrow(() -> new ServerSideException("a name must be given to the file"));
  }

  public String rightExt() {
    return Optional.ofNullable(ext).map(String::trim).map("."::concat).orElseGet(String::new);
  }
}
