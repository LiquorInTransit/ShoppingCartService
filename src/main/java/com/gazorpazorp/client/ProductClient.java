package com.gazorpazorp.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.gazorpazorp.model.Product;

@FeignClient(name="product-service")
public interface ProductClient {
	@GetMapping(value="/api/products/{id}", consumes = "application/json")
	ResponseEntity<Product> getProductById(@PathVariable("id")Long id);
}
