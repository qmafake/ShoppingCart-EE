package com.equalexperts.shoppingcart;

import com.equalexperts.shoppingcart.model.ProductPrice;
import com.equalexperts.shoppingcart.service.ProductPriceClientService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.price.endpoint=http://localhost:8099/"
})
public class ProductPriceClientServiceIntegrationTest2 {

    @Autowired
    private ProductPriceClientService productPriceClientService;

    private final WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8099));

    @Test
    void testFetchItemPrice_liveWithMockedServer() {
        wireMockServer.start();

        String product = "apple";
        String responseJson = """
            {
              "title": "Apple",
              "price": 1.99
            }
            """;

        wireMockServer.stubFor(get(urlEqualTo("/apple.json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseJson)));

        ProductPrice result = productPriceClientService.fetchItemPrice(product);

        assertEquals("Apple", result.getProductName());
        assertEquals(BigDecimal.valueOf(1.99), result.getPrice());

        wireMockServer.stop();
    }

    @Test
    void testFetchItemPrice_notFound() {
        wireMockServer.start();

        String product = "nonexistent";

        wireMockServer.stubFor(get(urlEqualTo("/nonexistent.json"))
                .willReturn(aResponse().withStatus(404)));

        ProductPrice result = productPriceClientService.fetchItemPrice(product);

        assertNull(result.getProductName());
        assertNull(result.getPrice());

        wireMockServer.stop();
    }
}
