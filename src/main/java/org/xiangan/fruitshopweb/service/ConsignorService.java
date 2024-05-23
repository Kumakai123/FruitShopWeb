package org.xiangan.fruitshopweb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xiangan.fruitshopweb.repository.ConsignorRepository;

/**
 *
 */
@Service
@Slf4j
public class ConsignorService {
	
	@Autowired
	private ConsignorRepository consignorRepository;
}
