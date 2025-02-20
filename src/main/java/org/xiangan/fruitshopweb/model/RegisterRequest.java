package org.xiangan.fruitshopweb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.xiangan.fruitshopweb.enumType.LevelEnum;

@Data
@AllArgsConstructor
public class RegisterRequest {
	private final String nickName;
	private final String name;
	private final LevelEnum level;
	private final String email;
	private final String password;
	private final String phoneNumber;
	private final String company;
}
