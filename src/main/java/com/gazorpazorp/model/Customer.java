package com.gazorpazorp.model;

public class Customer {
	private Long id;
	private String stripeId;
	
	public Long getId() {
		return id;
	}

	public String getStripeId() {
		return stripeId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setStripeId(String stripeId) {
		this.stripeId = stripeId;
	}
	
	
	
}
