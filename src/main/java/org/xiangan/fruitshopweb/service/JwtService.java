package org.xiangan.fruitshopweb.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {
	// Token有效期限 (設定15分鐘過期)
	private static final long EXPIRATION_TIME = 60 * 60 * 1000; //單位ms

	//BASE64編碼的密鑰
	private SecretKey SECRET_KEY;

	@PostConstruct
	public void init() {
		// 產生安全的 Base64 密鑰
		String base64Key = "GjDCrTPK8jsBihGyjBcVBSiMUzLhotGPmknZP6sD10E="; // 先產生這個密鑰
		byte[] decodedKey = Base64.getDecoder().decode(base64Key);
		SECRET_KEY = Keys.hmacShaKeyFor(decodedKey);
	}

	/**
	 * 從JWT令牌中提取用戶名
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * 提取JWT令牌中的任何聲明（Claims），並通過提供的Function來解析它們。
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	/**
	 * 簽發Token
	 */
	public String generateToken(
			Map<String, Object> extractClaims,
			UserDetails userDetails
	) {
		return Jwts
				.builder()
				.setClaims(extractClaims)
				.setSubject(userDetails.getUsername()) //以Username做為Subject
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * 驗證Token有效性，比對JWT和UserDetails的Username(Email)是否相同
	 *
	 * @return 有效為True，反之False
	 */
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	/**
	 * 驗證token是否過期
	 */
	private boolean isTokenExpired(String token) {
		final Date expirationDate = extractExpiration(token);
		//        return extractExpiration(token).before(new Date());
		return expirationDate != null && expirationDate.before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * 獲取令牌中所有的聲明將其解析
	 *
	 * @return 令牌中所有的聲明
	 */
	private Claims extractAllClaims(String token) {
		return Jwts
				.parserBuilder()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	/**
	 * 獲取JWT簽名的密鑰
	 */
	private Key getSignInKey() {
		return SECRET_KEY;
	}
}