package com.gazorpazorp.client;

import java.util.Set;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.gazorpazorp.client.config.TokenRequestClientConfiguration;
import com.gazorpazorp.model.Product;

@FeignClient(name="product-and-store-service", configuration = TokenRequestClientConfiguration.class)
public interface ProductClient {
	@GetMapping(value="/internal/products/{id}", consumes = "application/json")
	ResponseEntity<Product> getProductById(@PathVariable("id")Long id);
	
	@GetMapping(value="/internal/products", consumes="application/json")
	ResponseEntity<Set<Product>> getProductsById(@RequestParam("productIds")String ids);
}
