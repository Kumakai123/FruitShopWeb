package org.xiangan.fruitshopweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.xiangan.fruitshopweb.entity.Miscellaneous;
import org.xiangan.fruitshopweb.exception.CustomException;
import org.xiangan.fruitshopweb.model.PaginationRequest;
import org.xiangan.fruitshopweb.model.SummaryAmountDTO;
import org.xiangan.fruitshopweb.service.MiscellaneousService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 雜物 <p>
 * 雜物是為了計算今日的瑣碎支出(例如:塑膠袋、手套、衛生紙...)，或是紀錄一般用品的開銷
 * 可以統計今日、週期、月份的支出
 */
@RestController
@RequestMapping("amountSpend")
@Slf4j
@Tag(name = "雜物 api")
public class MiscellaneousController {

    /*

     */
    /**
     * (服務層) 雜物
     */
    private final MiscellaneousService miscellaneousService;

    /**
     * 依賴注入
     * @param miscellaneousService the miscellaneousService
     */
    @Autowired
    public MiscellaneousController(MiscellaneousService miscellaneousService) {
        this.miscellaneousService = miscellaneousService;
    }

    /**
     * 瀏覽
     *
     * @param paginationRequest 分頁請求
     * @param begin 起始時間
     * @param end 結束時間
     * @return 瀏覽可分頁自訂區段查詢的雜物清單
     */
    @Operation(
        summary = "瀏覽可分頁自訂區段查詢的雜物清單"
        ,parameters = {
        @Parameter(name = "begin",description = "起始時間",in = ParameterIn.QUERY,example = "2025-01-01 12:30:00")
        ,@Parameter(name = "end",description = "結束時間",in = ParameterIn.QUERY,example = "2025-04-01 15:30:00")}
        , responses = {
        @ApiResponse(responseCode = "200", description = "Success")
        , @ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
        , @ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
    })
    @GetMapping
    Page<Miscellaneous> browse(
        @Validated final PaginationRequest paginationRequest
        ,@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")final Date begin
        ,@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")final Date end) {
        final int p = paginationRequest.getP();

        try {
            return miscellaneousService
                .load(
                    p < 1 ? 0 : p - 1
                    ,paginationRequest.getS()
                    ,begin
                    ,end
                )
                .get();
        } catch (InterruptedException | ExecutionException exception) {
            throw new CustomException(
                String.format(
                    "瀏覽雜物時拋出線程中斷異常：%s❗", exception.getLocalizedMessage()));
        }
    }

    /**
     * 刪除
     *
     * @param id 主鍵
     * @return 是否刪除
     */
    @Operation(
        summary = "刪除雜物"
        ,parameters = {
        @Parameter(name = "id",description = "雜物主鍵 UUID(十碼)") }
        ,responses = {
        @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
        ,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
        ,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
    })
    @DeleteMapping("/{id:[A-Za-z0-9]{10}}")
    Boolean delete(@PathVariable final String id) {
        Miscellaneous miscellaneous;
        try {
            miscellaneous = miscellaneousService.load(id).get();
        } catch (InterruptedException | ExecutionException exception) {
            throw new CustomException(
                String.format("讀取雜物「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
        }

        try {
            return miscellaneousService.delete(miscellaneous).get();
        } catch (InterruptedException | ExecutionException exception) {
            throw new CustomException(
                String.format("刪除雜物「%s」時拋出線程中斷異常：%s❗️", id, exception.getLocalizedMessage()));
        }
    }

    /**
     * 讀取
     *
     * @param id 主鍵
     * @return 雜物
     */
    @Operation(
        summary = "輸入雜物ID讀取資料"
        , parameters = {
        @Parameter(name = "id", description = "雜物主鍵 UUID(十碼)")}
        , responses = {
        @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
        , @ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
        , @ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
    })
    @GetMapping("/{id:[A-Za-z0-9]{10}}")
    public ResponseEntity<Miscellaneous> read(@PathVariable final String id) {
        try {
            Miscellaneous result = miscellaneousService.load(id).get();
            if (result == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result);
        } catch (InterruptedException | ExecutionException exception) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                String.format("讀取雜物「%s」時發生錯誤：%s", id, exception.getLocalizedMessage()),
                exception
            );
        }
    }

//    Miscellaneous read(@PathVariable final long id) {
//        try {
//            return miscellaneousService.load(id).get();
//        } catch (InterruptedException | ExecutionException exception) {
//            throw new RuntimeException(String.format("讀取雜物「%s」時拋出線程中斷異常：%s❗", id,
//                exception.getLocalizedMessage()), exception);
//        }
//    }

    /**
     * 建立
     *
     * @param name 名稱
     * @param amount 金額
     * @return 雜物
     */
    @Operation(
        summary = "建立雜物"
        , parameters = {
        @Parameter(name = "name", description = "名稱", in = ParameterIn.QUERY, example = "塑膠袋")
        , @Parameter(name = "amount", description = "金額", in = ParameterIn.QUERY, example = "50")}
        , responses = {
        @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
        , @ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
        , @ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
    })
    @PostMapping
    Miscellaneous create(
        @RequestParam @NotNull(message = "名稱不可為空❗") final String name
        ,@RequestParam @NotNull(message = "金額不可為空❗") final BigDecimal amount) {
        try {
            return miscellaneousService.save(new Miscellaneous(name, amount)).get();
        } catch (InterruptedException | ExecutionException exception) {
            throw new CustomException(
                String.format("建立雜物清單時拋出線程中斷異常：%s❗", exception.getLocalizedMessage())
            );
        }
    }

    /**
     * 總結雜物金額
     *
     * @param begin 起始時間
     * @param end 結束時間
     * @return 雜物總金額
     */
    @Operation(
        summary = "總結雜物金額"
        ,parameters = {
        @Parameter(name = "begin",description = "起始時間")
        ,@Parameter(name = "end",description = "結束時間")}
        , responses = {
        @ApiResponse(responseCode = "200", description = "Success")
        , @ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
        , @ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
    })
    @GetMapping("/summaryAmount")
    CompletableFuture<SummaryAmountDTO> summaryMiscellaneousAmount(
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime begin
        ,@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end
    ){
        LocalDateTime finalBegin =
            Optional
                .ofNullable(begin)
                .orElse(LocalDateTime.of(1970, 1, 1, 0, 0));
        LocalDateTime finalEnd =
            Optional
                .ofNullable(end)
                .orElse(LocalDateTime.now());

        return miscellaneousService.sumAmountBetweenRecordDate(finalBegin, finalEnd)
            .thenApply(amount -> new SummaryAmountDTO(finalBegin, finalEnd, amount))
            .exceptionally(ex -> {
                throw new CustomException(
                    String.format("總結 %s ~ %s 雜物金額時發生錯誤: %s", finalBegin, finalEnd, ex.getMessage())
                );
            });
    }

    /**
     * 編輯
     *
     * @param id 主鍵
     * @param name 名稱
     * @param amount 金額
     * @return 雜物
     */
    @Operation(
        summary = "建立雜物"
        , parameters = {
        @Parameter(name = "id", description = "雜物主鍵 UUID(十碼)")
        ,@Parameter(name = "name", description = "名稱", in = ParameterIn.QUERY, example = "手套")
        , @Parameter(name = "amount", description = "金額", in = ParameterIn.QUERY, example = "50")}
        , responses = {
        @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
        , @ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
        , @ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
    })
    @PostMapping("/{id:[A-Za-z0-9]{10}}")
    Miscellaneous update(
        @PathVariable final String id
        ,@RequestParam @NotNull(message = "名稱不可為空❗") final String name
        ,@RequestParam @NotNull(message = "金額不可為空❗") final BigDecimal amount) {
        return miscellaneousService.update(id, name, amount);
    }
}
