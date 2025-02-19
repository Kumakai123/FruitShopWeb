package org.xiangan.fruitshopweb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class SummaryAmountDTO {

	/**
	 * 起始時間
	 */
	private LocalDateTime begin;

	/**
	 * 結束時間
	 */
	private LocalDateTime end;

	/**
	 * 總金額
	 */
	private BigDecimal summaryAmount;
}
