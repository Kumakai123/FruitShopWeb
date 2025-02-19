package org.xiangan.fruitshopweb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xiangan.fruitshopweb.entity.Product;
import org.xiangan.fruitshopweb.entity.Wastage;
import org.xiangan.fruitshopweb.entity.Wastage_;
import org.xiangan.fruitshopweb.exception.CustomException;
import org.xiangan.fruitshopweb.repository.WastageRepository;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 損耗單
 */
@Service
@Slf4j
public class WastageService {
	
	/**
	 * (數據存取對象)損耗單
	 */
	private final WastageRepository wastageRepository;

	/**
	 * (服務層) 產品
	 */
	private final ProductService productService;

	/**
	 * 依賴注入
	 * @param wastageRepository the wastageRepository
	 * @param productService the productService
	 */
	@Autowired
	public WastageService(WastageRepository wastageRepository, ProductService productService) {
		this.wastageRepository = wastageRepository;
		this.productService = productService;
	}

	/**
	 * 建立
	 *
	 * @param productId 產品主鍵
	 * @param quantity 損耗數量
	 * @param date 損耗日期
	 * @return 損耗單
	 */
	@Transactional
	public Wastage create(
			final String productId
			,final Double quantity
			,final Date date
	) {
		Wastage wastage = new Wastage();

		Product product;
		try {
			product = productService.load(productId).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
					String.format(
							"讀取產品「%s」時拋出線程中斷異常：%s❗", productId, exception.getLocalizedMessage()));
		}
		wastage.setProduct(product);

		wastage.setQuantity(quantity);
		double inventory = product.getInventory() - quantity;
		product.setInventory(inventory);

		wastage.setDate(date);

		try {
			return this.save(wastage).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
					String.format(
							"建立損耗單時拋出線程中斷異常：%s❗", exception.getLocalizedMessage()));
		}
	}

	/**
	 * 刪除
	 *
	 * @param id 主鍵
	 * @return 是否成功刪除
	 */
	@Async
	@Transactional
	public CompletableFuture<Boolean> delete(final String id) {
		Wastage wastage;
		try {
			wastage = this.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
					String.format(
							"讀取損耗表「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}

		String productID = wastage.getProduct().getId();
		Product product;
		try {
			product = productService.load(productID).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
					String.format(
							"讀取產品「%s」時拋出線程中斷異常：%s❗", productID, exception.getLocalizedMessage()));
		}

		Double quantity = wastage.getQuantity();
		double inventory = product.getInventory();

		product.setInventory(inventory+quantity);

		wastageRepository.delete(wastage);

		return CompletableFuture.completedFuture(true);
	}

	/**
	 * @param p 頁數
	 * @param s 一頁幾筆
	 * @return 可分頁的損耗單
	 */
	@Async
	@Transactional(readOnly = true)
	public CompletableFuture<Page<Wastage>> load(final int p, final int s) {
		return CompletableFuture.completedFuture(
			wastageRepository
				.findAll(
					(root, criteriaQuery, criteriaBuilder) -> {
						criteriaQuery.orderBy(
							criteriaBuilder.desc(root.get(Wastage_.date)),
							criteriaBuilder.asc(root.get(Wastage_.product)),
							criteriaBuilder.desc(root.get(Wastage_.quantity))
						);
						return criteriaBuilder.conjunction();
					},
					PageRequest.of(p, s)
				)
		);
	}
	
	/**
	 * @param id 主鍵
	 * @return 損耗單
	 */
	@Async
	@Transactional(readOnly = true)
	public CompletableFuture<Wastage> load(final String id) {
		return CompletableFuture.completedFuture(
			wastageRepository
				.findOne(
						((root, criteriaQuery, criteriaBuilder) ->
								criteriaBuilder.equal(root.get(Wastage_.id), id))
				)
				.orElseThrow(
					() -> new CustomException(
						String.format("無主鍵為「%s」的損耗單❗️", id))
				)
		);
	}
	
	/**
	 * @param entity 損耗單
	 * @return 持久化損耗單
	 */
	@Async
	@Transactional
	public CompletableFuture<Wastage> save(final Wastage entity) {
		try {
			return CompletableFuture.completedFuture(
				wastageRepository.saveAndFlush(entity)
			);
		} catch (Exception exception) {
			throw new CustomException(
				String.format(
						"持久化損耗單時拋出線程中斷異常：%s❗️", exception.getLocalizedMessage()));
		}
	}

	/**
	 * 編輯
	 * 
	 * @param id 損耗表主鍵
	 * @param productId 產品ID
	 * @param quantity 損耗數量
	 * @param date 損耗日期
	 * @return 損耗表
	 */
	@Transactional
	public Wastage update(
			final String id
			,final String productId
			,final Double quantity
			,final Date date){
		Wastage wastage;
		try {
			wastage = this.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
					String.format(
							"讀取損耗表「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}

		Product product = null;
		if (Objects.nonNull(productId)) {
			try {
				product = productService.load(productId).get();
			} catch (InterruptedException | ExecutionException exception) {
				throw new CustomException(
						String.format(
								"讀取產品「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
			}

			wastage.setProduct(product);
		}

		/*
		損耗表(前):apple* 10 ->beforeQuantity  ，產品: 20 ->beforeInventory
		損耗表(後):apple* 15 ->quantity        ，產品: 15 ->beforeInventory - fixedQuantity
		 */
		if (Objects.nonNull(quantity)) {
			Double beforeQuantity = wastage.getQuantity();
			double fixedQuantity = quantity - beforeQuantity;
			double beforeInventory = product.getInventory();
			double afterInventory = beforeInventory - fixedQuantity;

			wastage.setQuantity(quantity);
			if (afterInventory < 0){
				throw new CustomException(
						String.format(
								"產品「%s」 損耗數量異常，目前庫存數量為: %d %s"
								, product.getProductName()
								, quantity.intValue()
								, product.getUnitType().getChinese())
				);
			}
			product.setInventory(afterInventory);
		}

		if (Objects.nonNull(date)) {
			wastage.setDate(date);
		}

		try {
			return this.save(wastage).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
					String.format(
							"編輯損耗表「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}
	}
}
