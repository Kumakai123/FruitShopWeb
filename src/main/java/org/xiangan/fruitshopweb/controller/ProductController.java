package org.xiangan.fruitshopweb.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xiangan.fruitshopweb.entity.Consignor;
import org.xiangan.fruitshopweb.entity.Product;
import org.xiangan.fruitshopweb.enumType.ProductTypeEnum;
import org.xiangan.fruitshopweb.enumType.UnitTypeEnum;
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
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ConsignorService consignorService;
	
	/**
	 * 瀏覽
	 *
	 * @param paginationRequest 分頁請求
	 * @param isAll 是否找全部的產品,true:全部 | false:搜尋庫存大於 0 的
	 * @return 可分頁的產品
	 */
	@GetMapping("/paged")
	Page<Product> browse(@Validated final PaginationRequest paginationRequest,@RequestParam Boolean isAll) {
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
			throw new RuntimeException(
				String.format(
					"瀏覽產品時拋出線程中斷異常：%s❗",
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
	/**
	 * 瀏覽
	 *
	 * @param isAll 是否找全部的產品,true:全部 | false:搜尋庫存大於 0 的
	 * @return 可分頁的產品
	 */
	@GetMapping("/list")
	List<Product> browse(@RequestParam Boolean isAll) {
		 isAll = !Objects.isNull(isAll);
		try {
			return productService.load(isAll).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"瀏覽產品時拋出線程中斷異常：%s❗",
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
	
	/**
	 * 建立
	 *
	 * @param productName 產品名稱
	 * @param unitPrice   產品單價
	 * @param type        產品類型(列舉)
	 * @param unitType    單位(列舉)
	 * @param consignorId 貨主
	 * @param inventory   庫存
	 * @return 產品
	 */
	@PostMapping
	Product create(
		@RequestParam @NotNull(message = "產品名稱不可為空❗") final String productName,
		@RequestParam @NotNull(message = "產品單價不可為空❗") final BigDecimal unitPrice,
		@RequestParam @NotNull(message = "產品類型不可為空❗") final ProductTypeEnum type,
		@RequestParam @NotNull(message = "單位不可為空❗") final UnitTypeEnum unitType,
		@RequestParam("consignorId") final long consignorId,
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
			throw new RuntimeException(
				String.format(
					"讀取貨主「%d」時拋出線程中斷異常：%s❗",
					consignorId,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
		product.setConsignor(consignor);
		
		product.setInventory(inventory);
		
		try {
			return productService.save(product).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"建立產品時拋出線程中斷異常：%s❗",
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
	@DeleteMapping("/{id:^\\d+$}")
	Boolean delete(@PathVariable final long id) {
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
	
	/**
	 * 讀取
	 *
	 * @param id 主鍵
	 * @return 產品
	 */
	@GetMapping("/{id:^\\d+$}")
	Product read(@PathVariable final long id) {
		try {
			return productService
				       .load(id)
				       .get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取產品「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
	
	@PostMapping("/{id:^\\d+$}")
	Consignor update(
		@PathVariable final long id,
		@RequestParam(required = false)  final String lastName,
		@RequestParam(required = false)  final String firstName,
		@RequestParam(required = false) final String phoneNumber,
		@RequestParam(required = false)  final String company
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
		
		if (!lastName.isBlank()) {
			consignor.setNickName(lastName.trim());
		}
		if (!firstName.isBlank()) {
			consignor.setName(firstName.trim());
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
}
