package com.gazorpazorp.client;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gazorpazorp.model.Order;
import com.gazorpazorp.model.dto.OrderLineItem;

@FeignClient(name="order-service")
public interface OrderClient {
	
	@PostMapping("/api/orders")
	public Order createOrder(List<OrderLineItem> lineItems, @RequestParam("quote") Long quoteId);
}