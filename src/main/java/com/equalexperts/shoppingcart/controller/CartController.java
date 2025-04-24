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
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    private final ProductPriceClientService productPriceClientService;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public CartController(CartService cartService, ProductPriceClientService productPriceClientService) {
        this.cartService = cartService;
        this.productPriceClientService = productPriceClientService;
    }

    @GetMapping("/view")
    public Collection<CartItem> getAllItems() {

        return cartService.getCartItemsMap().values();
    }

    @PostMapping("/add")
    public ResponseEntity<Object> add(@RequestBody CartItem cartItem) {

        String productName = cartItem.getProductName();

        ProductPrice productPrice =  productPriceClientService.fetchItemPrice(productName);

        cartItem.setUnitPrice(productPrice.getPrice());

        Map<String, CartItem> cartItemsMap = cartService.getCartItemsMap();

        if (!cartItemsMap.containsKey(productName)){

            cartItemsMap.put(productName, cartItem);

            logger.info("Item added to cart: {}", cartItemsMap.get(productName).getProductName());

            return new ResponseEntity<>(cartItemsMap.get(productName).getProductName()
                    + " added to cart", HttpStatus.OK);
        }
        else {

            logger.info("Updating cart details for item: {}", productName);

            cartService.updateCart(cartItem);

            return new ResponseEntity<>(cartItemsMap.get(productName).getProductName()
                    + " quantity updated", HttpStatus.OK);
        }
    }

    @GetMapping("/checkout")
    public ResponseEntity<Object>  checkout() {

        cartService.checkoutCart();

        return new ResponseEntity<>(cartService.getCheckoutMsg(), HttpStatus.OK);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Object> remove(@RequestBody CartItem item) {

        String productName = item.getProductName();

        logger.info("Removing cart item: {}", productName);

        cartService.deleteCartItem(productName);

        return new ResponseEntity<>(productName + " removed successfully", HttpStatus.OK);
    }
}