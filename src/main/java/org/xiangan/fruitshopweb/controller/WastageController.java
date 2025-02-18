package org.xiangan.fruitshopweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xiangan.fruitshopweb.entity.Wastage;
import org.xiangan.fruitshopweb.exception.CustomException;
import org.xiangan.fruitshopweb.model.PaginationRequest;
import org.xiangan.fruitshopweb.service.WastageService;

import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * 損耗單
 */
@RestController
@RequestMapping("/wastage")
@Slf4j
@Tag(name = "損耗單 api")
public class WastageController {

	/**
	 * (服務層) 損耗單
	 */
	private final WastageService wastageService;

	/**
	 * 依賴注入
	 * @param wastageService the wastageService
	 */
	@Autowired
	public WastageController(WastageService wastageService) {
		this.wastageService = wastageService;
	}

	/**
	 * 瀏覽
	 *
	 * @param paginationRequest 分頁請求
	 * @return 可分頁的損耗單
	 */
	@Operation(
			summary = "瀏覽可分頁的所有損耗單"
			,responses = {
			@ApiResponse(responseCode = "200", description = "Success")
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@GetMapping
	Page<Wastage> browse(@Validated final PaginationRequest paginationRequest) {
		final int p = paginationRequest.getP();
		
		try {
			return wastageService
				       .load(
					       p < 1 ? 0 : p - 1,
					       paginationRequest.getS()
				       )
				       .get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("瀏覽損耗單時拋出線程中斷異常：%s❗", exception.getLocalizedMessage()));
		}
	}
	
	/**
	 * 建立
	 *
	 * @param productId 產品主鍵
	 * @param quantity  損耗數量
	 * @param date      損耗日期
	 * @return 損耗單
	 */
	@Operation(
			summary = "建立損耗單資料"
			,parameters = {
			@Parameter(name = "productId",description = "產品主鍵")
			,@Parameter(name = "quantity",description = "損耗數量",in = ParameterIn.QUERY,example = "1")
			,@Parameter(name = "date",description = "損耗日期",in = ParameterIn.QUERY,example = "2025-01-01")}
			,responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping
	Wastage create(
		@RequestParam @NotNull(message = "產品不可為空❗")
		@Pattern(
				regexp = "^[A-Za-z0-9]{10}$"
				, message = "產品ID必須為 UUID(十碼)❗")final String productId,
		@RequestParam final Double quantity,
		@RequestParam
		@DateTimeFormat(pattern = "yyyy-MM-dd") final Date date
	) {
		return wastageService.create(productId, quantity, date);
	}
	
	/**
	 * 刪除損耗單
	 *
	 * @param id 主鍵
	 * @return 是否刪除
	 */
	@Operation(
			summary = "刪除損耗單"
			,description = "根據損耗單主鍵 UUID(十碼) 刪除對應的損耗記錄，並同步調整對應產品的庫存數量。"
			,parameters = {
			@Parameter(name = "id",description = "損耗單主鍵 UUID(十碼)") }
			,responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@DeleteMapping("/{id:[A-Za-z0-9]{10}}")
	Boolean delete(@PathVariable final String id) {
		try {
			return wastageService.delete(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
					String.format("刪除損耗單「%s」時拋出線程中斷異常：%s❗️", id, exception.getLocalizedMessage()));
		}
	}
	
	/**
	 * 讀取
	 *
	 * @param id 主鍵
	 * @return 損耗單
	 */
	@Operation(
			summary = "讀取一筆損耗單資料"
			,description = "輸入損耗單ID讀取資料"
			,parameters = {
			@Parameter(name = "id",description = "損耗單主鍵 UUID(十碼)") }
			,responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@GetMapping("/{id:[A-Za-z0-9]{10}}")
	Wastage read(@PathVariable final String id) {
		try {
			return wastageService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("讀取損耗單「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}
	}
	
	/**
	 * 編輯
	 *
	 * @param id        主鍵
	 * @param productId 產品主鍵
	 * @param quantity  損耗數量
	 * @param date      日期
	 * @return 損耗單
	 */
	@Operation(
			summary = "編輯損耗單資料"
			,description = "根據損耗單主鍵 UUID(十碼) 編輯對應的損耗記錄，並同步調整對應產品的庫存數量。"
			,parameters = {
			@Parameter(name = "id",description = "損耗單主鍵")
			,@Parameter(name = "productId",description = "產品主鍵")
			,@Parameter(name = "quantity",description = "損耗數量",in = ParameterIn.QUERY,example = "5")
			,@Parameter(name = "date",description = "進貨日期")}
			,responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping("/{id:[A-Za-z0-9]{10}}")
	Wastage update(
		@PathVariable final String id,
		@RequestParam @Pattern(
				regexp = "^[A-Za-z0-9]{10}$"
				, message = "產品ID必須為 UUID(十碼)❗")  final String productId,
		@RequestParam(required = false) final Double quantity,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final Date date
	) {
		return wastageService.update(id, productId, quantity, date);
	}
}
