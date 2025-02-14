package org.xiangan.fruitshopweb.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * 全域例外處理程序 (Global Exception Handler)。
 * <p>
 * 此類別使用 {@code @ControllerAdvice} 來集中管理應用程式中的例外處理邏輯，
 * 可統一回傳標準化的錯誤資訊，提升維護性及一致性。
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 處理自訂例外 {@code CustomException} (對應 404 狀態碼)。
     * <p>
     * 當系統拋出 {@code CustomException} 時，會返回包含錯誤描述的標準回應結構。
     * </p>
     *
     * @param ex 觸發此處理程序的例外
     * @param request 當前的 WebRequest 請求內容
     * @return 標準格式的錯誤回應 {@link ErrorResponse} 及 HTTP 404 狀態碼
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
        CustomException ex, WebRequest request) {
        return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            "Resource Not Found",
            ex.getMessage(),
            request,
            ex);
    }

    /**
     * 處理通用例外 {@code Exception} (對應 500 狀態碼)。
     * <p>
     * 當系統出現未預期的錯誤 (如程式錯誤或系統異常) 時，
     * 會返回包含錯誤訊息及提示用戶聯繫系統管理員的標準回應。
     * </p>
     *
     * @param ex 觸發此處理程序的例外
     * @param request 當前的 WebRequest 請求內容
     * @return 標準格式的錯誤回應 {@link ErrorResponse} 及 HTTP 500 狀態碼
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
        Exception ex, WebRequest request) {
        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Unexpected Error",
            "請聯絡系統管理員",
            request,
            ex);
    }

    /**
     * 建立統一格式的錯誤回應 {@code ErrorResponse}。
     * <p>
     * 此方法將例外資訊封裝為自訂的錯誤回應物件 {@link ErrorResponse}，
     * 並記錄詳細的錯誤訊息以便日後除錯。
     * </p>
     *
     * @param status HTTP 狀態碼
     * @param errorTitle 錯誤標題 (如 "Resource Not Found"、"Unexpected Error")
     * @param message 詳細的錯誤描述
     * @param request 目前的請求內容
     * @param ex 實際拋出的例外
     * @return 標準格式的錯誤回應 {@link ErrorResponse}
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(
        HttpStatus status,
        String errorTitle,
        String message,
        WebRequest request,
        Exception ex
    ) {
        logger.error("Error message: {} - {}",  errorTitle, ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
            status.value(),
            errorTitle,
            message,
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, status);
    }
}
