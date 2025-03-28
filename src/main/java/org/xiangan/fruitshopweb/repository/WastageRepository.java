package org.xiangan.fruitshopweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.xiangan.fruitshopweb.entity.Wastage;

/**
 * (數據存取對象)損耗單
 *
 * @author kyle
 */
@Repository
public interface WastageRepository extends JpaRepository<Wastage, Long>, JpaSpecificationExecutor<Wastage> {
}
