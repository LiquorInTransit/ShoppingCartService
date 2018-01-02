package com.gazorpazorp.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gazorpazorp.client.config.TokenRequestClientConfiguration;

@FeignClient(name="payment-service", configuration = TokenRequestClientConfiguration.class)
public interface PaymentClient {

	@PostMapping("/api/payments/processPayment")
	public ResponseEntity processPayment(@RequestParam String customerId, @RequestParam Long orderId, @RequestParam Integer amount);
}
