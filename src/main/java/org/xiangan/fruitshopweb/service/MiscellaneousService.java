package org.xiangan.fruitshopweb.service;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xiangan.fruitshopweb.entity.Miscellaneous;
import org.xiangan.fruitshopweb.entity.Miscellaneous_;
import org.xiangan.fruitshopweb.repository.MiscellaneousRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * (服務層) 雜物
 */
@Service
@Slf4j
public class MiscellaneousService {
    @Autowired
    private MiscellaneousRepository miscellaneousRepo;

    @Async
    public CompletableFuture<Miscellaneous> load(long id) {
        return CompletableFuture.completedFuture(
            miscellaneousRepo
                .findById(id)
                .orElseThrow(
                    () -> new NoSuchElementException(
                    String.format(
                        "無主鍵為「%d」的雜物❗️",
                        id
                    )
                )));

    }

    @Async
    public CompletableFuture<List<Miscellaneous>> load(){
        return CompletableFuture.completedFuture(
            miscellaneousRepo.findAll()
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

    /**
     * 計算單日總花費金額
     *
     * @param begin 開始
     * @param end 結束
     * @return 總金額
     */
    public CompletableFuture<BigDecimal> dailyAmountSpend(Date begin, Date end){
//       同步  其他
//      return loadBetweenRecordDate(begin, end)
//            .join()
//            .stream()
//            .map(Miscellaneous::getAmount)
//            .filter( Objects::nonNull)
//            .reduce(BigDecimal.ZERO,BigDecimal::add);

        // 非同步
        return loadBetweenRecordDate(begin, end)
            .thenApply(list -> list.stream()
                .map(Miscellaneous::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
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

}
