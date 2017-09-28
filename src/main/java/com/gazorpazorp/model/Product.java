package com.gazorpazorp.model;

import java.io.Serializable;

public class Product implements Serializable{
	
	private Long id;
	private String name, description;
	private double priceInCents;
	
	public Product() {}
	
	
	public Product(Long id, String name, String description, double price) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.priceInCents = price;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getPrice() {
		return priceInCents;
	}
	public void setPrice(double price) {
		this.priceInCents = price;
	}


	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", description=" + description + ", priceInCents="
				+ priceInCents + "]";
	}
	
	
}
