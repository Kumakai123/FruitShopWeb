package org.xiangan.fruitshopweb.controller;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xiangan.fruitshopweb.entity.Product;
import org.xiangan.fruitshopweb.entity.Purchase;
import org.xiangan.fruitshopweb.model.PaginationRequest;
import org.xiangan.fruitshopweb.service.ProductService;
import org.xiangan.fruitshopweb.service.PurchaseService;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * 進貨單
 */
@RestController
@RequestMapping("/purchase")
@Slf4j
public class PurchaseController {
	
	@Autowired
	private PurchaseService purchaseService;
	
	@Autowired
	private ProductService productService;
	
	/**
	 * 瀏覽
	 *
	 * @param paginationRequest 分頁請求
	 * @return 可分頁的進貨單
	 */
	@GetMapping
	Page<Purchase> browse(@Validated final PaginationRequest paginationRequest) {
		final int p = paginationRequest.getP();
		
		try {
			return purchaseService
				       .load(
					       
					       p < 1 ? 0 : p - 1,
					       paginationRequest.getS()
				       )
				       .get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"瀏覽進貨單時拋出線程中斷異常：%s❗",
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
	
	/**
	 * @param productId     產品主鍵
	 * @param quantity      數量
	 * @param receivingDate 進貨日期
	 * @return 進貨單
	 */
	@PostMapping
	Purchase create(
		@RequestParam("product") @NotNull(message = "產品不可為空❗") final String productId,
		@RequestParam @NotNull(message = "進貨數量不可為空❗") final Double quantity,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX") final Date receivingDate
	) {
		Purchase purchase = new Purchase();
		
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
		purchase.setProduct(product);
		
		purchase.setQuantity(quantity);
		
		if (Objects.nonNull(receivingDate)) {
			purchase.setReceivingDate(receivingDate);
		}
		
		try {
			return purchaseService.save(purchase).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"建立進貨單時拋出線程中斷異常：%s❗",
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
		Purchase purchase;
		try {
			purchase = purchaseService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取進貨單「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
		
		try {
			return purchaseService.delete(purchase).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"刪除進貨單「%d」時拋出線程中斷異常：%s❗️",
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
	 * @return 進貨單
	 */
	@GetMapping("/{id:^\\d+$}")
	Purchase read(@PathVariable final long id) {
		try {
			return purchaseService
				       .load(id)
				       .get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取進貨單「%d」時拋出線程中斷異常：%s❗",
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
	 * @param id            主鍵
	 * @param productId     產品主鍵
	 * @param quantity      數量
	 * @param orderDate     開單日期
	 * @param receivingDate 進貨日期
	 * @return 進貨單
	 */
	@PostMapping("/{id:^\\d+$}")
	Purchase update(
		@PathVariable final long id,
		@RequestParam("product") final String productId,
		@RequestParam(required = false) final Double quantity,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX") final Date orderDate,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX") final Date receivingDate
	) {
		Purchase purchase;
		try {
			purchase = purchaseService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取進貨單「%d」時拋出線程中斷異常：%s❗",
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
			
			purchase.setProduct(product);
		}
		
		if (Objects.nonNull(quantity)) {
			purchase.setQuantity(quantity);
		}
		
		if (Objects.nonNull(orderDate)) {
			purchase.setOrderDate(orderDate);
		}
		
		if (Objects.nonNull(receivingDate)) {
			purchase.setReceivingDate(receivingDate);
		}
		
		try {
			return purchaseService.save(purchase).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"編輯進貨單「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
}
