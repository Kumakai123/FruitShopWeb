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
import org.xiangan.fruitshopweb.entity.Purchase;
import org.xiangan.fruitshopweb.exception.CustomException;
import org.xiangan.fruitshopweb.model.PaginationRequest;
import org.xiangan.fruitshopweb.service.PurchaseService;

import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * 進貨單
 */
@RestController
@RequestMapping("/purchase")
@Slf4j
@Tag(name = "進貨單 api",description = "進貨單操作")
public class PurchaseController {

	/**
	 * (服務層) 進貨單
	 */
	private final PurchaseService purchaseService;

	/**
	 * 依賴注入
	 * @param purchaseService the purchaseService
	 */
	@Autowired
	public PurchaseController(PurchaseService purchaseService) {
		this.purchaseService = purchaseService;
	}

	/**
	 * 瀏覽
	 *
	 * @param paginationRequest 分頁請求
	 * @return 可分頁的進貨單
	 */
	@Operation(
		summary = "瀏覽可分頁的所有進貨單"
		,responses = {
		@ApiResponse(responseCode = "200", description = "Success")
		,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
		,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@GetMapping
	Page<Purchase> browse(@Validated final PaginationRequest paginationRequest) {
		final int p = paginationRequest.getP();

		try {
			return purchaseService
				       .load(
					       p < 1 ? 0 : p - 1,
					       paginationRequest.getS()
				       )
				       .get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("瀏覽進貨單時拋出線程中斷異常：%s❗", exception.getLocalizedMessage()));
		}
	}

	/**
	 * @param productId     產品主鍵
	 * @param quantity      數量
	 * @param receivingDate 進貨日期
	 * @return 進貨單
	 */
	@Operation(
		summary = "建立進貨單資料"
		,parameters = {
		@Parameter(name = "productId",description = "產品主鍵")
		,@Parameter(name = "quantity",description = "數量",in = ParameterIn.QUERY,example = "25")
		,@Parameter(name = "receivingDate",description = "進貨日期")}
		,responses = {
		@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
		,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
		,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping
	Purchase create(
		@RequestParam @NotNull(message = "產品不可為空❗")
		@Pattern(
				regexp = "^[A-Za-z0-9]{10}$"
				, message = "產品ID必須為 UUID(十碼)❗") final String productId
		,@RequestParam @NotNull(message = "進貨數量不可為空❗") final Double quantity
		,@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final Date receivingDate
	) {
			return purchaseService.create(productId,quantity,receivingDate);
	}
	
	/**
	 * 刪除進貨單
	 *
	 * @param id 主鍵
	 * @return 是否刪除
	 */
	@Operation(
		summary = "刪除進貨單"
		,description = "根據進貨單主鍵 UUID(十碼) 刪除對應的進貨記錄，並同步調整對應產品的庫存數量。"
		,parameters = {
		@Parameter(name = "id",description = "進貨單主鍵 UUID(十碼)") }
		,responses = {
		@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
		,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
		,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@DeleteMapping("/{id:[A-Za-z0-9]{10}}")
	Boolean delete(@PathVariable final String id) {
		try {
			return purchaseService.delete(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format(
						"刪除進貨單「%s」時拋出線程中斷異常：%s❗️", id, exception.getLocalizedMessage()));
		}
	}
	
	/**
	 * 讀取
	 *
	 * @param id 主鍵
	 * @return 進貨單
	 */
	@Operation(
		summary = "讀取一筆進貨單資料"
		,description = "輸入進貨單ID讀取資料"
		,parameters = {
		@Parameter(name = "id",description = "進貨單主鍵 UUID(十碼)") }
		,responses = {
		@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
		,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
		,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@GetMapping("/{id:[A-Za-z0-9]{10}}")
	Purchase read(@PathVariable final String id) {
		try {
			return purchaseService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format(
						"讀取進貨單「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}
	}
	
	/**
	 * 編輯
	 *
	 * @param id            主鍵
	 * @param productId     產品主鍵
	 * @param quantity      數量
	 * @param receivingDate 進貨日期
	 * @return 進貨單
	 */
	@Operation(
		summary = "編輯進貨單資料"
		,description = "根據進貨單主鍵 UUID(十碼) 編輯對應的進貨記錄，並同步調整對應產品的庫存數量。"
		,parameters = {
		@Parameter(name = "id",description = "進貨單主鍵")
		,@Parameter(name = "productId",description = "產品主鍵")
		,@Parameter(name = "quantity",description = "數量",in = ParameterIn.QUERY,example = "5")
		,@Parameter(name = "receivingDate",description = "進貨日期")}
		,responses = {
		@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
		,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
		,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping("/{id:[A-Za-z0-9]{10}}")
	Purchase update(
		@PathVariable final String id,
		@RequestParam @Pattern(
				regexp = "^[A-Za-z0-9]{10}$"
				, message = "產品ID必須為 UUID(十碼)❗") final String productId,
		@RequestParam(required = false) final Double quantity,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final Date receivingDate
	) {
		return purchaseService.update(id, productId, quantity, receivingDate);
	}
}
