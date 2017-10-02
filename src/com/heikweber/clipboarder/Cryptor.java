package com.heikweber.clipboarder;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author Philipp, David
 *
 *         encrypt and decrypt text
 * 
 *         copied and altered from
 *         https://blog.axxg.de/java-aes-verschluesselung-mit-beispiel/
 *
 */

public class Cryptor {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static String encrypt(String text, String keyStr) throws Exception {

		// Encryption
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, createKey(keyStr));
		byte[] encrypted = cipher.doFinal(text.getBytes());

		// bytes zu Base64-String konvertieren (dient der Lesbarkeit)
		BASE64Encoder myEncoder = new BASE64Encoder();
		String secret = myEncoder.encode(encrypted);

		// result
		return secret;
	}

	private static SecretKeySpec createKey(String keyStr) throws Exception {
		// create byte-Array with keyStr being the password for encryption
		byte[] key = (keyStr).getBytes("UTF-8");
		// aus dem Array einen Hash-Wert erzeugen mit MD5 oder SHA
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		key = sha.digest(key);
		// nur die ersten 128 bit nutzen
		key = Arrays.copyOf(key, 16);
		// der fertige Schluessel
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

		return secretKeySpec;
	}

	public static String decrypt(String secret, String keyStr) throws Exception {

		// BASE64 String zu Byte-Array konvertieren
		BASE64Decoder myDecoder2 = new BASE64Decoder();
		byte[] crypted2 = myDecoder2.decodeBuffer(secret);

		// Decryption
		Cipher cipher2 = Cipher.getInstance("AES");
		cipher2.init(Cipher.DECRYPT_MODE, createKey(keyStr));
		byte[] cipherData2 = cipher2.doFinal(crypted2);
		String result = new String(cipherData2);

		return result;
	}

}
