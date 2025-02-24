package org.xiangan.fruitshopweb.model;

import lombok.Builder;
import lombok.Data;

/**
 * 身分驗證回應
 */
@Data
@Builder
public class AuthenticationResponse {
	private String tokenType;
	private String token;
	private String id;
	private String userName;
	private String email;
	private String role;
	private String company;
}
