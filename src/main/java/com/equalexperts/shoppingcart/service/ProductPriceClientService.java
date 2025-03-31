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
    StringBuilder priceUrl;

    public ProductPrice fetchItemPrice(String product) {

        ProductPrice productPrice = new ProductPrice();

        logger.info("Fetching price for - {}", product);

        try {
            // Construct the URL for the product endpoint
            String priceUrlPath = "https://equalexperts.github.io/backend-take-home-test-data/" + product + ".json";
//            String priceUrlPath = priceUrl.append(product).append(".json").toString();

            HttpResponse<JsonNode> response = Unirest.get(priceUrlPath)
                    .header("accept", "application/json")
                    .asJson();

            if (response.getStatus() == 200) {
                // Parse the JSON response
                JsonObject responseJson = JsonParser.parseString(response.getBody().toString()).getAsJsonObject();

                // Extract the title and price fields from the JSON response
                String title = responseJson.get("title").getAsString();
                double price = responseJson.get("price").getAsDouble();

                logger.info("Retrieved Product: {}, Price: {}", title, price);


                productPrice.setProductName(title);
                productPrice.setPrice(BigDecimal.valueOf(price));

            } else {
                System.out.println("Request failed. Response Code: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close Unirest when done to clean up resources
            Unirest.shutDown();
        }

        return productPrice;
    }
}
