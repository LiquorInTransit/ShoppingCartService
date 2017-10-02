package com.gazorpazorp.client;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gazorpazorp.client.config.TokenRequestClientConfiguration;
import com.gazorpazorp.model.Inventory;

@FeignClient(name="inventory-service", configuration = TokenRequestClientConfiguration.class)
public interface InventoryClient {

	@GetMapping("api/inventory")
	public List<Inventory> getInventoryForProductIds(@RequestParam("productIds") String productIds, @RequestParam("quote") Long quoteId);
}
