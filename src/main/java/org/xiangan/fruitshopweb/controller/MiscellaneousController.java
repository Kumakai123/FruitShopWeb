package org.xiangan.fruitshopweb.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.xiangan.fruitshopweb.entity.Miscellaneous;
import org.xiangan.fruitshopweb.service.MiscellaneousService;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("amountSpend")
public class MiscellaneousController {

    @Autowired
    private MiscellaneousService miscellaneousService;

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(RuntimeException ex) {
        return "An error occurred: " + ex.getMessage();
    }

    /**
     * 讀取
     *
     * @param id 主鍵
     * @return 產品
     */
    @GetMapping("/{id:^\\d+$}")
    public ResponseEntity<Miscellaneous> read(@PathVariable final long id) {
        try {
            Miscellaneous result = miscellaneousService.load(id).get();
            if (result == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result);
        } catch (InterruptedException | ExecutionException exception) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                String.format("讀取雜物「%d」時發生錯誤：%s", id, exception.getLocalizedMessage()),
                exception
            );
        }
    }
//    Miscellaneous read(@PathVariable final long id) {
//        try {
//            return miscellaneousService.load(id).get();
//        } catch (InterruptedException | ExecutionException exception) {
//            throw new RuntimeException(String.format("讀取雜物「%d」時拋出線程中斷異常：%s❗", id,
//                exception.getLocalizedMessage()), exception);
//        }
//    }

    @PostMapping
    Miscellaneous create(@RequestParam @NotNull(message = "名稱不可為空❗") final String name,
        @RequestParam @NotNull(message = "金額不可為空❗") final BigDecimal amount) {
        //        Miscellaneous miscellaneous = new Miscellaneous(name,amount);
        try {
            return miscellaneousService.save(new Miscellaneous(name, amount)).get();
        } catch (InterruptedException | ExecutionException exception) {
            throw new RuntimeException(String.format("建立雜物清單時拋出線程中斷異常：%s❗",
                exception.getLocalizedMessage()), exception);
        }
    }

    @PostMapping("/{id:^\\d+$}")
    Miscellaneous update(@PathVariable long id,
        @RequestParam @NotNull(message = "名稱不可為空❗") final String name,
        @RequestParam @NotNull(message = "金額不可為空❗") final BigDecimal amount) {
        Miscellaneous miscellaneous;
        try {
            miscellaneous = miscellaneousService.load(id).get();
        } catch (InterruptedException | ExecutionException exception) {
            throw new RuntimeException(String.format("讀取雜物「%d」時拋出線程中斷異常：%s❗", id,
                exception.getLocalizedMessage()), exception);
        }
        miscellaneous.setName(name.trim());
        miscellaneous.setAmount(amount);

        try {
            return miscellaneousService.save(miscellaneous).get();
        } catch (InterruptedException | ExecutionException exception) {
            throw new RuntimeException(String.format("編輯雜物「%d」時拋出線程中斷異常：%s❗", id,
                exception.getLocalizedMessage()), exception);
        }
    }
}
