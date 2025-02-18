package org.xiangan.fruitshopweb.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;

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
     * @param exception 觸發此處理程序的例外
     * @param request 當前的 WebRequest 請求內容
     * @return 標準格式的錯誤回應 {@link ErrorResponse} 及 HTTP 404 狀態碼
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
        CustomException exception, WebRequest request) {
        return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            "無法找到資源或 API，請檢查 URL 是否正確。",
            exception.getMessage(),
            request,
            exception);
    }

    /**
     * 處理資源未找到異常 (NoResourceFoundException)。
     * 通常發生於請求的資源不存在或路由錯誤。
     *
     * @param exception NoResourceFoundException 異常對象
     * @param request   異常請求物件
     * @return          包含錯誤訊息的 ResponseEntity
     */
    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException exception, WebRequest request){
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST
                ,"Resource Not Found"
                ,exception.getMessage()
                ,request
                ,exception
        );
    }

    /**
     * 處理重複鍵異常 (DuplicateKeyException)
     * 常見於插入資料庫時違反唯一約束 (Unique Constraint)
     *
     * @param exception DuplicateKeyException 異常對象
     * @param request   異常請求物件
     * @return          包含錯誤訊息的 ResponseEntity
     */
    @ExceptionHandler({DuplicateKeyException.class})
    public ResponseEntity<ErrorResponse> handleDuplicateKeyException(
            DuplicateKeyException exception, WebRequest request){
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST
                ,"資料重複，違反唯一約束"
                ,exception.getMessage()
                ,request
                ,exception
        );
    }

    /**
     * 處理非法參數異常 (IllegalArgumentException)
     * 常見於參數格式錯誤或不符合預期的業務邏輯
     *
     * @param exception IllegalArgumentException 異常對象
     * @param request   異常請求物件
     * @return          包含錯誤訊息的 ResponseEntity
     */
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception, WebRequest request){
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST
                ,"參數錯誤或格式不正確"
                ,exception.getMessage()
                ,request
                ,exception
        );
    }

    /**
     * 處理查無元素異常 (NoSuchElementException)
     * 常見於資料查詢時無對應結果
     *
     * @param exception NoSuchElementException 異常對象
     * @param request   異常請求物件
     * @return          包含錯誤訊息的 ResponseEntity
     */
    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(
            NoSuchElementException exception, WebRequest request){
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST
                ,"查無對應資料"
                ,exception.getMessage()
                ,request
                ,exception
        );
    }

    /**
     * 處理空指標異常 (NullPointerException)
     * 常見於物件為空卻進行操作時
     *
     * @param exception NullPointerException 異常對象
     * @param request   異常請求物件
     * @return          包含錯誤訊息的 ResponseEntity
     */
    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<ErrorResponse> handleNullPointerException(
            NullPointerException exception, WebRequest request){
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST
                ,"內部錯誤：空指標操作"
                ,exception.getMessage()
                ,request
                ,exception
        );
    }

    /**
     * 處理執行期異常 (RuntimeException)
     * 常見於未預期錯誤或業務邏輯例外
     *
     * @param exception RuntimeException 異常對象
     * @param request   異常請求物件
     * @return          包含錯誤訊息的 ResponseEntity
     */
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException exception, WebRequest request){
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST
                ,"未預期的伺服器錯誤"
                ,exception.getMessage()
                ,request
                ,exception
        );
    }

    /**
     * 處理執行期異常 (HandlerMethodValidationException)
     * 常見於參數輸入錯誤例外
     *
     * @param exception HandlerMethodValidationException 異常對象
     * @param request   異常請求物件
     * @return          包含錯誤訊息的 ResponseEntity
     */
    @ExceptionHandler({HandlerMethodValidationException.class})
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception, WebRequest request){
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST
                ,"參數輸入有誤，請重新確認或參考規則"
                ,exception.getMessage()
                ,request
                ,exception
        );
    }

//    /**
//     * 處理排程器異常 (SchedulerException)
//     * 常見於排程作業執行錯誤或任務無法執行
//     *
//     * @param exception SchedulerException 異常對象
//     * @param request   異常請求物件
//     * @return          包含錯誤訊息的 ResponseEntity
//     */
//    @ExceptionHandler({SchedulerException.class})
//    public ResponseEntity<ErrorResponse> handleSchedulerException(
//            SchedulerException exception, WebRequest request){
//        return buildErrorResponse(
//                HttpStatus.BAD_REQUEST
//                ,"排程作業執行錯誤"
//                ,exception.getMessage()
//                ,request
//                ,exception
//        );
//    }

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
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGenericException(
//        Exception ex, WebRequest request) {
//        return buildErrorResponse(
//            HttpStatus.INTERNAL_SERVER_ERROR,
//            "Unexpected Error",
//            "請聯絡系統管理員",
//            request,
//            ex);
//    }

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
