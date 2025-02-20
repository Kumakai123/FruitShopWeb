package org.xiangan.fruitshopweb.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.xiangan.fruitshopweb.util.JwtUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SimpleFilter implements Filter {

	private final JwtUtil jwtUtil;

	// 儲存使用者或 IP 的請求計數
	private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
	// 黑名單 (IP 或 User)
	private final Map<String, Long> blacklist = new ConcurrentHashMap<>();
	// 最後一次請求時間
	private final Map<String, Long> lastRequestTime = new ConcurrentHashMap<>();

	// 限流參數
	private static final int REQUEST_LIMIT = 5; // 每分鐘最多 5 次
	private static final long TIME_WINDOW = 60_000; // 60秒
	private static final long BLACKLIST_DURATION = 300_000; // 封鎖 5 分鐘
	private static final int HTTP_TOO_MANY_REQUESTS = 429; // HTTP 429 錯誤碼

	public SimpleFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String clientIp = req.getRemoteAddr();
		String jwt = getJwtFromRequest(req);
		String userKey = (jwt != null && jwtUtil.isTokenValid(jwt)) ?
			jwtUtil.getUsernameFromToken(jwt) :
			clientIp;

//		// 🛡️ 黑名單檢查
//		if (blacklist.containsKey(userKey)) {
//			long blockedUntil = blacklist.get(userKey);
//			if (System.currentTimeMillis() < blockedUntil) {
//				sendJsonError(res, HttpServletResponse.SC_FORBIDDEN, "已被封鎖，請稍後再試");
//				return;
//			} else {
//				blacklist.remove(userKey); // 移除過期黑名單
//			}
//		}

		// 🛡️ JWT 驗證失敗處理
		if (jwt != null && !jwtUtil.isTokenValid(jwt)) {
			sendJsonError(res, HttpServletResponse.SC_UNAUTHORIZED, "無效的 Token");
			return;
		}

//		// 限流計數
//		requestCounts.putIfAbsent(userKey, new AtomicInteger(0));
//		lastRequestTime.putIfAbsent(userKey, System.currentTimeMillis());
//
//		long currentTime = System.currentTimeMillis();
//		long lastTime = lastRequestTime.get(userKey);
//
//		if (currentTime - lastTime > TIME_WINDOW) {
//			requestCounts.get(userKey).set(0);
//			lastRequestTime.put(userKey, currentTime);
//		}
//
//		int currentCount = requestCounts.get(userKey).incrementAndGet();
//
//		// 🛡️ 超過限流封鎖
//		if (currentCount > REQUEST_LIMIT) {
//			blacklist.put(userKey, currentTime + BLACKLIST_DURATION);
//			sendJsonError(res, HTTP_TOO_MANY_REQUESTS, "請求過於頻繁，已被封鎖 5 分鐘");
//			return;
//		}

		// 通過驗證，繼續請求
		chain.doFilter(request, response);
	}

	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private void sendJsonError(HttpServletResponse response, int status, String message) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		PrintWriter writer = response.getWriter();
		writer.write("{\"error\":\"" + message + "\"}");
		writer.flush();
		writer.close();
	}
}
