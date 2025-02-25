package org.xiangan.fruitshopweb.enumType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

/**
 * 產品類型
 *
 * @author kyle
 */
@Getter
@JsonSerialize(using = ProductTypeEnumSerializer.class)
public enum ProductTypeEnum {
	/**
	 * 水果
	 */
	FRUIT("水果"),
	/**
	 * 五金
	 */
	HARDWARE("五金"),
	/**
	 * 生活
	 */
	DAILY_NECESSITIES("生活"),
	/**
	 * 3C家電
	 */
	DIGITAL_PRODUCTS("3C家電"),
	/**
	 * 服飾
	 */
	APPAREL("服飾"),
	/**
	 * 食品
	 */
	FOODSTUFF("食品"),
	/**
	 * 運動
	 */
	SPORT("運動"),
	/**
	 * 保健
	 */
	HEALTHCARE("保健"),
	/**
	 * 精品
	 */
	BOUTIQUE("精品"),
	/**
	 * 美妝
	 */
	COSMETICS("美妝"),
	/**
	 * 圖書
	 */
	BOOKS("圖書"),
	/**
	 * 居家
	 */
	HOUSEHOLD("居家");


	/**
	 * 單位的中文名稱
	 */
	private final String chinese;

	/**
	 * 構造函數
	 *
	 * @param chinese 產品的中文名稱
	 */
	ProductTypeEnum(String chinese) {
		this.chinese = chinese;
	}
}