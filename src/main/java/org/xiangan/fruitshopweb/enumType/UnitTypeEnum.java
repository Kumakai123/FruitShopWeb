package org.xiangan.fruitshopweb.enumType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

/**
 * 單位類型
 * 此枚舉定義了各種單位類型，包括長度、重量、容量等。
 * 每個枚舉常量包含單位的中文名稱和相對於基準單位的倍數。
 * 長度以公尺為基準，重量以公斤為基準，容量以公升為基準。
 *
 * @author kyle
 */
@Getter
@JsonSerialize(using = UnitTypeEnumSerializer.class)
public enum UnitTypeEnum {

	/**
	 * 臺斤，等於 0.6 公斤
	 */
	JIN("臺斤", 0.6),

	/**
	 * 臺兩，等於 1/16 臺斤，約 0.0375 公斤
	 */
	LIANG("臺兩", 0.0375),

	/**
	 * 克，等於 0.001 公斤
	 */
	GRAM("克", 0.001),

	/**
	 * 公斤，基本重量單位
	 */
	KILOGRAM("公斤", 1),

	/**
	 * 英鎊，約等於 0.453592 公斤
	 */
	POUND("英鎊", 0.453592),

	/**
	 * 公尺，基本長度單位
	 */
	METER("公尺", 1),

	/**
	 * 公里，等於 1000 公尺
	 */
	KILOMETER("公里", 1000),

	/**
	 * 公分，等於 0.01 公尺
	 */
	CENTIMETER("公分", 0.01),

	/**
	 * 毫米，等於 0.001 公尺
	 */
	MILLIMETER("毫米", 0.001),

	/**
	 * 英呎，約等於 0.3048 公尺
	 */
	FOOT("英呎", 0.3048),

	/**
	 * 英寸，約等於 0.0254 公尺
	 */
	INCH("英寸", 0.0254),

	/**
	 * 公升，基本容量單位
	 */
	LITRE("公升", 1),

	/**
	 * 毫升，等於 0.001 公升
	 */
	MILLILITRE("毫升", 0.001),

	/**
	 * 加侖，約等於 3.78541 公升
	 */
	GALLON("加侖", 3.78541),

	/**
	 * 個數單位，如根、張、則、塊、首、件等
	 */
	PIECE("根／張／則／塊／首／件／顆／個", 1),

	/**
	 * 包裝單位
	 */
	PACKAGE("件", 1),

	/**
	 * 成對單位，如一對或一雙
	 */
	PAIR("一對(雙)", 2),

	/**
	 * 集合單位，如組、台、套、架
	 */
	SET("組、台、套、架", 1),

	/**
	 * 打，等於 12 個
	 */
	DOZEN("一打", 12),

	/**
	 * 卷
	 */
	ROLL("卷", 1),

	/**
	 * 箱
	 */
	CASE("箱", 1),

	/**
	 * 包
	 */
	BALE("包", 1),

	/**
	 * 桶
	 */
	DRUM("桶", 1),

	/**
	 * 袋
	 */
	BAG("袋", 1);

	/**
	 * 單位的中文名稱
	 */
	private final String chinese;

	/**
	 * 單位相對於基準單位的倍數
	 * 對於長度，基準單位是公尺
	 * 對於重量，基準單位是公斤
	 * 對於容量，基準單位是公升
	 */
	private final double unit;

	/**
	 * 構造函數
	 *
	 * @param chinese 單位的中文名稱
	 * @param unit 單位相對於基準單位的倍數
	 */
	UnitTypeEnum(String chinese, double unit) {
		this.chinese = chinese;
		this.unit = unit;
	}
}