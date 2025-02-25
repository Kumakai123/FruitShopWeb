package org.xiangan.fruitshopweb.enumType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

/**
 * 身分等級
 *
 * @author kyle
 */
@Getter
@JsonSerialize(using = LevelEnumSerializer.class)
public enum LevelEnum {
	/**
	 * 管理者
	 */
	ADMIN("管理者"),
	/**
	 * 貨主
	 */
	CONSIGNOR("貨主"),
	/**
	 * 老闆
	 */
	BOSS("老闆"),
	/**
	 * 員工
	 */
	EMPLOYEE("員工");
	/**
	 * 中文
	 */
	private final String chinese;

	/**
	 * 建構函數
	 * @param chinese 中文
	 */
	LevelEnum(String chinese) {
		this.chinese = chinese;
	}
}
