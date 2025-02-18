package org.xiangan.fruitshopweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xiangan.fruitshopweb.entity.Revenue;
import org.xiangan.fruitshopweb.exception.CustomException;
import org.xiangan.fruitshopweb.model.PaginationRequest;
import org.xiangan.fruitshopweb.service.RevenueService;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * 營收狀況
 */
@RestController
@RequestMapping("/revenue")
@Slf4j
@Tag(name = "營收狀況 api")
public class RevenueController {

	/**
	 * (服務層) 營收狀況
	 */
	private final RevenueService revenueService;

	/**
	 * 依賴注入
	 *
	 * @param revenueService the revenueService
	 */
	@Autowired
	public RevenueController(RevenueService revenueService) {
		this.revenueService = revenueService;
	}

	/**
	 * 瀏覽
	 *
	 * @param paginationRequest 分頁請求
	 * @return 可分頁的營收狀況
	 */
	@Operation(
			summary = "瀏覽可分頁的所有營收狀況"
			, responses = {
			@ApiResponse(responseCode = "200", description = "Success")
			, @ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			, @ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@GetMapping
	Page<Revenue> browse(@Validated final PaginationRequest paginationRequest) {
		final int p = paginationRequest.getP();

		try {
			return revenueService
					.load(
							p < 1 ? 0 : p - 1,
							paginationRequest.getS()
					)
					.get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
					String.format(
							"瀏覽營業狀況時拋出線程中斷異常：%s❗", exception.getLocalizedMessage()));
		}
	}

	/**
	 * 建立
	 *
	 * @param grossIncome          總收入
	 * @param netIncome            淨收入
	 * @param purchasesExpense     進貨成本
	 * @param personnelExpenses    人事成本
	 * @param miscellaneousExpense 雜物成本
	 * @param wastage              損耗
	 * @return 營收狀況
	 */
	@Operation(
			summary = "建立營收狀況"
			, parameters = {
			@Parameter(name = "grossIncome", description = "總收入", in = ParameterIn.QUERY, example = "10000")
			, @Parameter(name = "netIncome", description = "淨收入", in = ParameterIn.QUERY, example = "5000")
			, @Parameter(name = "purchasesExpense", description = "進貨成本", in = ParameterIn.QUERY, example = "1000")
			, @Parameter(name = "personnelExpenses", description = "人事成本", in = ParameterIn.QUERY, example = "1500")
			, @Parameter(name = "miscellaneousExpense", description = "雜物成本", in = ParameterIn.QUERY, example = "1500")
			, @Parameter(name = "wastage", description = "損耗", in = ParameterIn.QUERY, example = "100")}
			, responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			, @ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			, @ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping
	Revenue create(
			@RequestParam(defaultValue = "0") final BigDecimal grossIncome,
			@RequestParam(defaultValue = "0") final BigDecimal netIncome,
			@RequestParam(defaultValue = "0") final BigDecimal purchasesExpense,
			@RequestParam(defaultValue = "0") final BigDecimal personnelExpenses,
			@RequestParam(defaultValue = "0") final BigDecimal miscellaneousExpense,
			@RequestParam(defaultValue = "0") final BigDecimal wastage
	) {
		return revenueService.create(grossIncome, netIncome, purchasesExpense,
				personnelExpenses, miscellaneousExpense, wastage);
	}

	/**
	 * 刪除
	 *
	 * @param id 主鍵
	 * @return 是否刪除
	 */
	@Operation(
			summary = "刪除營收狀況"
			, parameters = {
			@Parameter(name = "id", description = "營收狀況主鍵 UUID(十碼)")}
			, responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			, @ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			, @ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@DeleteMapping("/{id:[A-Za-z0-9]{10}}")
	Boolean delete(@PathVariable final String id) {
		Revenue revenue;
		try {
			revenue = revenueService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
					String.format(
							"讀取營收狀況「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}

		try {
			return revenueService.delete(revenue).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
					String.format(
							"刪除營收狀況「%s」時拋出線程中斷異常：%s❗️", id, exception.getLocalizedMessage()));
		}
	}

	/**
	 * 讀取
	 *
	 * @param id 主鍵
	 * @return 營收狀況
	 */
	@Operation(
			summary = "輸入營收狀況ID讀取資料"
			, parameters = {
			@Parameter(name = "id", description = "營收狀況主鍵 UUID(十碼)")}
			, responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			, @ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			, @ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@GetMapping("/{id:[A-Za-z0-9]{10}}")
	Revenue read(@PathVariable final String id) {
		try {
			return revenueService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
					String.format(
							"讀取營收狀況「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}
	}

	/**
	 * 編輯
	 *
	 * @param id                   主鍵
	 * @param grossIncome          總收入
	 * @param netIncome            淨收入
	 * @param purchasesExpense     進貨成本
	 * @param personnelExpenses    人事成本
	 * @param miscellaneousExpense 雜物成本
	 * @param wastage              損耗
	 * @return 營收狀況
	 */
	@Operation(
			summary = "編輯營收狀況"
			, parameters = {
			@Parameter(name = "id", description = "營收狀況主鍵 UUID(十碼)")
			, @Parameter(name = "grossIncome", description = "總收入", in = ParameterIn.QUERY, example = "10000")
			, @Parameter(name = "netIncome", description = "淨收入", in = ParameterIn.QUERY, example = "5000")
			, @Parameter(name = "purchasesExpense", description = "進貨成本", in = ParameterIn.QUERY, example = "1000")
			, @Parameter(name = "personnelExpenses", description = "人事成本", in = ParameterIn.QUERY, example = "1500")
			, @Parameter(name = "miscellaneousExpense", description = "雜物成本", in = ParameterIn.QUERY, example = "1500")
			, @Parameter(name = "wastage", description = "損耗", in = ParameterIn.QUERY, example = "100")}
			, responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			, @ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			, @ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping("/{id:[A-Za-z0-9]{10}}")
	Revenue update(
			@PathVariable final String id,
			@RequestParam(required = false) final BigDecimal grossIncome,
			@RequestParam(required = false) final BigDecimal netIncome,
			@RequestParam(required = false) final BigDecimal purchasesExpense,
			@RequestParam(required = false) final BigDecimal personnelExpenses,
			@RequestParam(required = false) final BigDecimal miscellaneousExpense,
			@RequestParam(required = false) final BigDecimal wastage
	) {
		return revenueService.update(id, grossIncome, netIncome,
				purchasesExpense, personnelExpenses, miscellaneousExpense, wastage);
	}
}
