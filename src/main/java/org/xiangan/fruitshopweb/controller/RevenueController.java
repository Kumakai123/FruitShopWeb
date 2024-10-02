package org.xiangan.fruitshopweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xiangan.fruitshopweb.entity.Revenue;
import org.xiangan.fruitshopweb.model.PaginationRequest;
import org.xiangan.fruitshopweb.service.RevenueService;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * 營業狀況
 */
@RestController
@RequestMapping("/revenue")
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
	 * @return 可分頁的營業狀況
	 */
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
			throw new RuntimeException(
				String.format(
					"瀏覽營業狀況時拋出線程中斷異常：%s❗",
					exception.getLocalizedMessage()
				),
				exception
			);
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
	@PostMapping
	Revenue create(
		@RequestParam(required = false) BigDecimal grossIncome,
		@RequestParam(required = false) BigDecimal netIncome,
		@RequestParam(required = false) BigDecimal purchasesExpense,
		@RequestParam(required = false) BigDecimal personnelExpenses,
		@RequestParam(required = false) BigDecimal miscellaneousExpense,
		@RequestParam(required = false) BigDecimal wastage
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
			throw new RuntimeException(
				String.format(
					"建立營收狀況時拋出線程中斷異常：%s❗",
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
	
	/**
	 * 刪除
	 *
	 * @param id 主鍵
	 * @return 是否刪除
	 */
	@DeleteMapping("/{id:^\\d+$}")
	Boolean delete(@PathVariable final long id) {
		Revenue revenue;
		try {
			revenue = revenueService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取營收狀況「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
		
		try {
			return revenueService.delete(revenue).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"刪除營收狀況「%d」時拋出線程中斷異常：%s❗️",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
	
	/**
	 * 讀取
	 *
	 * @param id 主鍵
	 * @return 營收狀況
	 */
	@GetMapping("/{id:^\\d+$}")
	Revenue read(@PathVariable final long id) {
		try {
			return revenueService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取營收狀況「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
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
		@PathVariable Long id,
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
			throw new RuntimeException(
				String.format(
					"讀取營收狀況「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
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
			throw new RuntimeException(
				String.format(
					"編輯營收狀況「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
}
