package security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {

	public static String getPasswordHash(String password) {
		byte[] pwHash = null;
		MessageDigest d = null;
		
		try {
			d = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		pwHash = d.digest(password.getBytes(StandardCharsets.UTF_8));
		
		return String.format("%x", new java.math.BigInteger(1, pwHash));
	}
}
