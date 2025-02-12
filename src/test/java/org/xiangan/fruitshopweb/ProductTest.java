package org.xiangan.fruitshopweb;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xiangan.fruitshopweb.entity.Consignor;
import org.xiangan.fruitshopweb.entity.Product;
import org.xiangan.fruitshopweb.enumType.ProductTypeEnum;
import org.xiangan.fruitshopweb.service.ConsignorService;
import org.xiangan.fruitshopweb.service.ProductService;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

@SpringBootTest
@Slf4j
public class ProductTest {

    @Autowired
    private ProductService service;
    @Autowired
    private ConsignorService consignorService;

    @Test
    void create() {
        Product product = new Product();
        product.setProductName("肯德基");
        product.setType(ProductTypeEnum.FRUIT);
        product.setInventory(166);
        product.setUnitPrice(BigDecimal.valueOf(100));

        Consignor consignor = null;
        try {
            consignor = consignorService.load(1).get();
            if (consignor == null) {
                throw new IllegalStateException("Consignor not found for ID: 1");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted during Consignor load", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to load Consignor: " + e.getCause().getMessage(), e);
        }

        product.setConsignor(consignor);

        Product saved = null;
        try {
            saved = service.save(product).get();
            if (saved == null) {
                throw new IllegalStateException("Product saving returned null.");
            }
            System.out.println("\n保存結果: " + saved + "\n");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted during Product save", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to save Product: " + e.getCause().getMessage(), e);
        }
    }
}

