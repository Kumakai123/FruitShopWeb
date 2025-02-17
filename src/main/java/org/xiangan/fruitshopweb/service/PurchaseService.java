package org.xiangan.fruitshopweb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xiangan.fruitshopweb.entity.Product;
import org.xiangan.fruitshopweb.entity.Purchase;
import org.xiangan.fruitshopweb.entity.Purchase_;
import org.xiangan.fruitshopweb.exception.CustomException;
import org.xiangan.fruitshopweb.repository.PurchaseRepository;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 進貨單
 */
@Service
@Slf4j
public class PurchaseService {
	
	/**
	 * (數據存取對象)進貨單
	 */
	private final PurchaseRepository purchaseRepository;

	/**
	 * (服務層) 產品
	 */
	private final ProductService productService;

	/**
	 * 依賴注入
	 * @param purchaseRepository the purchaseRepository
	 * @param productService the productService
	 */
	@Autowired
	public PurchaseService(PurchaseRepository purchaseRepository, ProductService productService) {
		this.purchaseRepository = purchaseRepository;
		this.productService = productService;
	}

	/**
	 * @param id 主鍵
	 * @return 是否成功刪除
	 */
	@Async
	@Transactional
	public CompletableFuture<Boolean> delete(final String id) {
		Purchase purchase;
		try {
			purchase = this.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("讀取進貨單「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}

		String productID = purchase.getProduct().getId();
		Product product;
		try {
			product = productService.load(productID).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("讀取產品「%s」時拋出線程中斷異常：%s❗", productID, exception.getLocalizedMessage()));
		}

		Double quantity = purchase.getQuantity();
		double inventory = product.getInventory();
		product.setInventory(inventory-quantity);

		purchaseRepository.delete(purchase);
		return CompletableFuture.completedFuture(true);
	}
	
	/**
	 * @param p 頁數
	 * @param s 一頁幾筆
	 * @return 可分頁的進貨單
	 */
	@Async
	@Transactional(readOnly = true)
	public CompletableFuture<Page<Purchase>> load(final int p, final int s) {
		return CompletableFuture.completedFuture(
			purchaseRepository
				.findAll(
					(root, criteriaQuery, criteriaBuilder) -> {
						criteriaQuery.orderBy(
							criteriaBuilder.desc(root.get(Purchase_.receivingDate)),
							criteriaBuilder.desc(root.get(Purchase_.orderDate)),
							criteriaBuilder.asc(root.get(Purchase_.product))
						);
						return criteriaBuilder.conjunction();
					},
					PageRequest.of(p, s)
				)
		);
	}
	
	/**
	 * @param id 主鍵
	 * @return 進貨單
	 */
	@Async
	@Transactional(readOnly = true)
	public CompletableFuture<Purchase> load(final String id) {
		return CompletableFuture.completedFuture(
			purchaseRepository
				.findOne(
					((root, criteriaQuery, criteriaBuilder) ->
						criteriaBuilder.equal(root.get(Purchase_.ID),id))
				)
				.orElseThrow(
					() -> new NoSuchElementException(
						String.format("無主鍵為「%s」的進貨單❗️", id))
				)
		);
	}
	
	/**
	 * @param entity 進貨單
	 * @return 持久化進貨單
	 */
	@Async
	@Transactional
	public CompletableFuture<Purchase> save(final Purchase entity) {
		try {
			return CompletableFuture.completedFuture(
				purchaseRepository.saveAndFlush(entity)
			);
		} catch (Exception exception) {
			throw new RuntimeException(
				String.format(
					"持久化進貨單時拋出線程中斷異常：%s❗️",
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}

	/**
	 * 建立
	 * @param productId 產品主鍵
	 * @param quantity 進貨數量
	 * @param receivingDate 進貨日期
	 * @return 進貨單
	 */
	@Transactional
	public Purchase create(
		final String productId
		,final Double quantity
		,final Date receivingDate){
		Purchase purchase = new Purchase();

		Product product;
		try {
			product = productService.load(productId).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("讀取產品「%s」時拋出線程中斷異常：%s❗", productId, exception.getLocalizedMessage()));
		}
		purchase.setProduct(product);

		purchase.setQuantity(quantity);

		// 庫存數量+進貨數量
		double inventory = product.getInventory();
		product.setInventory(inventory+quantity);

		purchase.setReceivingDate(receivingDate);

		try {
			return this.save(purchase).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("建立進貨單時拋出線程中斷異常：%s❗", exception.getLocalizedMessage()));
		}
	}

	/**
	 * 編輯進貨單
	 * @param id 進貨單主鍵
	 * @param productId 產品主鍵
	 * @param quantity 進貨數量
	 * @param receivingDate 進貨日期
	 * @return 進貨單
	 */
	@Transactional
	public Purchase update(
		final String id
		,final String productId
		,final Double quantity
		,final Date receivingDate){
		Purchase purchase;
		try {
			purchase = this.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("讀取進貨單「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}

		Product product = null;
		if (Objects.nonNull(productId)) {
			try {
				product = productService.load(productId).get();
			} catch (InterruptedException | ExecutionException exception) {
				throw new CustomException(
					String.format("讀取產品「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
			}

			purchase.setProduct(product);
		}

		/*
		進貨單(前):apple* 10 ->beforeQuantity  ，產品: 20 ->beforeInventory
		進貨單(後):apple* 15 ->quantity        ，產品: 25 ->beforeInventory + fixedQuantity
		 */
		if (Objects.nonNull(quantity)) {
			Double beforeQuantity = purchase.getQuantity();
			double fixedQuantity = quantity - beforeQuantity;
			double beforeInventory = product.getInventory();


			purchase.setQuantity(quantity);
			product.setInventory(beforeInventory+fixedQuantity);
		}

		if (Objects.nonNull(receivingDate)) {
			purchase.setReceivingDate(receivingDate);
		}

		try {
			return this.save(purchase).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format(
					"編輯進貨單「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}
	}
}
