package org.xiangan.fruitshopweb.controller;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xiangan.fruitshopweb.entity.Product;
import org.xiangan.fruitshopweb.entity.Wastage;
import org.xiangan.fruitshopweb.model.PaginationRequest;
import org.xiangan.fruitshopweb.service.ProductService;
import org.xiangan.fruitshopweb.service.WastageService;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * 損耗單
 */
@RestController
@RequestMapping("/wastage")
@Slf4j
public class WastageController {
	
	@Autowired
	private WastageService wastageService;
	
	@Autowired
	private ProductService productService;
	
	/**
	 * 瀏覽
	 *
	 * @param paginationRequest 分頁請求
	 * @return 可分頁的損耗單
	 */
	@GetMapping
	Page<Wastage> browse(@Validated final PaginationRequest paginationRequest) {
		final int p = paginationRequest.getP();
		
		try {
			return wastageService
				       .load(
					       
					       p < 1 ? 0 : p - 1,
					       paginationRequest.getS()
				       )
				       .get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"瀏覽損耗單時拋出線程中斷異常：%s❗",
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
	
	/**
	 * @param productId 產品主鍵
	 * @param quantity  數量
	 * @param date      進貨日期
	 * @return 損耗單
	 */
	@PostMapping
	Wastage create(
		@RequestParam("product") @NotNull(message = "產品不可為空❗") final String productId,
		@RequestParam final Double quantity,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX") final Date date
	) {
		Wastage wastage = new Wastage();
		
		Product product;
		try {
			product = productService.load(productId).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取產品「%s」時拋出線程中斷異常：%s❗",
					productId,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
		wastage.setProduct(product);
		
		if (Objects.nonNull(quantity)) {
			wastage.setQuantity(quantity);
		}
		wastage.setQuantity(
			wastage.getQuantity()
		);
		
		if (Objects.nonNull(date)) {
			wastage.setDate(date);
		}
		
		try {
			return wastageService.save(wastage).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"建立損耗單時拋出線程中斷異常：%s❗",
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
		Wastage purchase;
		try {
			purchase = wastageService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取損耗單「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
		
		try {
			return wastageService.delete(purchase).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"刪除損耗單「%d」時拋出線程中斷異常：%s❗️",
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
	 * @return 損耗單
	 */
	@GetMapping("/{id:^\\d+$}")
	Wastage read(@PathVariable final long id) {
		try {
			return wastageService
				       .load(id)
				       .get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取損耗單「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
	
	/**
	 * 編輯
	 *
	 * @param id        主鍵
	 * @param productId 產品主鍵
	 * @param quantity  數量
	 * @param date      日期
	 * @return 損耗單
	 */
	@PostMapping("/{id:^\\d+$}")
	Wastage update(
		@PathVariable final long id,
		@RequestParam("product") final String productId,
		@RequestParam(required = false) final Double quantity,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX") final Date date
	) {
		Wastage wastage;
		try {
			wastage = wastageService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取損耗單「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
		
		Product product;
		if (Objects.nonNull(productId)) {
			try {
				product = productService.load(productId).get();
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
			
			wastage.setProduct(product);
		}
		
		if (Objects.nonNull(quantity)) {
			wastage.setQuantity(quantity);
		}
		
		if (Objects.nonNull(date)) {
			wastage.setDate(date);
		}
		
		try {
			return wastageService.save(wastage).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"編輯損耗單「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
}
