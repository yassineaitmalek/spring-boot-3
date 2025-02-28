package com.yatmk.persistence.exception.file;

import com.yatmk.persistence.exception.config.ServerSideException;

public class FileUploadException extends ServerSideException {

  /**
   * @param message
   */
  public FileUploadException() {
    super("error could not upload File");

  }

}
