package org.xiangan.fruitshopweb.service;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xiangan.fruitshopweb.entity.Miscellaneous;
import org.xiangan.fruitshopweb.entity.Miscellaneous_;
import org.xiangan.fruitshopweb.exception.CustomException;
import org.xiangan.fruitshopweb.repository.MiscellaneousRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * (服務層) 雜物
 */
@Service
@Slf4j
public class MiscellaneousService {

	/**
	 * (數據存取對象)雜物
	 */
    private final MiscellaneousRepository miscellaneousRepo;

	/**
	 * 依賴注入
	 * @param miscellaneousRepo the miscellaneousRepo
	 */
    @Autowired
	public MiscellaneousService(MiscellaneousRepository miscellaneousRepo) {
		this.miscellaneousRepo = miscellaneousRepo;
	}

	/**
	 * @param entity 營業狀況
	 * @return 是否成功刪除
	 */
	@Async
	@Transactional
	public CompletableFuture<Boolean> delete(final Miscellaneous entity) {
		miscellaneousRepo.delete(entity);
		return CompletableFuture.completedFuture(true);
	}

	/**
     * 讀取
     *
     * @param id 主鍵
     * @return 雜物
     */
    @Async
    public CompletableFuture<Miscellaneous> load(final String id) {
        return CompletableFuture.completedFuture(
            miscellaneousRepo
                .findOne(
                    ((root, criteriaQuery, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get(Miscellaneous_.ID), id))
                )
                .orElseThrow(
                    () -> new CustomException(
                    String.format("無主鍵為「%s」的雜物❗️", id)
                ))
        );
    }

    @Async
    public CompletableFuture<List<Miscellaneous>> load(){
        return CompletableFuture.completedFuture(
            miscellaneousRepo.findAll()
        );
    }

	/**
	 * 自訂區段查詢的雜物清單
	 *
	 * @param p 頁數
	 * @param s 一頁幾筆
	 * @param begin 起始時間
	 * @param end 結束時間
	 * @return 可分頁自訂區段查詢的雜物清單
	 */
    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<Page<Miscellaneous>> load(
        final int p
        ,final int s
        ,final LocalDateTime begin
        ,final LocalDateTime end) {
        return CompletableFuture.completedFuture(
            miscellaneousRepo
                .findAll(
                    (root, criteriaQuery, criteriaBuilder) -> {
                        Collection<Predicate> predicates = new ArrayList<>();

                        if (Objects.nonNull(begin) && Objects.nonNull(end)){
                            predicates.add(
                                criteriaBuilder.between(
									root.get(Miscellaneous_.recordDate),begin,end)
                            );
                        }else {
							if (Objects.nonNull(begin)){
								criteriaBuilder.greaterThanOrEqualTo(
									root.get(Miscellaneous_.recordDate),begin
								);
							}
	                        if (Objects.nonNull(end)){
		                        criteriaBuilder.greaterThanOrEqualTo(
			                        root.get(Miscellaneous_.recordDate),end
		                        );
	                        }
                        }
                        criteriaQuery.orderBy(
                            criteriaBuilder.desc(root.get(Miscellaneous_.recordDate)),
                            criteriaBuilder.asc(root.get(Miscellaneous_.amount)),
                            criteriaBuilder.desc(root.get(Miscellaneous_.name))
                        );
	                    return criteriaBuilder.and(
		                    predicates.toArray(new Predicate[0])
	                    );
                    },
                    PageRequest.of(p, s)
                )
        );
    }

    /**
     * 查詢日期內的雜物清單
     * 小於某日的
     * 大於某日的
     * 起始日~結束日 期間的
     *
     * @param begin 日期
     * @param end 日期
     * @return 符合日期條件的所有雜物清單
     */
    @Async
    public CompletableFuture<List<Miscellaneous>> loadBetweenRecordDate(Date begin, Date end) {
        return CompletableFuture.completedFuture(
            miscellaneousRepo.findAll(((root, query, criteriaBuilder) ->
                {
                Collection<Predicate> predicate = new ArrayList<>();
                    // 小於某日的
                    if (Objects.nonNull(begin) && Objects.isNull(end)){
                        predicate.add(
                            criteriaBuilder.lessThanOrEqualTo(
                                root.get(Miscellaneous_.RECORD_DATE), begin)
                        );
                    }

                    // 大於某日的
                    if (Objects.nonNull(end) && Objects.isNull(begin)){
                        predicate.add(
                            criteriaBuilder.greaterThanOrEqualTo(
                                root.get(Miscellaneous_.RECORD_DATE),end)
                        );
                    }

                    //起始日~結束日 期間的
                    if (Objects.nonNull(begin) &&Objects.nonNull(end)){
                        predicate.add(
                            criteriaBuilder.between(
                                root.get(Miscellaneous_.RECORD_DATE),begin,end)
                        );
                    }
                    return criteriaBuilder.and(
                        predicate.toArray(new Predicate[0])
                    );
                }
            ))
        );
    }

    @Async
    @Transactional
    public CompletableFuture<Miscellaneous> save(final Miscellaneous entity){
        final BigDecimal amount = entity.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) < 0 ){
            throw new IllegalArgumentException("花費金額不可以小於0");
        }

        try {
            return CompletableFuture.completedFuture(
                miscellaneousRepo.saveAndFlush(entity)
            );
        } catch (Exception exception) {
            throw new RuntimeException(
                String.format(
                    "持久化雜物時拋出線程中斷異常：%s❗️",
                    exception.getLocalizedMessage()
                ),
                exception
            );
        }
    }

	/**
	 * 總計區間內的雜物金額
	 * @param begin 起始時間
	 * @param end   結束時間
	 * @return      雜物總金額
	 */
	@Async
	public CompletableFuture<BigDecimal> sumAmountBetweenRecordDate(final LocalDateTime begin,final LocalDateTime end) {
		return CompletableFuture.supplyAsync(
			() -> miscellaneousRepo.sumAmountBetweenDates(begin, end));
	}

	/**
	 * 編輯
	 *
	 * @param id  主鍵
	 * @param name  總收入
	 * @param amount 淨收入
	 * @return 雜物
	 */
	@Transactional
	public Miscellaneous update(
		final String id
		, final String name
		, final BigDecimal amount) {
		Miscellaneous miscellaneous;
		try {
			miscellaneous = this.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(String.format("讀取雜物「%s」時拋出線程中斷異常：%s❗", id,
				exception.getLocalizedMessage()));
		}

		if (Objects.nonNull(name)){
			miscellaneous.setName(name.trim());
		}
		if (Objects.nonNull(amount)){
			miscellaneous.setAmount(amount);
		}

		try {
			return this.save(miscellaneous).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new CustomException(
				String.format("編輯雜物清單時拋出線程中斷異常：%s❗", exception.getLocalizedMessage())
			);
		}
	}
}
