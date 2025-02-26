package org.xiangan.fruitshopweb.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 驗證與管理服務。
 * <p>
 * 負責處理 JSON Web Token (JWT) 的生成、解析與驗證，提供 Spring Security 的身份驗證機制。
 * </p>
 *
 * <h2>功能說明：</h2>
 * <ul>
 *   <li>生成 JWT Token，內含使用者資訊與有效期限</li>
 *   <li>從 Token 解析使用者名稱與 Claims</li>
 *   <li>驗證 Token 是否有效（比對使用者並檢查是否過期）</li>
 * </ul>
 *
 * <h3>安全性說明：</h3>
 * 採用 HMAC-SHA256 (`HS256`) 進行簽名，並使用 Base64 編碼的密鑰 (`jwt.secret`) 來初始化 HMAC 簽名密鑰。
 *
 * @author kyle
 */
@Service
@Slf4j
public class JwtService {

	/** Token 有效期限 (1 小時) */
	private static final long EXPIRATION_TIME = 60 * 60 * 1000; // 單位 ms

	/** 密鑰 */
	private final Key SECRET_KEY;

	/**
	 * 透過 `@Value` 讀取 Base64 密鑰，並在建構子中初始化 `SECRET_KEY`。
	 *
	 * @param base64Key Base64 編碼的 JWT 簽名密鑰
	 */
	public JwtService(@Value("${jwt.secret}") String base64Key) {
		byte[] decodedKey = Base64.getDecoder().decode(base64Key);
		this.SECRET_KEY = Keys.hmacShaKeyFor(decodedKey);
	}

	/**
	 * 從 Token 中提取使用者名稱
	 *
	 * @param token JWT Token
	 * @return 使用者名稱
	 */

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * 從 JWT Token 解析特定的 Claim
	 *
	 * @param token JWT Token
	 * @param claimsResolver Claim 解析函數
	 * @param <T> Claim 類型
	 * @return 解析後的 Claim 值
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * 生成 JWT Token（無額外 Claims）
	 *
	 * @param userDetails UserDetails
	 * @return Token
	 */
	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	/**
	 * 生成 JWT Token（可附加 Claims）
	 *
	 * @param extraClaims 額外的 Claims
	 * @param userDetails UserDetails
	 * @return Token
	 */
	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return Jwts.builder()
			.setIssuer("XianAn's authServer") // 設定發行者
			.setClaims(extraClaims)
			.setSubject(userDetails.getUsername()) // 使用者名稱 (Email)
			.setIssuedAt(new Date(System.currentTimeMillis())) // 簽發時間
			.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 過期時間
			.signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 使用 HMAC-SHA256 簽名算法
			.compact();
	}

	/**
	 * 驗證 Token 是否有效
	 *
	 * @param token JWT Token
	 * @param userDetails 使用者詳細資訊
	 * @return Token 是否有效
	 */
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	/**
	 * 驗證 Token 是否過期
	 *
	 * @param token JWT Token
	 * @return Token 是否過期
	 */
	private boolean isTokenExpired(String token) {
		final Date expirationDate = extractClaim(token, Claims::getExpiration);
		return expirationDate.before(new Date());
	}

	/**
	 * 解析 JWT 令牌，提取所有 Claims
	 *
	 * @param token JWT Token
	 * @return Token 內的所有 Claims
	 */
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(SECRET_KEY)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}
}
