package com.gazorpazorp.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gazorpazorp.client.AccountClient;
import com.gazorpazorp.client.InventoryClient;
import com.gazorpazorp.client.OrderClient;
import com.gazorpazorp.client.ProductClient;
import com.gazorpazorp.model.CartEvent;
import com.gazorpazorp.model.CartEventType;
import com.gazorpazorp.model.Catalog;
import com.gazorpazorp.model.CheckoutResult;
import com.gazorpazorp.model.Inventory;
import com.gazorpazorp.model.LineItem;
import com.gazorpazorp.model.Order;
import com.gazorpazorp.model.Product;
import com.gazorpazorp.model.ShoppingCart;
import com.gazorpazorp.model.dto.mapper.LineItemMapper;
import com.gazorpazorp.repository.CartEventRepository;

import reactor.core.publisher.Flux;

@Service
public class ShoppingCartService {

	@Autowired
	AccountClient accountClient;
	@Autowired
	ProductClient productClient;
	@Autowired
	OrderClient orderClient;
	@Autowired
	InventoryClient inventoryClient;
	@Autowired
	CartEventRepository cartEventRepository;
	
	Logger logger = LoggerFactory.getLogger(ShoppingCartService.class);
	
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
	
	@Transactional(readOnly = true)
	public CheckoutResult checkout(Long quoteId) throws Exception {
		CheckoutResult checkoutResult = new CheckoutResult();
		
		//Check available inventory
		ShoppingCart currentCart = null;
		try {
			currentCart = getShoppingCart();
		} catch (Exception e) {
			logger.error("Could not retrieve shopping cart", e);
		}
		
		if (currentCart != null) {
			List<Inventory> inventory = inventoryClient.getInventoryForProductIds(currentCart.getLineItems().stream().map(LineItem::getProductId).map(Object::toString).collect(Collectors.joining(",")), quoteId);
			
			if (!inventory.isEmpty()) {
				//Map is productId, inventoryCount
				Map<Long, Integer> inventoryItems = inventory.stream().collect(Collectors.toMap(Inventory::getProductId, Inventory::getCount));
				
				if (checkAvailableInventory(checkoutResult, currentCart, inventoryItems)) {
					//Create a new Order
					Order orderResponse = null;
					try {
						orderResponse = orderClient.createOrder(currentCart.getLineItems().stream().map(item -> LineItemMapper.INSTANCE.lineItemToOrderLineItem(item)).collect(Collectors.toList()), quoteId);
					} catch (Exception e) {
						checkoutResult.setResultMessage("User already has an active order");
						return checkoutResult;
					}
					if (orderResponse != null) {
						checkoutResult.setResultMessage("Order created");
						//Add Order Event (orders are not currently event sourced, so this step may be skipped)
						
						checkoutResult.setOrder(orderResponse);
					}
					addCartEvent(new CartEvent(CartEventType.CHECKOUT));
				}
			}
		}
		
		//return possible errors or message of successful order creation
		return checkoutResult;
	}
	
	
	public boolean checkAvailableInventory(CheckoutResult checkoutResult, ShoppingCart currentCart, Map<Long, Integer> inventoryItems) {
		boolean hasInventory = true;
		//determine if inventory is available
		try {
			List<LineItem> inventoryNotAvailable = currentCart.getLineItems().stream().filter(item -> inventoryItems.get(item.getProductId()) - item.getQty() < 0).collect(Collectors.toList());
			
			if (inventoryNotAvailable.size() > 0) {
				String productIdList = inventoryNotAvailable.stream().map(LineItem::getProductId).map(Object::toString).collect(Collectors.joining(","));
				checkoutResult.setResultMessage(String.format("Insufficient inventory available for %s. Lower the quantity of these products and try again.", productIdList));
				hasInventory = false;
			}
		} catch (Exception e) {
			logger.error("Error checking for available inventory");
		}
		return hasInventory;
	}
}