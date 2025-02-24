package org.xiangan.fruitshopweb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xiangan.fruitshopweb.entity.Person;
import org.xiangan.fruitshopweb.exception.CustomException;
import org.xiangan.fruitshopweb.model.ApiResponseDTO;
import org.xiangan.fruitshopweb.model.AuthenticationResponse;
import org.xiangan.fruitshopweb.model.RegisterRequest;
import org.xiangan.fruitshopweb.repository.PersonRepository;

import java.util.concurrent.ExecutionException;

/**
 * 身分驗證
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
	/**
	 * 驗證使用者身份
	 */
	private final AuthenticationManager authenticationManager;

	/**
	 * (數據存取層)人員
	 */
	private final PersonRepository personRepository;

	/**
	 * (服務層) 人員
	 */
	private final PersonService personService;

	/**
	 * 密碼加密與驗證
	 */
	private final PasswordEncoder passwordEncoder;

	/**
	 * JWT 生成與驗證
	 */
	private final JwtService jwtService;

	/**
	 * 註冊
	 *
	 * @param request 註冊請求
	 * @return 註冊狀態
	 */
	@Transactional
	public ResponseEntity<ApiResponseDTO<?>> register(final RegisterRequest request) {
		String company = request.company();
		String email = request.email();
		String phoneNumber = request.phoneNumber();

		// 檢查 Email 是否已被註冊
		boolean emailExists;
		try {
			emailExists = personService.existEmail(email, phoneNumber, company).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new CustomException("檢查 Email 存在時發生錯誤：" + e.getMessage());
		}

		if (emailExists) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponseDTO
					.builder()
					.status("error")
					.response(String.format("「%s」信箱已被註冊，請重新輸入", email))
					.build());
		}

		Person user = Person
			.builder()
			.nickName(request.nickName())
			.name(request.name())
			.level(request.level())
			.email(email)
			.password(passwordEncoder.encode(request.password()))
			.phoneNumber(phoneNumber)
			.company(company)
			.build();

		personService.save(user);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(
				ApiResponseDTO
					.builder()
					.status("成功" )
					.response(user)
					.code(201)
					.build()
			);
	}

	/**
	 * 登入
	 *
	 * @param email 信箱(帳號)
	 * @param password 密碼
	 * @return 登入身分驗證回應
	 */
	public ResponseEntity<ApiResponseDTO<?>> login(final String email,final String password) {
		// 查找使用者
		Person user = personRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException("帳號或密碼錯誤"));
		try {
			// 驗證帳號密碼
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

			// 產生 JWT Token
			String jwtToken = jwtService.generateToken(user);

			// 建立回應物件
			AuthenticationResponse response = AuthenticationResponse.builder()
				.tokenType("Bearer")
				.token(jwtToken)
				.id(user.getId())
				.userName(user.getName())
				.email(user.getEmail())
				.role(user.getLevel().name())
				.company(user.getCompany())
				.build();

			// 回傳成功的 API 回應
			return ResponseEntity.ok(ApiResponseDTO.<AuthenticationResponse>builder()
				.status("成功")
				.response(response)
				.code(200)
				.build());

		} catch (BadCredentialsException e) {
			throw new CustomException("帳號或密碼錯誤");
		}
	}

}