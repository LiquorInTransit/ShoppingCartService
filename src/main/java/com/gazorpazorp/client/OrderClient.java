package com.gazorpazorp.client;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gazorpazorp.client.config.TokenRequestClientConfiguration;
import com.gazorpazorp.model.Customer;
import com.gazorpazorp.model.LineItem;
import com.gazorpazorp.model.Order;

@FeignClient(name="order-and-delivery-service", configuration = TokenRequestClientConfiguration.class)
public interface OrderClient {
	
	@PostMapping("/api/orders")
	public ResponseEntity<Order> createOrder(List<LineItem> lineItems, @RequestParam("quote") Long quoteId, @RequestParam("customerId") Long customerId);
}
