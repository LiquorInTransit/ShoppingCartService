package com.gazorpazorp.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gazorpazorp.client.AccountClient;
import com.gazorpazorp.client.ProductClient;
import com.gazorpazorp.model.CartEvent;
import com.gazorpazorp.model.CartEventType;
import com.gazorpazorp.model.Catalog;
import com.gazorpazorp.model.Product;
import com.gazorpazorp.model.ShoppingCart;
import com.gazorpazorp.repository.CartEventRepository;

import reactor.core.publisher.Flux;

@Service
public class ShoppingCartService {

	@Autowired
	AccountClient accountClient;
	@Autowired
	ProductClient productClient;
	@Autowired
	CartEventRepository cartEventRepository;
	
	@Transactional(readOnly = true)
	public ShoppingCart getShoppingCart() throws Exception {
		Long customerId = getAuthenticatedCustomerId();
		ShoppingCart shoppingCart = null;
		if (customerId != null) {
			shoppingCart = aggregateCartEvents(customerId);
			shoppingCart.getLineItems().forEach(li -> li.getProduct().Incorporate());
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
		Timestamp ts;
		if (ev == null)
			ts = new Timestamp(0);
		else
			ts = ev.getCreatedAt();
		Flux<CartEvent> cartEvents = Flux.fromStream(cartEventRepository.findByCustomerIdAndCreatedAtAfterOrderByCreatedAtAsc(customerId, ts));
		ShoppingCart shoppingCart = cartEvents
				.takeWhile(cartEvent -> !ShoppingCart.isTerminal(cartEvent.getCartEventType()))
				.reduceWith(() -> new ShoppingCart(), ShoppingCart::incorporate)
				.get();		
		
		Set<Product> products = new HashSet<>();
		if (!shoppingCart.getProductMap().isEmpty())
			products = productClient.getProductsById(shoppingCart.getProductMap().entrySet().stream().map(e ->e.getKey().toString()).collect(Collectors.joining(","))).getBody();
		
		 Catalog catalog = new Catalog(products);
		 shoppingCart.setCatalog(catalog);
		 		
		shoppingCart.getLineItems();
		
		return shoppingCart;
	}
}
