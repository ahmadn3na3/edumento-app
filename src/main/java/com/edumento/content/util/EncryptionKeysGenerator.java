package com.edumento.content.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.util.DigestUtils;

public class EncryptionKeysGenerator {

	@SuppressWarnings("resource")
	public static String getVideoKeyId(final File in) {

		try (var is = new FileInputStream(in)) {
			return new String(Hex.encode(DigestUtils.md5Digest(is)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getVideoKey(final String keyId) {
		final var in = "Mint." + keyId + "@Eshraq";
		return new String(Hex.encode(DigestUtils.md5Digest(in.getBytes())));
	}

	public static String getVideoKey(final File in) {
		final var in5 = "Mint." + getVideoKeyId(in) + "@Eshraq";
		return new String(Hex.encode(DigestUtils.md5Digest(in5.getBytes())));
	}
}
