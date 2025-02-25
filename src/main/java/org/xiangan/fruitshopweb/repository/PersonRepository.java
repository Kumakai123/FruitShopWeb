package org.xiangan.fruitshopweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.xiangan.fruitshopweb.entity.Person;

import java.util.Optional;

/**
 * (數據存取對象)人員
 *
 * @author kyle
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
	Optional<Person> findByEmail(String email);
}
