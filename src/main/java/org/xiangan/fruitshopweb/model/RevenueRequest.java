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
	
	/**
	 * 總收入
	 */
	private Long id;
	private BigDecimal grossIncome;
	private BigDecimal netIncome;
	private BigDecimal purchasesExpense;
	private BigDecimal personnelExpenses;
	private BigDecimal miscellaneousExpense;
	private BigDecimal wastage;
	
	/**
	 * 淨收入
	 */
	
	/**
	 * 進貨成本
	 */
	
	/**
	 * 人事成本
	 */
	
	/**
	 * 雜物成本
	 */
	
	/**
	 * 損耗
	 */
	
	
	public interface Create extends Default {
	}
	
	public interface Update extends Default {
	}
	
}
