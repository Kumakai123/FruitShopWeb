package org.xiangan.fruitshopweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xiangan.fruitshopweb.enumType.LevelEnum;
import org.xiangan.fruitshopweb.model.AuthenticationResponse;
import org.xiangan.fruitshopweb.model.RegisterRequest;
import org.xiangan.fruitshopweb.model.StatusResponse;
import org.xiangan.fruitshopweb.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "身分驗證",description = "包含註冊、登出等身分驗證之API")
@Slf4j
public class AuthenticationController {

	private final AuthenticationService service;

	@Operation(summary = "註冊接口")
	@PostMapping("/register")
	public ResponseEntity<StatusResponse> register(
		@RequestBody @Validated RegisterRequest request

//		@RequestParam final String nickName
//		,@RequestParam final String name
//		,@RequestParam final LevelEnum level
//		,@RequestParam final String email
//		,@RequestParam final String password
//		,@RequestParam final String phoneNumber
//		,@RequestParam final String company
	){
		return ResponseEntity.ok(
			service.register(
				request
//			nickName
//			,name
//			,level
//			,email
//			,password
//			,phoneNumber
//			,company
			));
	}

	@Operation(summary = "登入接口")
	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponse> login(
		@Parameter final String email,@Parameter final String password
	) {
		return ResponseEntity.ok(service.login(email,password));
	}
}