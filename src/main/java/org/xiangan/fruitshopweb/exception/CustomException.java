package org.xiangan.fruitshopweb.exception;

/**
 * 表示自訂的未受檢（Unchecked）例外，用於應用程式中特定的錯誤情境。
 * 此例外類別繼承自 {@code RuntimeException}，屬於未受檢例外（Unchecked Exception）。
 *
 * <p>當遇到需要中斷正常流程且屬於應用程式自訂的錯誤時，可拋出此例外。</p>
 *
 * @see RuntimeException
 * @author kyle
 */
public class CustomException extends RuntimeException{

    /**
     * 使用指定的錯誤訊息建立新的 {@code CustomException} 例外物件。
     *
     * @param message 錯誤訊息，可透過 {@link Throwable#getMessage()} 方法取得。
     */
    public CustomException(String message) {
        super(message);
    }
}
