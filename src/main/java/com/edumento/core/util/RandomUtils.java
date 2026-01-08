package com.edumento.core.util;

import org.apache.commons.lang3.RandomStringUtils;

/** Utility class for generating random Strings. */
public final class RandomUtils {

  private static final int PASS_COUNT = 8;

  private RandomUtils() {}

  /**
   * Generates a password.
   *
   * @return the generated password
   */
  public static String generatePassword() {
    return generateRandomAlphanumeric(PASS_COUNT);
  }

  /**
   * Generates a password.
   *
   * @return the generated password
   */
  public static String generateRandomAlphanumeric(int defCount) {
    return RandomStringUtils.randomAlphanumeric(defCount);
  }

  /**
   * Generates an activation key.
   *
   * @return the generated activation key
   */
  public static String generateActivationKey() {
    return RandomStringUtils.randomNumeric(6);
  }

  /**
   * Generates a reset key.
   *
   * @return the generated reset key
   */
  public static String generateResetKey() {
    return RandomStringUtils.randomNumeric(6);
  }

  public static String genertateRandomColor() {
    var letters = "0123456789ABCDEF".toCharArray();
    var color = new StringBuilder('#');
    for (var i = 0; i < 6; i++) {
      color.append(letters[org.apache.commons.lang3.RandomUtils.nextInt(0, 16)]);
    }
    return color.toString();
  }
}
