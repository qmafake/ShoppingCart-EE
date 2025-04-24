package com.equalexperts.shoppingcart;

import com.equalexperts.shoppingcart.model.ProductPrice;
import com.equalexperts.shoppingcart.service.ProductPriceClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class ProductPriceClientServiceIntegrationTest {

    @Autowired
    private ProductPriceClientService productPriceClientService;

    @Test
    void testFetchItemPrice_liveWithMockedServer() {

        String product = "cheerios";

        ProductPrice result = productPriceClientService.fetchItemPrice(product);

        assertEquals("Cheerios", result.getProductName());
        assertEquals(BigDecimal.valueOf(8.43), result.getPrice());

    }

    @Test
    void testFetchItemPrice_notFound() {

        String product = "nonexistent";

        ProductPrice result = productPriceClientService.fetchItemPrice(product);

        assertNull(result.getProductName());
        assertNull(result.getPrice());
    }
}
