package org.xiangan.fruitshopweb.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.xiangan.fruitshopweb.enumType.LevelEnum;

/**
 * 註冊請求
 *
 * @author kyle
 */
public record RegisterRequest(
	String nickName
	, @NotBlank String name
	, LevelEnum level
	, @Email @NotBlank String email
	, @Size(min = 8)String password
	, String phoneNumber
	, String company
) {}
