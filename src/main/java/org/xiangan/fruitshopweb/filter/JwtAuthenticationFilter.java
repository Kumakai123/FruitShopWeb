package org.xiangan.fruitshopweb.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.xiangan.fruitshopweb.service.JwtService;
import org.xiangan.fruitshopweb.service.PersonService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	/**
	 * JWT 生成與驗證
	 */
	private  final JwtService jwtService;

	/**
	 * (服務層) 人員
	 */
	private final PersonService personService;

	@Override
	protected void doFilterInternal(
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String userEmail;

		/*
			以下條件為沒有攜帶 Token 的請求
			如果未攜帶 JWT 令牌或令牌不以"Bearer "開頭，則直接呼叫 filterChain.doFilter，繼續處理下一個過濾器或請求處理程序。
		 */
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		// 取"Bearer "後面的Token
		jwt = authHeader.substring(7);
		try {
			//提取Token中的Email
			userEmail = jwtService.extractUsername(jwt);
		} catch (Exception e) {
			log.error("Failed to extract username from token", e);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Invalid JWT token");
			response.getWriter().flush();
			return;
		}

		// 如果 userEmail 不為 null 且當前的 Security 上下文中不存在身份驗證
		if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails;
			try {
				// 根據 userEmail 加載用戶詳細資料。
				userDetails = personService.loadUserByUsername(userEmail);
			} catch (UsernameNotFoundException e) {
				log.error("User not found: " + userEmail, e);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("User not found");
				response.getWriter().flush();
				return;
			}

			if (jwtService.isTokenValid(jwt, userDetails)) {

				// 如果JWT令牌有效，則創建一個 UsernamePasswordAuthenticationToken
				// 並將其設置到 Spring Security 的 Security 上下文中，以確保用戶已成功驗證。
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities()
				);
				authToken.setDetails(
					new WebAuthenticationDetailsSource().buildDetails(request)
				);
				SecurityContextHolder.getContext().setAuthentication(authToken);
				log.info("JWT Authenticated user: {}", userEmail);
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("Invalid or expired JWT token");
				response.getWriter().flush();
				return;
			}
		}
		filterChain.doFilter(request, response);
	}
}