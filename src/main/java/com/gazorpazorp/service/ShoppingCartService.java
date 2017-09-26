package com.gazorpazorp.service;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gazorpazorp.client.AccountClient;
import com.gazorpazorp.model.CartEvent;
import com.gazorpazorp.model.CartEventType;
import com.gazorpazorp.model.ShoppingCart;
import com.gazorpazorp.repository.CartEventRepository;

import reactor.core.publisher.Flux;

@Service
public class ShoppingCartService {

	@Autowired
	AccountClient accountClient;
	@Autowired
	CartEventRepository cartEventRepository;
	
	@Transactional(readOnly = true)
	public ShoppingCart getShoppingCart() throws Exception {
		Long customerId = getAuthenticatedCustomerId();
		ShoppingCart shoppingCart = null;
		if (customerId != null) {
			shoppingCart = aggregateCartEvents(customerId);
		}
		return shoppingCart;
	}
	
	//TODO: Add check that productId belongs to a real product.
	public Boolean addCartEvent(CartEvent cartEvent) {
		Long customerId = getAuthenticatedCustomerId();
		if (customerId != null) {
			cartEvent.setCustomerId(customerId);
			System.out.println(cartEventRepository.save(cartEvent));
		} else {
			return null;
		}
		return true;
	}
	
	private Long getAuthenticatedCustomerId() {
		return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());//accountClient.getCustomer().getId();
	}
	
	@Transactional(readOnly = true)
	public ShoppingCart aggregateCartEvents(Long customerId) throws Exception {
		CartEvent ev = cartEventRepository.findTopByCustomerIdAndCartEventTypeInOrderByCreatedAtDesc(customerId, Arrays.asList(CartEventType.CLEAR_CART, CartEventType.CHECKOUT));
				
		Flux<CartEvent> cartEvents = Flux.fromStream(cartEventRepository.findByCustomerIdAndCreatedAtAfterOrderByCreatedAtAsc(customerId, ev.getCreatedAt()));
		ShoppingCart shoppingCart = cartEvents
				.takeWhile(cartEvent -> !ShoppingCart.isTerminal(cartEvent.getCartEventType()))
				.reduceWith(() -> new ShoppingCart(), ShoppingCart::incorporate)
				.get();
		/*Flux<CartEvent> cartEvents = Flux.fromStream(cartEventRepository.getCartEventStreamByCustomer(customerId));
		ShoppingCart shoppingCart = cartEvents
				.takeWhile(cartEvent -> !ShoppingCart.isTerminal(cartEvent.getCartEventType()))
				.reduceWith(() -> new ShoppingCart(), ShoppingCart::incorporate)
				.get();*/
		
		shoppingCart.getLineItems();
		
		return shoppingCart;
	}
}
