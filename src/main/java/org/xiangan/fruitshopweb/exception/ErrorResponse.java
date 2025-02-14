package org.xiangan.fruitshopweb.exception;

import lombok.Getter;

import java.time.LocalDateTime;

import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 表示 API 錯誤回應的資料模型。
 * <p>
 * 此類別通常在處理例外或錯誤請求時使用，
 * 將錯誤相關資訊以標準格式回傳給用戶端。
 * </p>
 */
@Getter
public class ErrorResponse {

    /** 發生錯誤的時間戳記 */
    private final LocalDateTime timestamp;

    /** HTTP 狀態碼 (如 404, 500) */
    private final int status;

    /** 錯誤類型或錯誤名稱 (如 "Not Found", "Internal Server Error") */
    private final String error;

    /** 錯誤詳細訊息或描述 */
    private final String message;

    /** 發生錯誤的請求路徑 (URI) */
    private final String path;

    /**
     * 建立包含錯誤資訊的 {@code ErrorResponse} 物件。
     *
     * @param status HTTP 狀態碼
     * @param error 錯誤名稱或錯誤類型
     * @param message 錯誤訊息或描述
     * @param path 發生錯誤的請求路徑
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}

