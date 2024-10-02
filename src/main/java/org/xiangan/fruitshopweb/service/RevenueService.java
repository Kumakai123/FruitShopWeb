package org.xiangan.fruitshopweb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xiangan.fruitshopweb.entity.Revenue;
import org.xiangan.fruitshopweb.entity.Revenue_;
import org.xiangan.fruitshopweb.entity.Wastage;
import org.xiangan.fruitshopweb.entity.Wastage_;
import org.xiangan.fruitshopweb.repository.RevenueRepository;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

/**
 * 營業狀況
 */
@Service
@Slf4j
public class RevenueService {
	
	@Autowired
	private RevenueRepository revenueRepository;
	
	/**
	 * @param entity 營業狀況
	 * @return 是否成功刪除
	 */
	@Async
	@Transactional
	public CompletableFuture<Boolean> delete(final Revenue entity) {
		revenueRepository.delete(entity);
		return CompletableFuture.completedFuture(true);
	}
	
	/**
	 * @param p 頁數
	 * @param s 一頁幾筆
	 * @return 可分頁的營業狀況
	 */
	@Async
	@Transactional(readOnly = true)
	public CompletableFuture<Page<Revenue>> load(final int p, final int s) {
		return CompletableFuture.completedFuture(
			revenueRepository
				.findAll(
					(root, criteriaQuery, criteriaBuilder) -> {
						criteriaQuery.orderBy(
							criteriaBuilder.desc(root.get(Revenue_.recordDate)),
							criteriaBuilder.asc(root.get(Revenue_.grossIncome)),
							criteriaBuilder.desc(root.get(Revenue_.netIncome))
						);
						return criteriaBuilder.conjunction();
					},
					PageRequest.of(p, s)
				)
		);
	}
	
	/**
	 * @param id 主鍵
	 * @return 營業狀況
	 */
	@Async
	@Transactional(readOnly = true)
	public CompletableFuture<Revenue> load(final long id) {
		return CompletableFuture.completedFuture(
			revenueRepository
				.findById(id)
				.orElseThrow(
					() -> new NoSuchElementException(
						String.format(
							"無主鍵為「%d」的營業狀況❗️",
							id
						)
					)
				)
		);
	}
	
	/**
	 * @param entity 營業狀況
	 * @return 持久化營業狀況
	 */
	@Async
	@Transactional
	public CompletableFuture<Revenue> save(final Revenue entity) {
		try {
			return CompletableFuture.completedFuture(
				revenueRepository.saveAndFlush(entity)
			);
		} catch (Exception exception) {
			throw new RuntimeException(
				String.format(
					"持久化營業狀況時拋出線程中斷異常：%s❗️",
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
}
