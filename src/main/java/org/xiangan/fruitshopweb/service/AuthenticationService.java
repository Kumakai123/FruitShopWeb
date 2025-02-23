package org.xiangan.fruitshopweb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.xiangan.fruitshopweb.entity.Person;
import org.xiangan.fruitshopweb.enumType.LevelEnum;
import org.xiangan.fruitshopweb.model.AuthenticationResponse;
import org.xiangan.fruitshopweb.model.RegisterRequest;
import org.xiangan.fruitshopweb.model.StatusResponse;
import org.xiangan.fruitshopweb.repository.PersonRepository;

@Service
@Slf4j
public class AuthenticationService {
	@Autowired
	private  AuthenticationManager authenticationManager;
	@Autowired
	private  PersonRepository personRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtService jwtService;

	/**
	 * 註冊
	 */
	public StatusResponse register(
		RegisterRequest request
//	 final String nickName
//	,final String name
//	,final LevelEnum level
//	,final String email
//	,final String password
//	,final String phoneNumber
//	,final String company
	) {
		var user = Person
			.builder()
			.nickName(request.getNickName())
			.name(request.getName())
			.level(LevelEnum.EMPLOYEE)
			.email(request.getEmail())
			.password(passwordEncoder.encode(request.getPassword()))
			.phoneNumber(request.getPhoneNumber())
			.company(request.getCompany())
			.build();

//		personService.create(
//			nickName,name,level,email,passwordEncoder.encode(password),phoneNumber,company
//		);
		personRepository.save(user);
		return StatusResponse.builder().status("成功" ).build();
	}

	/**
	 * 登入
	 */
	public AuthenticationResponse login(final String email,final String password) {
		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(email, password));
		var user = personRepository.findByEmail(email).orElseThrow();
		var jwtToken = jwtService.generateToken(user);
		log.info(jwtToken);
		return AuthenticationResponse
			.builder()
			.status("成功" )
			.token(jwtToken)
			.build();
	}
}