package com.edumento.content.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.springframework.security.crypto.codec.Hex;

import io.netty.handler.codec.DecoderException;

public class Base64HexUtil {

  /**
   * @param base64 String as short String
   * @return hex String as long String
   */
  public static String base64ToHex(final String base64) {

    // String in = base64.replace("-", "+").replace("_", "/");

    final byte[] decoded = Base64.getDecoder().decode(base64);
    final String hex = String.copyValueOf(Hex.encode(decoded));
    return hex;
  }

  /**
   * from long text to short
   *
   * @param hex String as long String
   * @return base64 String as short String
   * @throws DecoderException
   * @throws UnsupportedEncodingException
   */
  public static String hex2Base64(final String hex) {
    try {
      final byte[] bs = Hex.decode(hex);
      final byte[] retBytes = Base64.getEncoder().encode(bs);
      final String ret = new String(retBytes);
      return ret.replace("+", "-").replace("/", "_");
    } catch (DecoderException e) {
      throw new RuntimeException(e);
    }
  }
}
