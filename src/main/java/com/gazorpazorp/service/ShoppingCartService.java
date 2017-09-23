package com.gazorpazorp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gazorpazorp.client.AccountClient;
import com.gazorpazorp.model.CartEvent;
import com.gazorpazorp.repository.CartEventRepository;

@Service
public class ShoppingCartService {

	@Autowired
	AccountClient accountClient;
	@Autowired
	CartEventRepository cartEventRepository;
	
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
		return accountClient.getCustomer().getId();
	}
}
