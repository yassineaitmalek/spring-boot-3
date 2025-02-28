package com.yatmk.persistence.exception.file;

import com.yatmk.persistence.exception.config.ServerSideException;

public class FileUnStreamableException extends ServerSideException {

  /**
   * @param message
   */
  public FileUnStreamableException() {
    super("error the file is unstreamable");

  }

}
