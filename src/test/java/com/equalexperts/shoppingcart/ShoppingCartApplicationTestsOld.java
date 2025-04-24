package com.equalexperts.shoppingcart;

import com.equalexperts.shoppingcart.controller.CartController;
import com.equalexperts.shoppingcart.model.CartItem;
import com.equalexperts.shoppingcart.service.CartService;
import com.equalexperts.shoppingcart.service.ProductPriceClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ShoppingCartApplicationTestsOld {

	@Autowired
	CartService cartService; //TODO - try mockito
    @Autowired
    ProductPriceClientService productPriceClientService;

	@InjectMocks
	private CartController cartController; //TODO - try mockito

	protected Logger logger = LoggerFactory.getLogger(this.getClass());


	@Test
	void contextLoads() {
	}

	@BeforeEach
	void loadCart(){

		CartItem cartItem = new CartItem();
		cartItem.setProductName("rice");
		cartItem.setQuantity(2);
		cartItem.setUnitPrice(BigDecimal.valueOf(6.65));
		cartService.getCartItemsMap().put("rice", cartItem);
		cartController.add(cartItem); //TODO: null issue on productPriceClientService

		CartItem cartItem1 = new CartItem();
		cartItem1.setProductName("salt");
		cartItem1.setQuantity(1);
		cartItem1.setUnitPrice(BigDecimal.valueOf(2.25));
		cartService.getCartItemsMap().put("salt", cartItem1);
//		cartController.add(cartItem1);
	}

	@Test
	public void getCartItemsMap_ShouldReturnAllCartItems() {

		logger.info("Hi");
		logger.info("Size: {}", cartService.getCartItemsMap().size());

		Map<String, CartItem> cartItemsMap = cartService.getCartItemsMap();

        assertEquals(cartItemsMap.size() ,2 /*result.size()*/);
	}

	@Test
	public void deleteCartItem_ShouldReturnCurrentQtyMinusOne() {

		int currentQty = cartService.getCartItemsMap().get("rice").getQuantity();

		logger.info("Current quantity = {}", currentQty);

		cartService.deleteCartItem("rice");

		int updatedQty = cartService.getCartItemsMap().get("rice").getQuantity();

		logger.info("Updated quantity = {}", updatedQty);

		assertEquals(updatedQty , currentQty - 1);
	}

}
