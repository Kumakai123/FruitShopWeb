package org.xiangan.fruitshopweb.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (請求模型)分頁
 */
@Data
@NoArgsConstructor
public class PaginationRequest {
	
	/**
	 * 第幾頁
	 */
	private int p = 1;
	
	/**
	 * 每頁幾筆
	 */
	private int s = 10;
}
