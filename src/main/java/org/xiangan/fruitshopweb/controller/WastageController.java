package org.xiangan.fruitshopweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiangan.fruitshopweb.service.WastageService;

/**
 *
 */
@RestController
@Controller
@RequestMapping
public class WastageController {
	
	@Autowired
	private WastageService wastageService;
}
