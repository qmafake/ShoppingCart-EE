package com.equalexperts.shoppingcart.service;

import com.equalexperts.shoppingcart.model.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<String, CartItem> cartItemsMap = new HashMap<>();

    @Value("${spring.tax.percent}")
    BigDecimal taxPercentage;

    public Map<String, CartItem> getCartItemsMap() { //TODO: Check on this
        return cartItemsMap;
    }

    public String calculateSubTotal() {

        StringBuilder checkoutMsg = new StringBuilder();
        BigDecimal subTotal = new BigDecimal(0);
        BigDecimal tax = new BigDecimal(0);
        BigDecimal total = new BigDecimal(0);

        for (CartItem item : cartItemsMap.values()) {
            logger.info("Cart contains {} x {} @ {} each",
                    item.getQuantity(), item.getProductName(), item.getUnitPrice());

            checkoutMsg.append("Cart contains ").append(item.getQuantity()).append(" x ").append(item.getProductName())
                    .append(" @ ").append(item.getUnitPrice()).append("each\n");

            subTotal = subTotal.add(item.getUnitPrice());
        }

        logger.info("Subtotal = {}", subTotal);
        checkoutMsg.append("Subtotal = ").append(subTotal);

        logger.info("Tax rate is: {} %", taxPercentage);
        BigDecimal taxRate = taxPercentage.divide(BigDecimal.valueOf(100.00), 2, RoundingMode.HALF_UP);

        tax = subTotal.multiply(taxRate);

        logger.info("Tax is: {} %", tax);
        checkoutMsg.append("Total = ").append(subTotal.add(tax));

        return checkoutMsg.toString();
    }
}