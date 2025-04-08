package com.equalexperts.shoppingcart.service;

import com.equalexperts.shoppingcart.exception.ProductNotFoundException;
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

    private final Map<String, CartItem> cartItemsMap = new HashMap<>();

    @Value("${spring.tax.percent}")
    BigDecimal taxPercentage;

    private int currentQuantity;

    private int updatedQuantity;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StringBuilder checkoutMsg = new StringBuilder();

    public Map<String, CartItem> getCartItemsMap() { //TODO: Check on this
        return cartItemsMap;
    }

    /**
     * Calculate subtotal, tax and total
     */
    public String checkoutCart() {

        BigDecimal subTotal = new BigDecimal(0);
        BigDecimal tax;

        for (CartItem item : cartItemsMap.values()) {

            logger.info("Cart contains {} x {} @ {} each",
                    item.getQuantity(), item.getProductName(), item.getUnitPrice());

            checkoutMsg.append("Cart contains ").append(item.getQuantity()).append(" x ").append(item.getProductName())
                    .append(" @ ").append(item.getUnitPrice()).append(" each\n");

            subTotal = subTotal.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        logger.info("Subtotal = {}", subTotal);
        checkoutMsg.append("Subtotal = ").append(subTotal);

        logger.info("Tax rate is: {}%", taxPercentage);

        BigDecimal taxRate = taxPercentage.movePointLeft(2);

        logger.info("Tax rate in decimal: {} %", taxRate);

        tax = subTotal.multiply(taxRate).setScale(2 , RoundingMode.HALF_UP);

        logger.info("Tax is: {}", tax);
        checkoutMsg.append("\nTax = ").append(tax);

        checkoutMsg.append("\nTotal = ").append(subTotal.add(tax));

        return checkoutMsg.toString();
    }

    public void deleteProduct(String productName) {

        if (! cartItemsMap.containsKey(productName)) {

            logger.warn("Product Not Found !");

            throw new ProductNotFoundException();
        }

        currentQuantity = cartItemsMap.get(productName).getQuantity();

        if ( currentQuantity > 0){

            updatedQuantity = currentQuantity - 1;

            cartItemsMap.get(productName).setQuantity(updatedQuantity);

            logger.info("Reduced quantity of {} from {} to {}", productName, currentQuantity, updatedQuantity);
        }
    }

    public void updateCart(CartItem cartItem) {

        String productName = cartItem.getProductName();

        int additionalQuantity = cartItem.getQuantity();

        currentQuantity = cartItemsMap.get(productName).getQuantity();

        updatedQuantity = currentQuantity + additionalQuantity;

        //Update cart item quantity
        cartItemsMap.get(productName).setQuantity(updatedQuantity);

        logger.info("Increased quantity of {} from {} to {}", productName, currentQuantity, updatedQuantity);
    }

    public StringBuilder getCheckoutMsg() {
        return checkoutMsg;
    }
}