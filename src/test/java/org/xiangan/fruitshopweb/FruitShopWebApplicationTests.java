package org.xiangan.fruitshopweb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;
import org.xiangan.fruitshopweb.entity.Miscellaneous;
import org.xiangan.fruitshopweb.service.MiscellaneousService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class FruitShopWebApplicationTests {

	@Autowired
	private MiscellaneousService miscellaneousService;

	@Test
	void createTest(){
		Miscellaneous entity = new Miscellaneous("dsdsd", BigDecimal.valueOf(666));
		miscellaneousService.save(entity);
		System.out.println("\n 建立成功");
	}
	@Test
	void readAllDataTest() throws Exception {
		// 查詢數據
		List<Miscellaneous> miscellaneous = miscellaneousService.load().get();
		// 打印結果
		if (!CollectionUtils.isEmpty(miscellaneous)) {
			System.out.println("\n查詢結果: " + miscellaneous+"\n");
		} else {
			System.out.println("未找到任何資料");
		}
	}
	@Test
	void readDataTest() throws Exception {
		// 查詢數據
		Optional<Miscellaneous> result = Optional.ofNullable(miscellaneousService.load("1").get());

		// 打印結果
		if (result.isPresent()) {
			System.out.println("\n查詢結果: " + result.get()+"\n");
		} else {
			System.out.println("未找到任何資料");
		}
	}

}
