package com.yatmk.persistence.models;

import com.yatmk.persistence.exception.config.ServerSideException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Range {

  public static final long PACE = 1024l * 1024l; // 1Mb

  public static final long BAR = 4 * PACE; // 4 Mb

  public static final int BYTE_RANGE = 1024;

  private long start;

  private long end;

  public long len() {
    return (end - start) + 1;
  }

  public String lenStr() {
    return String.valueOf((end - start) + 1);
  }

  public static Range getRange(String range, long fileSize, boolean isImage) {
    // bytes=186253317-49549
    if (range == null) {
      return Range.getRange("bytes=0-" + PACE, fileSize, isImage);
    }
    String[] ranges = range.replace("bytes=", "").trim().split("-");
    long start = checkLong(ranges[0]);
    long end = 0;
    if (isImage) {
      return Range.builder().start(start).end(fileSize - 1).build();
    }
    if (ranges.length > 1) {
      end = checkLong(ranges[1]);
      if (end > fileSize) {
        end = fileSize - 1;
      }
      if ((end - start) > BAR) {
        throw new ServerSideException("Range passed the bar");
      }
      if ((end - start) < 0) {
        throw new ServerSideException("Range out of index");
      }
    } else {
      if ((start + PACE) > fileSize) {
        end = fileSize - 1;
      } else {
        end = start + PACE;
      }

    }
    if (fileSize < end) {
      end = fileSize - 1;
    }

    return Range.builder().start(start).end(end).build();
  }

  public static long checkLong(String s) {

    try {
      long l = Long.parseLong(s);
      if (l < 0) {
        throw new ServerSideException("Error Parsing a String to number expected a positive number");
      }
      return l;
    } catch (Exception e) {
      log.info("expected a positive number and we get this : {}", s);
      throw new ServerSideException("Error Parsing a String to number");
    }

  }

}
