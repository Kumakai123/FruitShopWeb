package org.xiangan.fruitshopweb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiangan.fruitshopweb.enumType.ProductTypeEnum;
import org.xiangan.fruitshopweb.enumType.UnitTypeEnum;

/**
 * (控制器)產品類型
 *
 * @author kyle
 */
@RestController
@RequestMapping("productType")
public class ProductTypeEnumController {

	/**
	 * 瀏覽所有產品類型
	 *
	 * @return 產品類型們
	 */
	@GetMapping
	ResponseEntity<ProductTypeEnum[]> browse() {
		return new ResponseEntity<>(ProductTypeEnum.values(), HttpStatus.OK);
	}

	/**
	 * 讀取產品類型
	 *
	 * @param value 列舉值
	 * @return 產品類型
	 */
	@GetMapping("/{value}")
	ResponseEntity<ProductTypeEnum> read(@PathVariable String value) {
		try {
			return new ResponseEntity<>(
					ProductTypeEnum.valueOf(value), HttpStatus.OK
			);
		} catch (IllegalArgumentException illegalArgumentException) {
			throw new IllegalArgumentException(
					String.format(
							"讀取產品類型「%s」時拋出非法引數異常：%s❗️",
							value,
							illegalArgumentException.getLocalizedMessage()
					),
					illegalArgumentException
			);
		}
	}
}
