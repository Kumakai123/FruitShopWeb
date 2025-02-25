package org.xiangan.fruitshopweb.configuration;

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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.xiangan.fruitshopweb.filter.JwtAuthenticationFilter;
import org.xiangan.fruitshopweb.repository.PersonRepository;

/**
 * 應用程式的安全性設定。
 * <p>
 * 設定應用程式的身份驗證、授權、CORS 及 Session 管理，透過 Spring Security 保護系統資源。
 * </p>
 * <p>
 * 透過這個設定，可以自訂 API 存取規則、使用者身份驗證方式，確保系統安全性。
 * </p>
 *
 * <h2>設定內容：</h2>
 * <ul>
 *   <li>使用 JWT 進行身份驗證</li>
 *   <li>限制 API 路由的存取權限</li>
 *   <li>允許跨來源請求（CORS）</li>
 *   <li>關閉 CSRF 以支援無狀態身份驗證</li>
 * </ul>
 *
 * @author kyle
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	/**
	 * (數據存取層)人員
	 */
	private final PersonRepository personRepository;

	/**
	 * Jwt 驗證過濾器
	 */
	private final JwtAuthenticationFilter jwtFiler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// 停用 CSRF，避免影響 API 登入，禁止CSRF（跨站請求偽造）保護。
				.csrf(AbstractHttpConfigurer::disable)
				// JWT 無需 session
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						// 訪客可使用以下 API
						.requestMatchers(
								"/swagger-ui/**",
								"/v3/api-docs/**",
								"/swagger-resources/**",
								"/webjars/**",
								"/error",
								"/auth/**"
						).permitAll()
						.anyRequest().authenticated()  // 其他 API 需要身份驗證
				)
				.anonymous(Customizer.withDefaults())
				.addFilterBefore(jwtFiler, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * 用於查找用戶詳細信息
	 *
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
		authProvider.setPasswordEncoder((new BCryptPasswordEncoder(12)));
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

}