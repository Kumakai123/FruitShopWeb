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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xiangan.fruitshopweb.entity.Consignor;
import org.xiangan.fruitshopweb.entity.Product;
import org.xiangan.fruitshopweb.enumType.ProductTypeEnum;
import org.xiangan.fruitshopweb.enumType.UnitTypeEnum;
import org.xiangan.fruitshopweb.exception.CustomException;
import org.xiangan.fruitshopweb.model.PaginationRequest;
import org.xiangan.fruitshopweb.service.ConsignorService;
import org.xiangan.fruitshopweb.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * 產品
 */
@RestController
@RequestMapping("/product")
@Slf4j
@Tag(name = "產品 api",description = "產品的 CRUD")
public class ProductController {

	/**
	 * (服務層) 產品
	 */
	private final ProductService productService;

	/**
	 * (服務層) 貨主
	 */
	private final ConsignorService consignorService;

	/**
	 * 依賴注入
	 * @param productService the productService
	 * @param consignorService the consignorService
	 */
	@Autowired
	public ProductController(ProductService productService, ConsignorService consignorService) {
		this.productService = productService;
		this.consignorService = consignorService;
	}

	/**
	 * 瀏覽
	 *
	 * @param paginationRequest 分頁請求
	 * @param isAll 是否找全部的產品,true:全部 | false:搜尋庫存大於 0 的
	 * @return 可分頁的產品
	 */
	@Operation(
		summary = "瀏覽可分頁的所有產品名單"
		,parameters = {
			@Parameter(name = "isAll",description = "是否找全部的產品,true:全部 | false:搜尋庫存大於 0 的")}
		,responses = {
		@ApiResponse(responseCode = "200", description = "Success")
		,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
		,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@GetMapping("/paged")
	Page<Product> browse(
		@Validated final PaginationRequest paginationRequest
		,@RequestParam Boolean isAll) {
		final int p = paginationRequest.getP();
		 isAll = !Objects.isNull(isAll);
		try {
			return productService
				       .load(
					       
					       p < 1 ? 0 : p - 1,
					       paginationRequest.getS(),
						   isAll
				       )
				       .get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("瀏覽產品時拋出線程中斷異常：%s❗", exception.getLocalizedMessage()));
		}
	}

	/**
	 * 瀏覽
	 *
	 * @param isAll 是否找全部的產品,true:全部 | false:搜尋庫存大於 0 的
	 * @return 全部的產品
	 */
	@Operation(
		summary = "瀏覽所有的產品名單(無分頁)"
		,parameters = {
		@Parameter(name = "isAll",description = "是否找全部的產品,true:全部 | false:搜尋庫存大於 0 的")}
		,responses = {
		@ApiResponse(responseCode = "200", description = "Success")
		,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
		,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@GetMapping("/list")
	List<Product> browse(@RequestParam Boolean isAll) {
		 isAll = !Objects.isNull(isAll);
		try {
			return productService.load(isAll).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("瀏覽產品時拋出線程中斷異常：%s❗", exception.getLocalizedMessage()));
		}
	}
	
	/**
	 * 建立
	 *
	 * @param productName 產品名稱
	 * @param unitPrice   產品單價
	 * @param type        產品類型(列舉)
	 * @param unitType    單位(列舉)
	 * @param consignorId 貨主主鍵
	 * @param inventory   庫存
	 * @return 產品
	 */
	@Operation(
		summary = "建立產品資料"
		,parameters = {
		@Parameter(name = "productName",description = "產品名稱",in = ParameterIn.QUERY,example = "青森蘋果")
		,@Parameter(name = "unitPrice",description = "產品單價",in = ParameterIn.QUERY,example = "50")
		,@Parameter(name = "type",description = "產品類型(列舉)",in = ParameterIn.QUERY,example = "FRUIT")
		,@Parameter(name = "unitType",description = "單位(列舉)",in = ParameterIn.QUERY,example = "PIECE")
		,@Parameter(name = "consignorId",description = "貨主主鍵",in = ParameterIn.QUERY,example = "y6uItannsE")
		,@Parameter(name = "inventory",description = "庫存",in = ParameterIn.QUERY,example = "20") }
		,responses = {
		@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
		,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
		,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping
	Product create(
		@RequestParam @NotNull(message = "產品名稱不可為空❗") final String productName,
		@RequestParam @NotNull(message = "產品單價不可為空❗") final BigDecimal unitPrice,
		@RequestParam @NotNull(message = "產品類型不可為空❗") final ProductTypeEnum type,
		@RequestParam @NotNull(message = "單位不可為空❗") final UnitTypeEnum unitType,
		@RequestParam("consignorId") final String consignorId,
		@RequestParam final double inventory
	) {
		Product product = new Product();
		
		product.setProductName(productName.trim());
		product.setUnitPrice(unitPrice);
		product.setType(type);
		product.setUnitType(unitType);
		
		Consignor consignor;
		try {
			consignor = consignorService.load(consignorId).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("讀取貨主「%s」時拋出線程中斷異常：%s❗", consignorId, exception.getLocalizedMessage()));
		}
		product.setConsignor(consignor);
		product.setInventory(inventory);
		
		try {
			return productService.save(product).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("建立產品時拋出線程中斷異常：%s❗", exception.getLocalizedMessage()));
		}
	}
	
//	/**
//	 * 刪除
//	 *
//	 * @param id 主鍵
//	 * @return 是否刪除
//	 */
//	@DeleteMapping("/{id:^\\d+$}")
//	Boolean delete(@PathVariable final long id) {
//		Consignor consignor;
//		try {
//			consignor = consignorService.load(id).get();
//		} catch (InterruptedException | ExecutionException exception) {
//			throw new RuntimeException(
//				String.format(
//					"讀取貨主「%d」時拋出線程中斷異常：%s❗",
//					id,
//					exception.getLocalizedMessage()
//				),
//				exception
//			);
//		}
//
//		try {
//			return consignorService.delete(consignor).get();
//		} catch (InterruptedException | ExecutionException exception) {
//			throw new RuntimeException(
//				String.format(
//					"刪除貨主「%d」時拋出線程中斷異常：%s❗️",
//					id,
//					exception.getLocalizedMessage()
//				),
//				exception
//			);
//		}
//	}
	
	/**
	 * 讀取
	 *
	 * @param id 主鍵
	 * @return 產品
	 */
	@Operation(
		summary = "讀取一筆產品資料"
		,description = "輸入產品ID讀取資料"
		,parameters = {
		@Parameter(name = "id",description = "產品主鍵 UUID(十碼)") }
		,responses = {
		@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
		,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
		,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@GetMapping("/{id:[A-Za-z0-9]{10}}")
	Product read(@PathVariable final String id) {
		try {
			return productService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("讀取產品「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}
	}

	/**
	 * 編輯
	 *
	 * @param id 產品主鍵
	 * @param productName 產品名稱
	 * @param unitPrice 產品單價
	 * @param type 產品類型(列舉)
	 * @param unitType 單位(列舉)
	 * @param consignorId 貨主主鍵
	 * @param inventory 庫存
	 * @return 產品
	 */
	@Operation(
		summary = "編輯產品"
		,description = "輸入主鍵，編輯該筆產品。若不輸入參數，則不會更動該欄位資料。"
		,parameters = {
		@Parameter(name = "productName",description = "產品名稱",in = ParameterIn.QUERY,example = "青森蘋果")
		,@Parameter(name = "unitPrice",description = "產品單價",in = ParameterIn.QUERY,example = "50")
		,@Parameter(name = "type",description = "產品類型(列舉)",in = ParameterIn.QUERY,example = "FRUIT")
		,@Parameter(name = "unitType",description = "單位(列舉)",in = ParameterIn.QUERY,example = "PIECE")
		,@Parameter(name = "consignorId",description = "貨主主鍵",in = ParameterIn.QUERY,example = "y6uItannsE")
		,@Parameter(name = "inventory",description = "庫存",in = ParameterIn.QUERY,example = "20")
		,@Parameter(name = "id",description = "產品主鍵 UUID(十碼)")}
		,responses = {
		@ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
		,@ApiResponse(responseCode = "400", description = "參數有誤", content = @Content)
		,@ApiResponse(responseCode = "500", description = "伺服器請求失敗", content = @Content)
	})
	@PostMapping("/{id:[A-Za-z0-9]{10}}")
	Product update(
		@PathVariable final String id
		,@RequestParam(required = false) final String productName
		,@RequestParam(required = false) final BigDecimal unitPrice
		,@RequestParam(required = false) final ProductTypeEnum type
		,@RequestParam(required = false) final UnitTypeEnum unitType
		,@RequestParam(required = false) final String consignorId
		,@RequestParam(required = false) final Double inventory
	) {
		return productService.update(id, productName, unitPrice, type, unitType,consignorId,inventory);
	}
}
