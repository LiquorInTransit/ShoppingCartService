package com.gazorpazorp.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gazorpazorp.client.AccountClient;
import com.gazorpazorp.client.InventoryClient;
import com.gazorpazorp.client.OrderClient;
import com.gazorpazorp.client.PaymentClient;
import com.gazorpazorp.client.ProductClient;
import com.gazorpazorp.model.CartEvent;
import com.gazorpazorp.model.CartEventType;
import com.gazorpazorp.model.Catalog;
import com.gazorpazorp.model.CheckoutResult;
import com.gazorpazorp.model.Customer;
import com.gazorpazorp.model.Inventory;
import com.gazorpazorp.model.LineItem;
import com.gazorpazorp.model.Order;
import com.gazorpazorp.model.Product;
import com.gazorpazorp.model.ShoppingCart;
import com.gazorpazorp.repository.CartEventRepository;
import com.stripe.Stripe;

import reactor.core.publisher.Flux;

@Service
@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS, value="request")
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
	PaymentClient paymentClient;
	@Autowired
	CartEventRepository cartEventRepository;
	
	Logger logger = LoggerFactory.getLogger(ShoppingCartService.class);
	
	@Value("${stripe.secret-key}")
	String secretKey;
	
	private Customer customer;
	
	@Transactional(readOnly = true)
	public ShoppingCart getShoppingCart() throws Exception {
		getAuthenticatedCustomer();
		ShoppingCart shoppingCart = null;
		if (customer.getId() != null) {
			shoppingCart = aggregateCartEvents(customer.getId());
		//	shoppingCart.getLineItems().forEach(li -> li.getProduct().Incorporate());
		}
		return shoppingCart;
	}
	
	@Transactional(readOnly = true)
	public Integer getCount() throws Exception {
		ShoppingCart cart = getShoppingCart();
		return cart.getLineItems().stream().mapToInt(LineItem::getQty).sum();
	}
	
	//TODO: Add check that productId belongs to a real product.
	public Boolean addCartEvent(CartEvent cartEvent) {
		getAuthenticatedCustomer();
		logger.info("Add cart Customer ID: " + customer.getId());
		if (customer.getId() != null) {
			cartEvent.setCustomerId(customer.getId());
			logger.warn("Here's the event just added to the DB: " + cartEventRepository.saveAndFlush(cartEvent));
		} else {
			return null;
		}
		return true;
	}
	
	private void getAuthenticatedCustomer() {
		Customer customer = accountClient.getCustomer();
		this.customer = customer;
		//return /*Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());*/accountClient.getCustomer().getId();
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
		 		
		shoppingCart.getLineItems().forEach(li -> li.getProduct().Incorporate());
		return shoppingCart;
	}
	
	@Transactional
	public CheckoutResult checkout(Long quoteId) throws Exception {
		CheckoutResult checkoutResult = new CheckoutResult();
		
		//Check available inventory
		ShoppingCart currentCart = null;
		try {
			currentCart = getShoppingCart();
		} catch (Exception e) {
			logger.error("Could not retrieve shopping cart", e);
		}
		
		//Check if the customer has a payment method
		Stripe.apiKey=secretKey;
		com.stripe.model.Customer cust = com.stripe.model.Customer.retrieve(customer.getStripeId());
		if (cust.getDefaultSource()==null) {
			checkoutResult.setResultMessage("No Payment Method");
			checkoutResult.setStatus(HttpStatus.CONFLICT.value()); //409
			return checkoutResult;
		}
		
		if (currentCart != null) {
			if (currentCart.getLineItems().isEmpty()) {
				checkoutResult.setResultMessage("Your cart is empty. Add items to cart before checking out.");
				checkoutResult.setStatus(HttpStatus.NO_CONTENT.value());
				return checkoutResult;
			}
			List<Inventory> inventory = inventoryClient.getInventoryForProductIds(currentCart.getLineItems().stream().map(LineItem::getProductId).map(Object::toString).collect(Collectors.joining(",")), quoteId);
			
			if (!inventory.isEmpty()) {
				//Map is productId, inventoryCount
				Map<Long, Integer> inventoryItems = inventory.stream().collect(Collectors.toMap(Inventory::getProductId, Inventory::getCount));
				
				if (checkAvailableInventory(checkoutResult, currentCart, inventoryItems)) {
					//Create a new OrderResponse
					Order orderResponse = null;
					
					//make the order (this also processes the payment)
					try {
						ResponseEntity<Order> orderEntity = orderClient.createOrder(currentCart.getLineItems(), quoteId, this.customer.getId());
						if (orderEntity.getStatusCode() == HttpStatus.OK) {
							//Continue with returning the order
							orderResponse = orderEntity.getBody();
							checkoutResult.appendResultMessage("Order created");
							checkoutResult.setOrder(orderResponse);
							checkoutResult.setStatus(HttpStatus.OK.value());
							logger.warn("Added checkout cart event: " + addCartEvent(new CartEvent(CartEventType.CHECKOUT)).toString());
						} else {
							//Don't continue
							checkoutResult.setResultMessage("Error Creating Order");
							checkoutResult.setStatus(orderEntity.getStatusCodeValue());
						}
					} catch (Exception e) {
						checkoutResult.setResultMessage("User already has an active order");
						e.printStackTrace();
					}
					/*if (orderResponse != null) {
						//Take the customers money
						HttpStatus paymentStatus = paymentClient.processPayment(customer.getStripeId(), orderResponse.getId(), Integer.valueOf((int) (orderResponse.getTotal()*100))).getStatusCode();
						checkoutResult.setStatus(paymentStatus.value());
						if (paymentStatus != HttpStatus.OK) {
							//cancel the order	
							
						} else {
							
							//Add Order Event (orders are not currently event sourced, so this step may be skipped)
							
							
						}
					}
					logger.warn("Added checkout cart event: " + addCartEvent(new CartEvent(CartEventType.CHECKOUT)).toString());*/
				}
			}
		}
		
		//return possible errors or message of successful order creation
		return checkoutResult;
	}
	
	
	public boolean checkAvailableInventory(CheckoutResult checkoutResult, ShoppingCart currentCart, Map<Long, Integer> inventoryItems) {
		boolean hasInventory = true;
		//First, check to make sure the inventory-service could actually contact the LCBO API (inventory will receive -1 for count if it couldn't contact the LCBO API)
		//If information is not available, warn the user and go ahead with the order.
		if (inventoryItems.entrySet().stream().filter(ii -> ii.getValue().equals(-1)).collect(Collectors.toList()).size()>0) {
			inventoryItems.entrySet().forEach(System.out::println);
			checkoutResult.setResultMessage("Inventory unavailable. Your driver will contact you if items are not in stock");
			return true;
		}
		//determine if inventory is available
		try {
			List<LineItem> inventoryNotAvailable = currentCart.getLineItems().stream().filter(item -> inventoryItems.get(item.getProductId()) - item.getQty() < 0).collect(Collectors.toList());
			
			if (inventoryNotAvailable.size() > 0) {
				String productIdList = inventoryNotAvailable.stream().map(LineItem::getProduct).map(Product::getName).collect(Collectors.joining(", "));
				checkoutResult.setResultMessage(String.format("Insufficient inventory available for %s. Lower the quantity of these products and try again.", productIdList));
				checkoutResult.setStatus(HttpStatus.NOT_FOUND.value());
				hasInventory = false;
			}
		} catch (Exception e) {
			logger.error("Error checking for available inventory");
			return false;
		}
		return hasInventory;
	}
}