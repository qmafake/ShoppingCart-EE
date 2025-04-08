
package com.equalexperts.shoppingcart;

import com.equalexperts.shoppingcart.controller.CartController;
import com.equalexperts.shoppingcart.model.CartItem;
import com.equalexperts.shoppingcart.model.ProductPrice;
import com.equalexperts.shoppingcart.service.CartService;
import com.equalexperts.shoppingcart.service.ProductPriceClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartControllerTest {

	@InjectMocks
	private CartController cartController;

	@Mock
	private CartService cartService;

	@Mock
	private ProductPriceClientService productPriceClientService;

	private CartItem cartItem;
	private ProductPrice productPrice;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Initialize mocks

		cartItem = new CartItem();
		cartItem.setProductName("Test Item");
		cartItem.setQuantity(1);

		productPrice = new ProductPrice();
		productPrice.setPrice(BigDecimal.valueOf(100.0));  // Assuming the price is 100 for this test
	}

	@Test
	void testGetAllItems() {
		// Arrange
		Map<String, CartItem> mockItems = new HashMap<>();
		mockItems.put(cartItem.getProductName(), cartItem);
		when(cartService.getCartItemsMap()).thenReturn(mockItems);

		// Act
		var items = cartController.getAllItems();

		// Assert
		assertNotNull(items);
		assertTrue(items.contains(cartItem));
		verify(cartService, times(1)).getCartItemsMap();
	}

	@Test
	void testAddItem_New() {
		// Arrange
		when(productPriceClientService.fetchItemPrice(cartItem.getProductName())).thenReturn(productPrice);
		when(cartService.getCartItemsMap()).thenReturn(Collections.emptyMap());

		// Act
		ResponseEntity<Object> response = cartController.add(cartItem);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("Added to cart"));
		verify(cartService, times(1)).getCartItemsMap();
		verify(cartService, times(1)).getCartItemsMap().put(cartItem.getProductName(), cartItem);
		verify(productPriceClientService, times(1)).fetchItemPrice(cartItem.getProductName());
	}

	@Test
	void testAddItem_Existing() {
		// Arrange
		CartItem existingItem = new CartItem();
		existingItem.setProductName("Test Item");
		existingItem.setQuantity(2);
		when(cartService.getCartItemsMap()).thenReturn(Collections.singletonMap("Test Item", existingItem));
		when(productPriceClientService.fetchItemPrice(cartItem.getProductName())).thenReturn(productPrice);

		// Act
		ResponseEntity<Object> response = cartController.add(cartItem);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("quantity updated"));
		assertEquals(3, existingItem.getQuantity());  // Quantity should be updated to 3
		verify(cartService, times(1)).getCartItemsMap();
		verify(cartService, times(1)).getCartItemsMap().put(cartItem.getProductName(), existingItem);
		verify(productPriceClientService, times(1)).fetchItemPrice(cartItem.getProductName());
	}

	@Test
	void testCheckout() {
		// Arrange
		String expectedResponse = "Subtotal: 100.0";
		when(cartService.checkoutCart()).thenReturn(expectedResponse);

		// Act
		ResponseEntity<Object> response = cartController.checkout();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedResponse, response.getBody());
		verify(cartService, times(1)).checkoutCart();
	}
}

