package org.xiangan.fruitshopweb.configuration;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.xiangan.fruitshopweb.repository.PersonRepository;
import org.xiangan.fruitshopweb.service.JwtService;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

//	private static final String SECRET_KEY = "8GbDA6x8vLFWHMaRpBU5NiNawAlDb3iSi+rMCXHpSKA="; // 密鑰
	private final PersonRepository personRepository;
	private final JwtService jwtService;

	@Bean
	public SecurityFilterChain filterChain(
		HttpSecurity http
	,AuthenticationProvider authenticationProvider) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)  // ✅ 停用 CSRF，避免影響 API 登入，禁止CSRF（跨站請求偽造）保護。
			.authorizeHttpRequests(auth -> auth
				// 訪客可使用以下 API
				.requestMatchers(
					"/swagger-ui/**",
					"/v3/api-docs/**",
					"/swagger-resources/**",
					"/webjars/**",
					"/error",
					"/api/auth/**"
				).permitAll()
//				.anyRequest().permitAll()
				.anyRequest().authenticated()  // 其他 API 需要身份驗證
			)
			.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwt -> jwt.decoder(jwtDecoder())) // ✅ 使用 `JwtDecoder`
			).sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)); // ✅ JWT 無需 session

		//			// JWT驗證
//			.oauth2ResourceServer( oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//			// 使用 JWT，關閉 Session
//			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//			.authenticationProvider(authenticationProvider)
//			.build();

		return http.build();
	}

	/**
	 * 用於查找用戶詳細信息
	 * @return userEmail(用戶詳細信息)
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		return userEmail -> personRepository.findByEmail(userEmail)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
	}

	/**
	 * 執行身份驗證的關鍵組件
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	/**
	 * 管理身份驗證的組件
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	/**
	 * 以便將用戶密碼進行安全的雜湊存儲
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

//	@Bean
//	public AuthenticationProvider jwtAuthenticationProvider() {
//		return new JwtAuthenticationProvider(jwtDecoder());
//	}

	@Bean
	public JwtDecoder jwtDecoder(JwtService jwtService) {
		return NimbusJwtDecoder.withSecretKey(jwtService.getSecretKey()).build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		return new JwtAuthenticationConverter();
	}
	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withSecretKey(jwtService.getSecretKey()).build(); // ✅ **確保 `SECRET_KEY` 兩邊一致**
	}
}