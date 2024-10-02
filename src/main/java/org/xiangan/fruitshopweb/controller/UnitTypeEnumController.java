package org.xiangan.fruitshopweb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiangan.fruitshopweb.enumType.UnitTypeEnum;

/**
 * (控制器)單位類型
 *
 * @author kyle
 */
@RestController
@RequestMapping("unitType")
public class UnitTypeEnumController {

	/**
	 * 瀏覽所有單位類型
	 *
	 * @return 單位類型們
	 */
	@GetMapping
	ResponseEntity<UnitTypeEnum[]> browse() {
		return new ResponseEntity<>(UnitTypeEnum.values(), HttpStatus.OK);
	}

	/**
	 * 讀取單位類型
	 *
	 * @param value 列舉值
	 * @return 單位類型
	 */
	@GetMapping("/{value}")
	ResponseEntity<UnitTypeEnum> read(@PathVariable String value) {
		try {
			return new ResponseEntity<>(
					UnitTypeEnum.valueOf(value), HttpStatus.OK
			);
		} catch (IllegalArgumentException illegalArgumentException) {
			throw new IllegalArgumentException(
					String.format(
							"讀取單位類型「%s」時拋出非法引數異常：%s❗️",
							value,
							illegalArgumentException.getLocalizedMessage()
					),
					illegalArgumentException
			);
		}
	}
}
