package com.edumento.content.util;

import java.util.Base64;

public class Base64Util {

	/**
	 * @param base64 String as short String
	 * @return String as long String
	 */
	public static String base64ToDecode(final String base64) {
		final var decoded = Base64.getDecoder().decode(base64);
		return decoded.toString();
	}

	/**
	 * from long text to short
	 *
	 * @return base64 String as short String
	 */
	public static String base64Encode(final String text) {
		final var retBytes = Base64.getEncoder().encode(text.getBytes());
		return new String(retBytes);
	}
}
