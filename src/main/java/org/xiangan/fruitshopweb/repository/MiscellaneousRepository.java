package org.xiangan.fruitshopweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.xiangan.fruitshopweb.entity.Miscellaneous;

import java.util.Date;

/**
 * (數據存取對象)雜物
 */
@Repository
public interface MiscellaneousRepository
    extends JpaRepository<Miscellaneous, Long>, JpaSpecificationExecutor<Miscellaneous> {
    Miscellaneous findByfindByRecordDate( Date date);
    Miscellaneous findByNameAndRecordDate(String name, Date date);

}
