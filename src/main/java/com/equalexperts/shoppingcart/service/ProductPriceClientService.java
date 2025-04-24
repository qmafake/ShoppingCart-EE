package com.equalexperts.shoppingcart.service;

import com.equalexperts.shoppingcart.model.ProductPrice;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductPriceClientService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.price.endpoint}")
    public String priceUrl;

    public ProductPrice fetchItemPrice(String product) {

        ProductPrice productPrice = new ProductPrice();

        try {

            String priceUrlPath = priceUrl.concat(product).concat(".json");

            logger.info("Fetching {} price:  {}", product, priceUrlPath);

            HttpResponse<JsonNode> response = Unirest.get(priceUrlPath)
                    .header("accept", "application/json")
                    .asJson();

            if (response.getStatus() == 200) {

                JsonObject responseJson = JsonParser.parseString(response.getBody().toString()).getAsJsonObject();

                String title = responseJson.get("title").getAsString();
                double price = responseJson.get("price").getAsDouble();

                logger.info("Product: {}, Price: {}", title, price);

                productPrice.setProductName(title);
                productPrice.setPrice(BigDecimal.valueOf(price));

            } else {
                logger.info("Request failed. Response Code: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            Unirest.shutDown();
        }

        return productPrice;
    }
}
