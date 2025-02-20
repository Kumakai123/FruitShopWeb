package org.xiangan.fruitshopweb.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

	private static final long EXPIRATION_TIME = 3600000; // 1 小時 (3600秒)
	private SecretKey SECRET_KEY;

	@PostConstruct
	public void init() {
		// 產生安全的 Base64 密鑰
		String base64Key = "GjDCrTPK8jsBihGyjBcVBSiMUzLhotGPmknZP6sD10E="; // 先產生這個密鑰
		byte[] decodedKey = Base64.getDecoder().decode(base64Key);
		SECRET_KEY = Keys.hmacShaKeyFor(decodedKey);
	}

	// 🟢 產生 JWT Token
	public String generateToken(String username) {
		return Jwts.builder()
			.setSubject(username)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
			.signWith(SECRET_KEY, SignatureAlgorithm.HS256)
			.compact();
	}

	// 取得使用者名稱 (從 Token)
	public String getUsernameFromToken(String token) {
		return parseClaims(token).getSubject();
	}

	// 🛡️ 驗證 JWT 是否有效
	public boolean isTokenValid(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (ExpiredJwtException e) {
			System.err.println("Token 已過期");
		} catch (JwtException e) {
			System.err.println("Token 驗證失敗");
		}
		return false;
	}

	// 🔍 解析 JWT
	private Claims parseClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(SECRET_KEY)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}
}
