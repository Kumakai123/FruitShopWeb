package org.xiangan.fruitshopweb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xiangan.fruitshopweb.entity.Purchase;
import org.xiangan.fruitshopweb.entity.Purchase_;
import org.xiangan.fruitshopweb.repository.ProductRepository;
import org.xiangan.fruitshopweb.repository.PurchaseRepository;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

/**
 * 進貨單
 */
@Service
@Slf4j
public class PurchaseService {
	
	/**
	 * (數據存取對象)進貨單
	 */
	@Autowired
	private PurchaseRepository purchaseRepository;
	
	/**
	 * (數據存取對象)產品
	 */
	@Autowired
	private ProductRepository productRepository;
	
	/**
	 * @param entity 產品
	 * @return 是否成功刪除
	 */
	@Async
	@Transactional
	public CompletableFuture<Boolean> delete(final Purchase entity) {
		purchaseRepository.delete(entity);
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
	public CompletableFuture<Purchase> load(final long id) {
		return CompletableFuture.completedFuture(
			purchaseRepository
				.findById(id)
				.orElseThrow(
					() -> new NoSuchElementException(
						String.format(
							"無主鍵為「%d」的進貨單❗️",
							id
						)
					)
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
}
