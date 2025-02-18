package org.xiangan.fruitshopweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiangan.fruitshopweb.enumType.ProductTypeEnum;
import org.xiangan.fruitshopweb.exception.CustomException;

/**
 * (控制器)產品類型
 *
 * @author kyle
 */
@RestController
@RequestMapping("productType")
@Tag(name = "產品類型 Enum api")
public class ProductTypeEnumController {

	/**
	 * 瀏覽所有產品類型
	 *
	 * @return 產品類型們
	 */
	@Operation(
			summary = "瀏覽所有產品類型"
			, responses = {
			@ApiResponse(responseCode = "200", description = "Success")
			, @ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
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
	@Operation(
			summary = "讀取產品類型"
			, parameters = {
			@Parameter(name = "value", description = "產品類型的value")}
			, responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			, @ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			, @ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@GetMapping("/{value}")
	ResponseEntity<ProductTypeEnum> read(@PathVariable String value) {
		try {
			return new ResponseEntity<>(
					ProductTypeEnum.valueOf(value), HttpStatus.OK
			);
		} catch (IllegalArgumentException illegalArgumentException) {
			throw new CustomException(
					String.format(
							"讀取產品類型「%s」時拋出非法引數異常：%s❗️",
							value,
							illegalArgumentException.getLocalizedMessage()));
		}
	}
}
