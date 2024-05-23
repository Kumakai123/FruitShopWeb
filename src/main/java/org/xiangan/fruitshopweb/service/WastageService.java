package org.xiangan.fruitshopweb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xiangan.fruitshopweb.repository.WastageRepository;

@Service
@Slf4j
public class WastageService {
	
	@Autowired
	private WastageRepository wastageRepository;
}
