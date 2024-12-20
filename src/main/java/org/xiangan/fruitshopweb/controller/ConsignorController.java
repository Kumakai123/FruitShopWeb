package org.xiangan.fruitshopweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xiangan.fruitshopweb.entity.Consignor;
import org.xiangan.fruitshopweb.model.PaginationRequest;
import org.xiangan.fruitshopweb.service.ConsignorService;

import java.util.concurrent.ExecutionException;

/**
 * 貨主
 */
@RestController
@RequestMapping("/consignor")
@Slf4j
public class ConsignorController {

	/**
	 * (服務層) 貨主
	 */
	@Autowired
	private ConsignorService consignorService;

	/**
	 * 瀏覽
	 *
	 * @param paginationRequest 分頁請求
	 * @return 可分頁的貨主
	 */
	@GetMapping
	Page<Consignor> browse(@Validated final PaginationRequest paginationRequest) {
		final int p = paginationRequest.getP();
		try {
			return consignorService
				       .load(
					       p < 1 ? 0 : p - 1,
					       paginationRequest.getS()
				       )
				       .get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"瀏覽貨主時拋出線程中斷異常：%s❗",
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}

	/**
	 * 讀取
	 *
	 * @param id 主鍵
	 * @return 貨主
	 */
	@GetMapping("/{id:^\\d+$}")
	Consignor read(@PathVariable final long id) {
		try {
			return consignorService
				       .load(id)
				       .get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取貨主「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}

	/**
	 * 建立
	 *
	 * @param nickName    姓氏
	 * @param name   名字
	 * @param phoneNumber 連絡電話
	 * @param company     公司行號/統編
	 * @return 貨主
	 */
	@PostMapping
	Consignor create(
		@RequestParam final String nickName,
		@RequestParam final String name,
		@RequestParam final String phoneNumber,
		@RequestParam final String company
	) {
		Consignor consignor = new Consignor(
			nickName.trim(),
			name.trim(),
			phoneNumber.trim(),
			company.trim()
		);

		try {
			return consignorService.save(consignor).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"建立貨主時拋出線程中斷異常：%s❗",
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}

	@PostMapping("/{id:^\\d+$}")
	Consignor update(
		@PathVariable final long id,
		@RequestParam final String nickName,
		@RequestParam final String name,
		@RequestParam final String phoneNumber,
		@RequestParam final String company
	) {
		Consignor consignor;
		try {
			consignor = consignorService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取貨主「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}

		if (!nickName.isBlank()) {
			consignor.setNickName(nickName.trim());
		}
		if (!name.isBlank()) {
			consignor.setName(name.trim());
		}
		if (!phoneNumber.isBlank()) {
			consignor.setPhoneNumber(phoneNumber.trim());
		}
		if (!company.isBlank()) {
			consignor.setCompany(company.trim());
		}

		try {
			return consignorService.save(consignor).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"編輯貨主「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}

	/**
	 * 刪除
	 *
	 * @param id 主鍵
	 * @return 是否刪除
	 */
	@DeleteMapping("/{id:^\\d+$}")
	Boolean delete(@PathVariable final long id) {
		Consignor consignor;
		try {
			consignor = consignorService.load(id).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"讀取貨主「%d」時拋出線程中斷異常：%s❗",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}

		try {
			return consignorService.delete(consignor).get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new RuntimeException(
				String.format(
					"刪除貨主「%d」時拋出線程中斷異常：%s❗️",
					id,
					exception.getLocalizedMessage()
				),
				exception
			);
		}
	}
}
