package com.gazorpazorp.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gazorpazorp.model.CartEvent;
import com.gazorpazorp.service.ShoppingCartService;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {
	
	@Autowired
	ShoppingCartService shoppingCartService;
	
	@PostMapping
	public ResponseEntity addCartEvent(@RequestBody CartEvent cartEvent) throws Exception {
		return Optional.ofNullable(shoppingCartService.addCartEvent(cartEvent))
				.map(event -> new ResponseEntity(HttpStatus.OK))
				.orElseThrow(() -> new Exception("Could not find shopping cart"));
	}
	
	@GetMapping
	public ResponseEntity getCart() throws Exception {
		return Optional.ofNullable(shoppingCartService.getShoppingCart())
				.map(cart -> new ResponseEntity<>(cart, HttpStatus.OK))
				.orElseThrow(() -> new Exception("Could not find shopping cart"));
	}
	
	@PostMapping("/checkout")
	public ResponseEntity checkout(@RequestParam("quote") Long quoteId) throws Exception {
		return Optional.ofNullable(shoppingCartService.checkout(quoteId))
				.map(checkoutResult -> new ResponseEntity<>(checkoutResult, HttpStatus.OK))
				.orElseThrow(() -> new Exception("Could not checkout"));
	}
	

	
	
}
