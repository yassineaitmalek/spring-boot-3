package com.yatmk.persistence.exception.file;

import com.yatmk.persistence.exception.config.ServerSideException;

public class FileDeleteException extends ServerSideException {

  /**
   * @param message
   */
  public FileDeleteException() {
    super("error while deleting a file");

  }

}
