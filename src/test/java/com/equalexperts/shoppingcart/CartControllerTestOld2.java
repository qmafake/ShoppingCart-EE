package com.equalexperts.shoppingcart;

import com.equalexperts.shoppingcart.controller.CartController;
import com.equalexperts.shoppingcart.model.CartItem;
import com.equalexperts.shoppingcart.model.ProductPrice;
import com.equalexperts.shoppingcart.service.CartService;
import com.equalexperts.shoppingcart.service.ProductPriceClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTestOld2 {

	@Mock
	private CartService cartService;

	@Mock
	private ProductPriceClientService productPriceClientService;

	@InjectMocks
	private CartController cartController;

	private CartItem testCartItem;

	private ProductPrice testProductPrice;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@BeforeEach
	void setUp() {
		testCartItem = new CartItem("Test Product", 2);
		testProductPrice = new ProductPrice();
		testProductPrice.setProductName("Test Product");
		testProductPrice.setPrice(BigDecimal.valueOf(10.99));

		testCartItem.setUnitPrice(testProductPrice.getPrice());
	}

	@Test
	void getAllItems_ShouldReturnAllCartItems() {
		// Arrange
		Map<String, CartItem> mockItems = new HashMap<>();
		mockItems.put("Product1", new CartItem("Product1", 2));
		mockItems.put("Product2", new CartItem("Product2", 1));
		when(cartService.getCartItemsMap()).thenReturn(mockItems);

		// Act
		Collection<CartItem> result = cartController.getAllItems();

		// Assert
		assertEquals(2, result.size());
		verify(cartService, times(1)).getCartItemsMap();
	}

	@Test
	void addItem_ShouldAddNewItemToCart() {
		// Arrange
		when(cartService.getCartItemsMap()).thenReturn(new HashMap<>());
		when(productPriceClientService.fetchItemPrice("Test Product")).thenReturn(testProductPrice);

		// Act

		ProductPrice productPrice = productPriceClientService.fetchItemPrice("cornflakes");

		logger.info("Product {}, price {}", productPrice.getProductName(), productPrice.getPrice());


		ResponseEntity<Object> response = cartController.add(testCartItem);

		logger.info("Map values - {}", cartService.getCartItemsMap());

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Test Product added to cart", response.getBody());
		verify(cartService, times(2)).getCartItemsMap();
		verify(productPriceClientService, times(1)).fetchItemPrice("Test Product");
	}

	@Test
	void addItem_ShouldUpdateExistingItem() {
		// Arrange
		Map<String, CartItem> mockItems = new HashMap<>();
		mockItems.put("Test Product", testCartItem);
		when(cartService.getCartItemsMap()).thenReturn(mockItems);
		when(productPriceClientService.fetchItemPrice("Test Product")).thenReturn(testProductPrice);

		// Act
		ResponseEntity<Object> response = cartController.add(testCartItem);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Test Product quantity updated", response.getBody());
		verify(cartService, times(1)).updateCart(testCartItem);
	}

	@Test
	void checkout_ShouldProcessCart() {
		// Arrange
		String expectedMessage = "Checkout message";

		when(cartService.getCheckoutMsg()).thenReturn(expectedMessage);

		// Act
		ResponseEntity<Object> response = cartController.checkout();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedMessage, response.getBody());
		verify(cartService, times(1)).checkoutCart();
	}

	@Test
	void removeItem_ShouldRemoveItemFromCart() {
		// Arrange
		doNothing().when(cartService).deleteCartItem("Test Product");

		// Act
		ResponseEntity<Object> response = cartController.remove(testCartItem);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Test Product removed successfully", response.getBody());
		verify(cartService, times(1)).deleteCartItem("Test Product");
	}
}