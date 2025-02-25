package org.xiangan.fruitshopweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xiangan.fruitshopweb.model.ApiResponseDTO;
import org.xiangan.fruitshopweb.model.RegisterRequest;
import org.xiangan.fruitshopweb.service.AuthenticationService;

/**
 * 身分驗證
 *
 * @author kyle
 */
@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = "身分驗證",description = "包含註冊、登出等身分驗證之API")
public class AuthenticationController {

	/**
	 * (服務層)身分驗證
	 */
	private final AuthenticationService service;

	/**
	 * 註冊
	 * @param request 註冊請求
	 * @return API 回應模板
	 */
	@Operation(
		summary = "註冊"
		,description = "密碼長度不可小於8碼"
		,responses = {
		@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
		,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
		,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping("/register")
	public ResponseEntity<ApiResponseDTO<?>> register(@RequestBody @Validated RegisterRequest request){
		return service.register(request);
	}

	/**
	 * 登入
	 * @param email 信箱
	 * @param password 密碼
	 * @return API 回應模板
	 */
	@Operation(
		summary = "登入"
		,parameters = {
		@Parameter(name = "email",description = "信箱",in = ParameterIn.QUERY,example = "test@gmail.com")
		,@Parameter(name = "password",description = "密碼",in = ParameterIn.QUERY,example = "test1234") }
		,responses = {
		@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
		,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
		,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping("/login")
	public ResponseEntity<ApiResponseDTO<?>> login(
		@RequestParam final String email
		,@RequestParam final String password
	) {
		return service.login(email,password);
	}
}