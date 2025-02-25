package org.xiangan.fruitshopweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xiangan.fruitshopweb.entity.Person;
import org.xiangan.fruitshopweb.enumType.LevelEnum;
import org.xiangan.fruitshopweb.exception.CustomException;
import org.xiangan.fruitshopweb.model.ApiResponseDTO;
import org.xiangan.fruitshopweb.model.PaginationRequest;
import org.xiangan.fruitshopweb.model.RegisterRequest;
import org.xiangan.fruitshopweb.service.AuthenticationService;
import org.xiangan.fruitshopweb.service.PersonService;

import java.util.concurrent.ExecutionException;

/**
 * 人員
 *
 * @author kyle
 */
@RequestMapping("/consignor")
@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = "人員 api",description = "維護人員的相關操作")
public class PersonController {

	/**
	 * (服務層) 人員
	 */
	private final PersonService personService;

	/**
	 * (服務層)身分驗證
	 */
	private final AuthenticationService service;

	/**
	 * 瀏覽
	 *
	 * @param paginationRequest 分頁請求
	 * @return 可分頁的人員
	 */
	@Operation(
		summary = "瀏覽所有的人員名單"
		,description = "瀏覽可分頁的所有人員名單"
		,responses = {
			@ApiResponse(responseCode = "200", description = "Success")
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@GetMapping
	Page<Person> browse(@Validated final PaginationRequest paginationRequest) {
		final int p = paginationRequest.getP();
		try {
			return personService
				       .load(
					       p < 1 ? 0 : p - 1,
					       paginationRequest.getS()
				       )
				       .get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("瀏覽人員時拋出線程中斷異常：%s❗", exception.getLocalizedMessage()));
		}
	}

	/**
	 * 讀取
	 *
	 * @param id 主鍵
	 * @return 人員
	 */
	@Operation(
		summary = "讀取一筆人員資料"
		,description = "輸入人員ID讀取資料"
		,parameters = {
			@Parameter(name = "id",description = "人員主鍵 UUID(十碼)") }
		,responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@GetMapping("/{id:[A-Za-z0-9]{10}}")
	Person read(@PathVariable final String id) {
		try {
			return personService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("讀取人員「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}
	}

	/**
	 * 建立
	 *
	 * @param request 註冊請求
	 * @return 人員
	 */
	@Operation(
		summary = "建立人員資料"
		,description = "密碼長度不可小於8碼"
		,responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping
	ResponseEntity<ApiResponseDTO<?>> create(@RequestBody @Validated RegisterRequest request) {
		return service.register(request);
	}

	/**
	 * 編輯
	 *
	 * @param id 主鍵
	 * @param nickName 暱稱/稱呼
	 * @param name 名字
	 * @param phoneNumber 連絡電話
	 * @param company     公司行號/統編
	 * @return 人員
	 */
	@Operation(
		summary = "編輯人員"
		,description = "輸入主鍵，編輯該筆人員。若不輸入參數，則不會更動該欄位資料。"
		,parameters = {
			@Parameter(name = "nickName",description = "暱稱/稱呼",in = ParameterIn.QUERY,example = "陳大哥")
			,@Parameter(name = "name",description = "名字",in = ParameterIn.QUERY,example = "陳浩銘")
			,@Parameter(name = "level",description = "身分等級",in = ParameterIn.QUERY,example = "CONSIGNOR")
			,@Parameter(name = "email",description = "信箱",in = ParameterIn.QUERY,example = "abc123@gmail.com")
			,@Parameter(name = "password",description = "密碼(長度不可小於8碼)",in = ParameterIn.QUERY,example = "test1234")
			,@Parameter(name = "phoneNumber",description = "連絡電話",in = ParameterIn.QUERY,example = "0912345678")
			,@Parameter(name = "company",description = "公司行號/統編",in = ParameterIn.QUERY,example = "test123")
			,@Parameter(name = "id",description = "人員主鍵 UUID(十碼)")}
		,responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping("/{id:[A-Za-z0-9]{10}}")
	Person update(
		 @PathVariable final String id
		,@RequestParam(required = false) final String nickName
		,@RequestParam(required = false) final String name
		,@RequestParam(required = false) final LevelEnum level
		,@RequestParam(required = false) final String email
		,@RequestParam(required = false) final String password
		,@RequestParam(required = false) final String phoneNumber
		,@RequestParam(required = false) final String company
	) {
		return personService.update(id, nickName, name, level,email,password,phoneNumber, company);
	}

	/**
	 * 刪除
	 *
	 * @param id 主鍵
	 * @return 是否刪除
	 */
	@Operation(
		summary = "刪除人員"
		,parameters = {
			@Parameter(name = "id",description = "人員主鍵 UUID(十碼)") }
		,responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@DeleteMapping("/{id:[A-Za-z0-9]{10}}")
	Boolean delete(@PathVariable final String id) {
		Person person;
		try {
			person = personService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("讀取人員「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}

		try {
			return personService.delete(person).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("刪除人員「%s」時拋出線程中斷異常：%s❗️", id, exception.getLocalizedMessage()));
		}
	}
}
