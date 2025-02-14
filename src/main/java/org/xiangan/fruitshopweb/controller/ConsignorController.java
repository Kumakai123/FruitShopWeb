package org.xiangan.fruitshopweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xiangan.fruitshopweb.entity.Consignor;
import org.xiangan.fruitshopweb.exception.CustomException;
import org.xiangan.fruitshopweb.model.PaginationRequest;
import org.xiangan.fruitshopweb.service.ConsignorService;

import java.util.concurrent.ExecutionException;

/**
 * 貨主
 */
@RestController
@RequestMapping("/consignor")
@Slf4j
@Tag(name = "貨主 api",description = "貨主的 CRUD")
public class ConsignorController {

	/**
	 * (服務層) 貨主
	 */
	@Autowired
	private ConsignorService consignorService;

	/**
	 * 瀏覽
	 *
	 * @param paginationRequest 分頁請求
	 * @return 可分頁的貨主
	 */
	@Operation(
		summary = "瀏覽所有的貨主名單"
		,description = "瀏覽可分頁的所有貨主名單"
		,requestBody =
			@RequestBody(
				description = "分頁參數"
				,required = true
				,content = @Content(
					mediaType = "application/json"
					,examples =@ExampleObject(
						summary = "分頁參數範例"
						,value = "{\"p\": 1, \"s\": 10}"
			)
			))
		,responses = {
			@ApiResponse(responseCode = "200", description = "Success")
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@GetMapping
	Page<Consignor> browse(@Validated final PaginationRequest paginationRequest) {
		final int p = paginationRequest.getP();
		try {
			return consignorService
				       .load(
					       p < 1 ? 0 : p - 1,
					       paginationRequest.getS()
				       )
				       .get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"瀏覽貨主時拋出線程中斷異常：%s❗",
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}

	/**
	 * 讀取
	 *
	 * @param id 主鍵
	 * @return 貨主
	 */
	@Operation(
		summary = "讀取一筆貨主資料"
		,description = "輸入貨主ID讀取資料"
		,parameters = {
			@Parameter(name = "id",description = "貨主主鍵",in = ParameterIn.QUERY,example = "UUID(十碼)") }
		,responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@GetMapping("/{id:^\\d+$}")
	Consignor read(@PathVariable final String id) {
		try {
			return consignorService
				       .load(id)
				       .get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format(
					"讀取貨主「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				)
			);
		}
	}

	/**
	 * 建立
	 *
	 * @param nickName    暱稱/稱呼
	 * @param name   名字
	 * @param phoneNumber 連絡電話
	 * @param company     公司行號/統編
	 * @return 貨主
	 */
	@Operation(
		summary = "建立貨主資料"
		,parameters = {
			@Parameter(name = "nickName",description = "暱稱/稱呼",in = ParameterIn.QUERY,example = "陳大哥")
			,@Parameter(name = "name",description = "名字",in = ParameterIn.QUERY,example = "陳浩銘")
			,@Parameter(name = "phoneNumber",description = "連絡電話",in = ParameterIn.QUERY,example = "0912345678")
			,@Parameter(name = "company",description = "公司行號/統編",in = ParameterIn.QUERY,example = "test123") }
		,responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping
	Consignor create(
		@RequestParam final String nickName,
		@RequestParam final String name,
		@RequestParam final String phoneNumber,
		@RequestParam final String company
	) {
		Consignor consignor = new Consignor(
			nickName.trim(),
			name.trim(),
			phoneNumber.trim(),
			company.trim()
		);

		try {
			return consignorService.save(consignor).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(String.format(
				"建立貨主時拋出線程中斷異常：%s❗",
				exception.getLocalizedMessage()
			));
		}
	}

	/**
	 * 編輯
	 *
	 * @param id 主鍵
	 * @param nickName 暱稱/稱呼
	 * @param name 名字
	 * @param phoneNumber 連絡電話
	 * @param company     公司行號/統編
	 * @return 貨主
	 */
	@Operation(
		summary = "編輯貨主"
		,description = "輸入主鍵，編輯該筆貨主。若不輸入參數，則不會更動該欄位資料。"
		,parameters = {
			@Parameter(name = "id",description = "貨主主鍵",in = ParameterIn.QUERY,example = "UUID(十碼)")
			,@Parameter(name = "nickName",description = "暱稱/稱呼",in = ParameterIn.QUERY,example = "陳大哥")
			,@Parameter(name = "name",description = "名字",in = ParameterIn.QUERY,example = "陳浩銘")
			,@Parameter(name = "phoneNumber",description = "連絡電話",in = ParameterIn.QUERY,example = "0912345678")
			,@Parameter(name = "company",description = "公司行號/統編",in = ParameterIn.QUERY,example = "test123") }
		,responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping("/{id:^\\s+$}")
	Consignor update(
		@PathVariable final String id,
		@RequestParam(required = false) final String nickName,
		@RequestParam(required = false) final String name,
		@RequestParam(required = false) final String phoneNumber,
		@RequestParam(required = false) final String company
	) {
		Consignor consignor;
		try {
			consignor = consignorService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取貨主「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}

		if (!nickName.isBlank()) {
			consignor.setNickName(nickName.trim());
		}
		if (!name.isBlank()) {
			consignor.setName(name.trim());
		}
		if (!phoneNumber.isBlank()) {
			consignor.setPhoneNumber(phoneNumber.trim());
		}
		if (!company.isBlank()) {
			consignor.setCompany(company.trim());
		}

		try {
			return consignorService.save(consignor).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"編輯貨主「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}

	/**
	 * 刪除
	 *
	 * @param id 主鍵
	 * @return 是否刪除
	 */
	@Operation(
		summary = "刪除貨主"
		,parameters = {
			@Parameter(name = "id",description = "貨主主鍵",in = ParameterIn.QUERY,example = "UUID(十碼)") }
		,responses = {
			@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
			,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
			,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@DeleteMapping("/{id:^\\d+$}")
	Boolean delete(@PathVariable final String id) {
		Consignor consignor;
		try {
			consignor = consignorService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取貨主「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}

		try {
			return consignorService.delete(consignor).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"刪除貨主「%d」時拋出線程中斷異常：%s❗️",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
}
