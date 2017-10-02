package com.gazorpazorp.model;

import java.util.Set;

import com.gazorpazorp.model.dto.OrderLineItem;

public class Order {
	private Long id;
	private Long customerId;		

	private double total;
	private String status;
	
	private Set<OrderLineItem> items;
	
	public Order() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Set<OrderLineItem> getItems() {
		return items;
	}

	public void setItems(Set<OrderLineItem> items) {
		this.items = items;
	}
	
	
}
