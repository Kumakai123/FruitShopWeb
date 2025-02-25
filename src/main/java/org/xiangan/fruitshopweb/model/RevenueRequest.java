package org.xiangan.fruitshopweb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RevenueRequest {
	
	/**
	 * 主鍵
	 */
	@NotNull(
		groups = {RevenueRequest.Update.class},
		message = "請輸入「主鍵」❗️"
	)
	private Long id;

	/**
	 * 總收入
	 */
	private BigDecimal grossIncome;

	/**
	 * 淨收入
	 */
	private BigDecimal netIncome;

	/**
	 * 進貨成本
	 */
	private BigDecimal purchasesExpense;

	/**
	 * 人事成本
	 */
	private BigDecimal personnelExpenses;

	/**
	 * 雜物成本
	 */
	private BigDecimal miscellaneousExpense;

	/**
	 * 損耗
	 */
	private BigDecimal wastage;

	
	public interface Create extends Default {
	}
	
	public interface Update extends Default {
	}
	
}
