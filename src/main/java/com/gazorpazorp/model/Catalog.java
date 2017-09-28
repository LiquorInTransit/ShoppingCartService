package com.gazorpazorp.model;

import java.util.HashSet;
import java.util.Set;

public class Catalog {
	
	public Catalog() {}
	public Catalog(Set<Product> products) {
		this.products = products;
	}
	
	private Set<Product> products = new HashSet<>();

	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}
	
	
}
