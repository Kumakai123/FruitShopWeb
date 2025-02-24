package org.xiangan.fruitshopweb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * API 回應模板
 * @param <T>
 */
@AllArgsConstructor
@Builder
@Data
public class ApiResponseDTO<T> {
	private String status;
	private T response;
	private int code;
}
