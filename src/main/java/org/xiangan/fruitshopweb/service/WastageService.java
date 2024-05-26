package org.xiangan.fruitshopweb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xiangan.fruitshopweb.entity.Wastage;
import org.xiangan.fruitshopweb.entity.Wastage_;
import org.xiangan.fruitshopweb.repository.WastageRepository;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

/**
 * 損耗單
 */
@Service
@Slf4j
public class WastageService {
	
	/**
	 * (數據存取對象)損耗單
	 */
	@Autowired
	private WastageRepository wastageRepository;
	
	/**
	 * @param entity 損耗單
	 * @return 是否成功刪除
	 */
	@Async
	@Transactional
	public CompletableFuture<Boolean> delete(final Wastage entity) {
		wastageRepository.delete(entity);
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
	public CompletableFuture<Wastage> load(final long id) {
		return CompletableFuture.completedFuture(
			wastageRepository
				.findById(id)
				.orElseThrow(
					() -> new NoSuchElementException(
						String.format(
							"無主鍵為「%d」的損耗單❗️",
							id
						)
					)
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
			throw new RuntimeException(
				String.format(
					"持久化損耗單時拋出線程中斷異常：%s❗️",
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
}
