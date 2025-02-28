package com.yatmk.common.utility;

import io.vavr.control.Try;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CheckUtility {

  private static final DecimalFormat df = getDecimalFormat();

  private static final DecimalFormat df_xls = getDecimalFormatXLS();

  private static final String EMPTY_STRING = "";

  private static String doubleRemoveComma(String doubleString) {
    return doubleString.replace(",", ".");
  }

  public static String formatDecimal(Double d) {
    return Optional.ofNullable(d).map(df::format).map(CheckUtility::doubleRemoveComma).orElse(null);
  }

  public Long doubleToPositiveLong(Double d) {
    return Optional.ofNullable(d)
        .map(Object::toString)
        .map(e -> e.replace("-", EMPTY_STRING))
        .map(e -> e.replace(",", EMPTY_STRING).replace(".", EMPTY_STRING))
        .map(Long::valueOf)
        .orElse(null);
  }

  public String concatString(List<String> strs) {

    return Utils.checkStream(strs).collect(Collectors.joining(", "));
  }

  public static DecimalFormat getDecimalFormat() {
    DecimalFormat df = new DecimalFormat("0.00");
    df.setRoundingMode(RoundingMode.HALF_UP);
    return df;
  }

  public Double round(Double d) {
    return Optional.ofNullable(d)
        .map(df::format)
        .map(CheckUtility::doubleRemoveComma)
        .map(Double::parseDouble)
        .orElse(null);
  }

  public static DecimalFormat getDecimalFormatXLS() {
    DecimalFormat df = new DecimalFormat("#.##");
    df.setRoundingMode(RoundingMode.HALF_UP);
    return df;
  }

  public String checkString(String s) {
    return Optional.ofNullable(s)
        .map(String::trim)
        .filter(Utils.not(Utils.EMPTY_STRING::equals))
        .orElse(null);
  }

  public String formatDouble(Double d) {

    return Optional.ofNullable(d).map(df::format).orElse(null);
  }

  public String formatDoubleXls(Double d) {

    return Optional.ofNullable(d).map(df_xls::format).orElse(null);
  }

  public Long checkLong(String s) {
    return Optional.ofNullable(checkDouble(s)).map(Double::longValue).orElse(null);
  }

  public Integer checkInteger(String s) {

    return Optional.ofNullable(checkDouble(s)).map(Double::intValue).orElse(null);
  }

  public Double checkDouble(String s) {

    return Try.of(() -> checkString(s))
        .filter(Objects::nonNull)
        .map(Double::parseDouble)
        .getOrNull();
  }

  public LocalDate checkDate(String s) {

    return Try.of(() -> checkString(s)).filter(Objects::nonNull).map(LocalDate::parse).getOrNull();
  }

  public String objectToString(Object obj) {
    return Optional.ofNullable(obj).map(Object::toString).orElseGet(String::new);
  }

  public <T> String parseDefaultValue(T obj, T defaultValue) {
    if (Objects.isNull(defaultValue)) {
      throw new IllegalArgumentException("the default value can not be null");
    }
    return Optional.ofNullable(obj).map(Object::toString).orElseGet(defaultValue::toString);
  }
}
