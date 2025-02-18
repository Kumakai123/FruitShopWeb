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
import org.xiangan.fruitshopweb.exception.CustomException;
import org.xiangan.fruitshopweb.repository.RevenueRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 營業狀況
 */
@Service
@Slf4j
public class RevenueService {

	/**
	 * (數據存取對象)營業狀況
	 */
	private final RevenueRepository revenueRepository;

	/**
	 * 依賴注入
	 * @param revenueRepository the revenueRepository
	 */
	@Autowired
	public RevenueService(RevenueRepository revenueRepository) {
		this.revenueRepository = revenueRepository;
	}

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
	 * 建立
	 *
	 * @param grossIncome          總收入
	 * @param netIncome            淨收入
	 * @param purchasesExpense     進貨成本
	 * @param personnelExpenses    人事成本
	 * @param miscellaneousExpense 雜物成本
	 * @param wastage              損耗
	 * @return 營收狀況
	 */
	@Transactional
	public Revenue create(
			final BigDecimal grossIncome
			, final BigDecimal netIncome
			, final BigDecimal purchasesExpense
			, final BigDecimal personnelExpenses
			, final BigDecimal miscellaneousExpense
			, final BigDecimal wastage
	) {
		Revenue revenue = new Revenue();
		checkAmountLength("grossIncome", grossIncome);
		checkAmountLength("netIncome", netIncome);
		checkAmountLength("purchasesExpense", purchasesExpense);
		checkAmountLength("personnelExpenses", personnelExpenses);
		checkAmountLength("miscellaneousExpense", miscellaneousExpense);
		checkAmountLength("wastage", wastage);
		// 總收入
		revenue.setGrossIncome(grossIncome);
		// 淨收入
		revenue.setNetIncome(netIncome);
		// 進貨支出
		revenue.setPurchasesExpense(purchasesExpense);
		// 人事成本
		revenue.setPersonnelExpenses(personnelExpenses);
		// 雜物成本
		revenue.setMiscellaneousExpense(miscellaneousExpense);
		// 損耗成本
		revenue.setWastage(wastage);

		try {
			return this.save(revenue).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
					String.format(
							"建立營收狀況時拋出線程中斷異常：%s❗", exception.getLocalizedMessage()));
		}
	}

	/**
	 * 檢查金額長度
	 * @param fieldName 欄位名稱
	 * @param amount 金額
	 */
	private void checkAmountLength(String fieldName, BigDecimal amount) {
		String integerPart = amount.setScale(0, RoundingMode.DOWN).toPlainString();
		if (integerPart.length() > 10) {
			throw new IllegalArgumentException(
					String.format("%s 金額上限不可超過10位整數(目前長度:%d)",fieldName,integerPart.length())
			);
		}
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
	public CompletableFuture<Revenue> load(final String id) {
		return CompletableFuture.completedFuture(
				revenueRepository
						.findOne(
								((root, criteriaQuery, criteriaBuilder) ->
										criteriaBuilder.equal(root.get(Revenue_.id), id))
						)
						.orElseThrow(
								() -> new NoSuchElementException(
										String.format("無主鍵為「%s」的營業狀況❗️", id))
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

	/**
	 * 編輯
	 *
	 * @param id                   主鍵
	 * @param grossIncome          總收入
	 * @param netIncome            淨收入
	 * @param purchasesExpense     進貨成本
	 * @param personnelExpenses    人事成本
	 * @param miscellaneousExpense 雜物成本
	 * @param wastage              損耗
	 * @return 營收狀況
	 */
	@Transactional
	public Revenue update(
			final String id
			, final BigDecimal grossIncome
			, final BigDecimal netIncome
			, final BigDecimal purchasesExpense
			, final BigDecimal personnelExpenses
			, final BigDecimal miscellaneousExpense
			, final BigDecimal wastage) {
		Revenue revenue;
		try {
			revenue = this.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
					String.format(
							"讀取營收狀況「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}

		// 總收入
		if (Objects.nonNull(grossIncome)) {
			checkAmountLength("grossIncome", grossIncome);
			revenue.setGrossIncome(grossIncome);
		}

		// 淨收入
		if (Objects.nonNull(netIncome)) {
			checkAmountLength("netIncome", netIncome);
			revenue.setNetIncome(netIncome);
		}

		// 進貨支出
		if (Objects.nonNull(purchasesExpense)) {
			checkAmountLength("purchasesExpense", purchasesExpense);
			revenue.setPurchasesExpense(purchasesExpense);
		}

		// 人事成本
		if (Objects.nonNull(personnelExpenses)) {
			checkAmountLength("personnelExpenses", personnelExpenses);
			revenue.setPersonnelExpenses(personnelExpenses);
		}

		// 雜物成本
		if (Objects.nonNull(miscellaneousExpense)) {
			checkAmountLength("miscellaneousExpense", miscellaneousExpense);
			revenue.setMiscellaneousExpense(miscellaneousExpense);
		}

		if (Objects.nonNull(wastage)) {
			checkAmountLength("wastage", wastage);
			revenue.setWastage(wastage);
		}

		try {
			return this.save(revenue).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
					String.format(
							"編輯營收狀況「%s」時拋出線程中斷異常：%s❗", id, exception.getLocalizedMessage()));
		}
	}
}
