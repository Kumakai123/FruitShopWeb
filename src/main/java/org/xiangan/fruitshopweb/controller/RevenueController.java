package org.xiangan.fruitshopweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "營業狀況 api")
public class RevenueController {
	
	/**
	 * (服務層) 營收狀況
	 */
	@Autowired
	private RevenueService revenueService;
	
	/**
	 * 瀏覽
	 *
	 * @param paginationRequest 分頁請求
	 * @return 可分頁的營收狀況
	 */
	@Operation(
		summary = "瀏覽可分頁的所有營收狀況"
		,responses = {
		@ApiResponse(responseCode = "200", description = "Success")
		,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
		,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
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
		,parameters = {
		@Parameter(name = "grossIncome",description = "總收入",in = ParameterIn.QUERY,example = "10000")
		,@Parameter(name = "netIncome",description = "淨收入",in = ParameterIn.QUERY,example = "5000")
		,@Parameter(name = "purchasesExpense",description = "進貨成本",in = ParameterIn.QUERY,example = "1000")
		,@Parameter(name = "personnelExpenses",description = "人事成本",in = ParameterIn.QUERY,example = "1500")
		,@Parameter(name = "miscellaneousExpense",description = "雜物成本",in = ParameterIn.QUERY,example = "1500")
		,@Parameter(name = "wastage",description = "損耗",in = ParameterIn.QUERY,example = "100")}
		,responses = {
		@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
		,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
		,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping
	Revenue create(
		@RequestParam BigDecimal grossIncome,
		@RequestParam BigDecimal netIncome,
		@RequestParam BigDecimal purchasesExpense,
		@RequestParam BigDecimal personnelExpenses,
		@RequestParam BigDecimal miscellaneousExpense,
		@RequestParam BigDecimal wastage
	) {
		Revenue revenue = new Revenue();
		
		// 總收入
		if (Objects.nonNull(grossIncome)) {
			revenue.setGrossIncome(grossIncome);
		}
		revenue.setGrossIncome(revenue.getGrossIncome());
		
		// 淨收入
		if (Objects.nonNull(netIncome)) {
			revenue.setNetIncome(netIncome);
		}
		revenue.setNetIncome(revenue.getNetIncome());
		
		// 進貨支出
		if (Objects.nonNull(purchasesExpense)) {
			revenue.setPurchasesExpense(purchasesExpense);
		}
		revenue.setPurchasesExpense(revenue.getPurchasesExpense());
		
		// 人事成本
		if (Objects.nonNull(personnelExpenses)) {
			revenue.setPersonnelExpenses(personnelExpenses);
		}
		revenue.setPersonnelExpenses(revenue.getPersonnelExpenses());
		
		// 雜物成本
		if (Objects.nonNull(miscellaneousExpense)) {
			revenue.setMiscellaneousExpense(miscellaneousExpense);
		}
		revenue.setMiscellaneousExpense(revenue.getMiscellaneousExpense());
		
		if (Objects.nonNull(wastage)) {
			revenue.setWastage(wastage);
		}
		revenue.setWastage(revenue.getWastage());
		
		
		try {
			return revenueService.save(revenue).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format(
					"建立營收狀況時拋出線程中斷異常：%s❗", exception.getLocalizedMessage()));
		}
	}
	
	/**
	 * 刪除
	 *
	 * @param id 主鍵
	 * @return 是否刪除
	 */
	@DeleteMapping("/{id:^\\d+$}")
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
	@GetMapping("/{id:^\\d+$}")
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
	@PostMapping("/{id:^\\d+$}")
	Revenue update(
		@PathVariable String id,
		@RequestParam(required = false) BigDecimal grossIncome,
		@RequestParam(required = false) BigDecimal netIncome,
		@RequestParam(required = false) BigDecimal purchasesExpense,
		@RequestParam(required = false) BigDecimal personnelExpenses,
		@RequestParam(required = false) BigDecimal miscellaneousExpense,
		@RequestParam(required = false) BigDecimal wastage
	) {
		Revenue revenue;
		try {
			revenue = revenueService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format(
					"讀取營收狀況「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}
		
		// 總收入
		if (Objects.nonNull(grossIncome)) {
			revenue.setGrossIncome(grossIncome);
		}
		
		// 淨收入
		if (Objects.nonNull(netIncome)) {
			revenue.setNetIncome(netIncome);
		}
		
		// 進貨支出
		if (Objects.nonNull(purchasesExpense)) {
			revenue.setPurchasesExpense(purchasesExpense);
		}
		
		// 人事成本
		if (Objects.nonNull(personnelExpenses)) {
			revenue.setPersonnelExpenses(personnelExpenses);
		}
		
		// 雜物成本
		if (Objects.nonNull(miscellaneousExpense)) {
			revenue.setMiscellaneousExpense(miscellaneousExpense);
		}
		
		if (Objects.nonNull(wastage)) {
			revenue.setWastage(wastage);
		}
		
		try {
			return revenueService.save(revenue).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format(
					"編輯營收狀況「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}
	}
}
