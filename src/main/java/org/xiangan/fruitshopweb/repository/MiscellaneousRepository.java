package org.xiangan.fruitshopweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.xiangan.fruitshopweb.entity.Miscellaneous;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * (數據存取對象)雜物
 */
@Repository
public interface MiscellaneousRepository
    extends JpaRepository<Miscellaneous, Long>, JpaSpecificationExecutor<Miscellaneous> {
	@Query("SELECT COALESCE(SUM(m.amount), 0) FROM Miscellaneous m " +
		"WHERE m.recordDate BETWEEN :begin AND :end")
	BigDecimal sumAmountBetweenDates(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
}
