package org.xiangan.fruitshopweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.xiangan.fruitshopweb.entity.Revenue;

/**
 * (數據存取對象)營業狀況
 *
 * @author kyle
 */
@Repository
public interface RevenueRepository extends JpaRepository<Revenue, Long>, JpaSpecificationExecutor<Revenue> {
}
