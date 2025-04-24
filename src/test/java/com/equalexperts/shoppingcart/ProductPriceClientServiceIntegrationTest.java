package com.equalexperts.shoppingcart;

import com.equalexperts.shoppingcart.model.ProductPrice;
import com.equalexperts.shoppingcart.service.ProductPriceClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.price.endpoint=http://localhost:8099/"
})
public class ProductPriceClientServiceIntegrationTest {

    @Autowired
    private ProductPriceClientService productPriceClientService;

    @Test
    void testFetchItemPrice_liveWithMockedServer() {

        String product = "cornflakes";

        ProductPrice result = productPriceClientService.fetchItemPrice(product);

        assertEquals("cornflakes", result.getProductName());
        assertEquals(BigDecimal.valueOf(2.52), result.getPrice());

    }

    @Test
    void testFetchItemPrice_notFound() {

        String product = "nonexistent";

        ProductPrice result = productPriceClientService.fetchItemPrice(product);

        assertNull(result.getProductName());
        assertNull(result.getPrice());
    }
}
