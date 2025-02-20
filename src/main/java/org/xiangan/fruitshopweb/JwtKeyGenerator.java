package org.xiangan.fruitshopweb;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;

public class JwtKeyGenerator {
	public static void main(String[] args) {
		SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256); // 產生安全密鑰
		String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
		System.out.println("Generated Secure JWT Key: " + base64Key);
	}
}
