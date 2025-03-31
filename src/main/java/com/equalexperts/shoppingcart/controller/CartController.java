package com.equalexperts.shoppingcart.controller;

import com.equalexperts.shoppingcart.model.CartItem;
import com.equalexperts.shoppingcart.model.ProductPrice;
import com.equalexperts.shoppingcart.service.CartService;
import com.equalexperts.shoppingcart.service.ProductPriceClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private final CartService cartService;
    @Autowired
    ProductPriceClientService productPriceClientService;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/view")
    public Collection<CartItem> getAllItems() {

        return cartService.getCartItemsMap().values();
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addItem(@RequestBody CartItem item) {

        String productName = item.getProductName();

        ProductPrice productPrice =  productPriceClientService.fetchItemPrice(productName);
        item.setUnitPrice(productPrice.getPrice());

        logger.info("{} cost: {}", productName, productPrice.getPrice());

        if (!cartService.getCartItemsMap().containsKey(productName)){

            logger.info("Adding to cart for item: {}", productName);

            cartService.getCartItemsMap().put(productName, item);

            logger.info("Item added to cart: {}", cartService.getCartItemsMap().get(productName).getProductName());

            return new ResponseEntity<>(cartService.getCartItemsMap().get(productName).getProductName()
                    + " Added to cart", HttpStatus.OK);
        }
        else {

            logger.info("Update cart details for item: {}", productName);

            int currentQuantity = cartService.getCartItemsMap().get(productName).getQuantity();
            int additionalQuantity = item.getQuantity();
            int newQuantity = currentQuantity + additionalQuantity;

            //Update cart item quantity
            cartService.getCartItemsMap().get(productName).setQuantity(newQuantity);

            logger.info("Current Qty: {}, Additional Qty {}", currentQuantity, additionalQuantity);
            logger.info("Confirm new Qty updated in memory map: {}",
                    cartService.getCartItemsMap().get(productName).getQuantity());

            return new ResponseEntity<>(cartService.getCartItemsMap().get(productName).getProductName()
                    + " quantity updated", HttpStatus.OK);
        }
    }

    @GetMapping("/checkout")
    public ResponseEntity<Object>  checkout() {

        String responseMsg = cartService.calculateSubTotal();

        return new ResponseEntity<>(responseMsg, HttpStatus.OK);
    }


}