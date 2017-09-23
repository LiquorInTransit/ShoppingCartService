package com.gazorpazorp.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.gazorpazorp.model.Customer;

@FeignClient(name="account-service")
public interface AccountClient {
	
	@GetMapping(value="/me", consumes = "application/json")
	Customer getCustomer();
}

