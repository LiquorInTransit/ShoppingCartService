package com.gazorpazorp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;

public class ShoppingCart {

	private List<LineItem> lineItems = new ArrayList<>();
	
	//This map has an key of productIds, with values of quantities in the cart
	private Map<Long, Integer> productMap = new HashMap<>();
	
	public List<LineItem> getLineItems() throws Exception {
		lineItems = productMap.entrySet()
				.stream()							    //TODO: get the actual product from the catalog/product-service
				.map(item -> new LineItem(item.getKey(), new Product(item.getKey(), "booze", "booze", 12.99), item.getValue()))
				.filter(item -> item.getQty() > 0)
				.collect(Collectors.toList());
		if (lineItems.stream().anyMatch(item->item.getProduct()==null)) {
			throw new Exception ("Product not found in catalog");
		}
		return lineItems;
	}
	
	public ShoppingCart incorporate(CartEvent cartEvent) {
		Flux<CartEventType> validCartEventTypes = Flux.fromStream(Stream.of(CartEventType.ADD_ITEM, CartEventType.REMOVE_ITEM));
		if (validCartEventTypes.exists(cartEventType -> cartEvent.getCartEventType().equals(cartEventType)).get()) {
			productMap.put(cartEvent.getProductId(), 
					productMap.getOrDefault(cartEvent.getProductId(), 0) + 
					(cartEvent.getQty() * (cartEvent.getCartEventType()
							.equals(CartEventType.ADD_ITEM)?1:-1)));
		}
		return this;
	}
	
	 public static Boolean isTerminal(CartEventType eventType) {
		 return (eventType == CartEventType.CLEAR_CART || eventType == CartEventType.CHECKOUT);
	 }
}
