package org.xiangan.fruitshopweb.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (請求模型)分頁
 */
@Data
@NoArgsConstructor
@Schema(description = "分頁參數")
public class PaginationRequest {
	
	/**
	 * 第幾頁
	 */
	@Schema(description = "頁碼 (從 1 開始)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	@Min(value = 1, message = "頁碼必須大於 0")
	private int p = 1;
	
	/**
	 * 每頁幾筆
	 */
	@Schema(description = "每頁筆數", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
	@Min(value = 1, message = "每頁筆數必須大於 0")
	private int s = 10;
}
