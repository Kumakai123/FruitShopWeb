package org.xiangan.fruitshopweb.service;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xiangan.fruitshopweb.entity.Consignor;
import org.xiangan.fruitshopweb.entity.Product;
import org.xiangan.fruitshopweb.entity.Product_;
import org.xiangan.fruitshopweb.enumType.ProductTypeEnum;
import org.xiangan.fruitshopweb.enumType.UnitTypeEnum;
import org.xiangan.fruitshopweb.exception.CustomException;
import org.xiangan.fruitshopweb.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 產品
 */
@Service
@Slf4j
public class ProductService {

	/**
	 * (數據存取層)產品
	 */
	private final ProductRepository productRepository;

	/**
	 * (服務層) 貨主
	 */
	private final ConsignorService consignorService;

	/**
	 * 依賴注入
	 *
	 * @param productRepository the productRepository
	 * @param consignorService the consignorService
	 */
	@Autowired
	public ProductService(ProductRepository productRepository, ConsignorService consignorService) {
		this.productRepository = productRepository;
		this.consignorService = consignorService;
	}

	/**
	 * @param entity 產品
	 * @return 是否成功刪除
	 */
	@Async
	@Transactional
	public CompletableFuture<Boolean> delete(final Product entity) {
		productRepository.delete(entity);
		return CompletableFuture.completedFuture(true);
	}
	
	/**
	 * @param productName 產品名稱
	 * @param unitPrice   成本單價
	 * @return 是否符合產品名稱、成本單價的產品
	 */
	@Async
	@Transactional(readOnly = true)
	public CompletableFuture<Boolean> exist(
		final String productName,
		final BigDecimal unitPrice
	) {
		return CompletableFuture.completedFuture(
			productRepository
				.exists(
					(root, criteriaQuery, criteriaBuilder) ->
						criteriaBuilder.and(
							criteriaBuilder.equal(root.get(Product_.productName), productName),
							criteriaBuilder.equal(root.get(Product_.unitPrice), unitPrice)
						)
				)
		);
		
	}
	
	/**
	 * @param id          主鍵
	 * @param productName 產品名稱
	 * @param unitPrice   成本單價
	 * @return 是否符合主鍵、產品名稱、成本單價的產品
	 */
	@Async
	@Transactional(readOnly = true)
	public CompletableFuture<Boolean> exist(
		final String id,
		final String productName,
		final BigDecimal unitPrice
	) {
		return CompletableFuture.completedFuture(
			productRepository
				.exists(
					(root, criteriaQuery, criteriaBuilder) ->
						criteriaBuilder.and(
							criteriaBuilder.not(
								criteriaBuilder.equal(root.get(Product_.id), id)
							),
							criteriaBuilder.equal(root.get(Product_.productName), productName),
							criteriaBuilder.equal(root.get(Product_.unitPrice), unitPrice)
						)
				)
		);
		
	}
	
	/**
	 * @param p 頁數
	 * @param s 一 頁幾筆
	 * @param isAll 是否找全部的產品
	 * @return 可分頁的產品
	 */
	@Async
	@Transactional(readOnly = true)
	public CompletableFuture<Page<Product>> load(final int p, final int s,Boolean isAll) {
		return CompletableFuture.completedFuture(
			productRepository
				.findAll(
					(root, criteriaQuery, criteriaBuilder) -> {
					List<Predicate> predicates = new ArrayList<>();
						if (isAll){
							predicates.add(criteriaBuilder.greaterThan(root.get(Product_.INVENTORY),0));
						}
						criteriaQuery.orderBy(
							criteriaBuilder.asc(root.get(Product_.productName)),
							criteriaBuilder.asc(root.get(Product_.unitPrice)),
							criteriaBuilder.asc(root.get(Product_.inventory))
						);
						return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
					},
					PageRequest.of(p, s)
				)
		);
	}

	/**
	 * @param isAll 是否找全部的產品
	 * @return 產品們
	 */
	@Async
	@Transactional(readOnly = true)
	public CompletableFuture<List<Product>> load(Boolean isAll) {
		return CompletableFuture.completedFuture(
			productRepository
				.findAll(
					(root, criteriaQuery, criteriaBuilder) -> {
						List<Predicate> predicates = new ArrayList<>();
						if (isAll){
							predicates.add(criteriaBuilder.greaterThan(root.get(Product_.INVENTORY),0));
						}
						criteriaQuery.orderBy(
							criteriaBuilder.asc(root.get(Product_.productName)),
							criteriaBuilder.asc(root.get(Product_.unitPrice)),
							criteriaBuilder.asc(root.get(Product_.inventory))
						);
						return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
					}
				)
		);
	}

	/**
	 * @param id 主鍵
	 * @return 產品
	 */
	@Async
	@Transactional(readOnly = true)
	public CompletableFuture<Product> load(final String id) {
		return CompletableFuture.completedFuture(
			productRepository
				.findOne(
					((root, criteriaQuery, criteriaBuilder) ->
						criteriaBuilder.equal(root.get(Product_.ID),id))
				)
				.orElseThrow(
					() -> new CustomException(
						String.format("無主鍵為「%s」的產品❗️", id))
				)
		);
	}
	
	/**
	 * @param entity 產品
	 * @return 持久化產品
	 */
	@Async
	@Transactional
	public CompletableFuture<Product> save(final Product entity) {
		final String id = entity.getId();
		final String productName = entity.getProductName();
		final BigDecimal unitPrice = entity.getUnitPrice();
		
		try {
			if (Objects.nonNull(id)) {
				if (exist(id, productName, unitPrice).get()) {
					throw new CustomException(
						String.format("已有重複的產品名稱：%s❗️", productName)
					);
				}
			} else {
				if (exist(productName, unitPrice).get()) {
					throw new CustomException(
						String.format("已有重複的產品名稱：%s❗️", productName));
				}
			}
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("讀取是否有重複的產品時發生線程中斷異常：%s❗️", exception.getLocalizedMessage()));
		}
		
		try {
			return CompletableFuture.completedFuture(
				productRepository.saveAndFlush(entity)
			);
		} catch (Exception exception) {
			throw new CustomException(
				String.format("持久化產品時拋出線程中斷異常：%s❗️", exception.getLocalizedMessage()));
		}
	}

	/**
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
	@Transactional
	public Product update(
		final String id,
		final String productName,
		final BigDecimal unitPrice,
		final ProductTypeEnum type,
		final UnitTypeEnum unitType,
		final String consignorId,
		final Double inventory) {

		Product product;
		try {
			product = this.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("讀取產品「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}

		// 使用 Objects.nonNull() 和 isBlank() 簡化非空邏輯
		if (Objects.nonNull(productName) && !productName.isBlank()) {
			product.setProductName(productName.trim());
		}
		if (Objects.nonNull(unitPrice)) {
			product.setUnitPrice(unitPrice);
		}
		if (Objects.nonNull(type)) {
			product.setType(type);
		}
		if (Objects.nonNull(unitType)) {
			product.setUnitType(unitType);
		}
		if (Objects.nonNull(consignorId) && !consignorId.isBlank()) {
			try {
				Consignor consignor = consignorService.load(consignorId).get();
				product.setConsignor(consignor);
			} catch (InterruptedException | ExecutionException exception) {
				throw new CustomException(
					String.format("讀取貨主「%s」時拋出線程中斷異常：%s❗", consignorId, exception.getLocalizedMessage()));
			}
		}
		if (Objects.nonNull(inventory)) {
			product.setInventory(inventory);
		}

		try {
			return this.save(product).get();
		} catch (Exception exception) {
			throw new CustomException(
				String.format("編輯產品「%s」時拋出線程中斷異常：%s❗", product.getProductName(), exception.getLocalizedMessage()));
		}
	}

}
