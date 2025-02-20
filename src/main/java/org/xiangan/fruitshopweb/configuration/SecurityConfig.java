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
import org.xiangan.fruitshopweb.repository.PersonRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final PersonRepository personRepository;

//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http.csrf(AbstractHttpConfigurer::disable)  //禁止CSRF（跨站請求偽造）保護。
//			.authorizeHttpRequests((authorize) -> authorize //對所有訪問 HTTP 端點的 HttpServletRequest 進行限制
//				.requestMatchers(
//					"/swagger-ui/**",  // 允許 Swagger UI
//					"/**/api-docs/**",  // ✅ 允許 Swagger 加載 API 定義
//					"/swagger-resources/**",
//					"/webjars/**",
//					"/error"
//				).permitAll()  //指定上述匹配規則中的路徑，允許所有用戶訪問，即不需要進行身份驗證。
//				.anyRequest().authenticated()   //其他尚未匹配到的路徑都需要身份驗證
//			).anonymous(Customizer.withDefaults());  // 允許匿名用戶訪問 Swagger UI;
//		return http.build();
//	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)  // ✅ 停用 CSRF，避免影響 API 登入，禁止CSRF（跨站請求偽造）保護。
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/swagger-ui/**",
					"/v3/api-docs/**",
					"/swagger-resources/**",
					"/webjars/**",
					"/error",
					"/api/auth/**"
				).permitAll()  // ✅ 允許訪問這些 API
				.anyRequest().authenticated()  // 其他 API 需要身份驗證
			)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // ✅ 使用 JWT，關閉 Session
			.anonymous(Customizer.withDefaults());  // ✅ 允許匿名訪問 Swagger

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
}