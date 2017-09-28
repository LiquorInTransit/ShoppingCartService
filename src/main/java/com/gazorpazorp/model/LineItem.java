package com.gazorpazorp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LineItem {
	@JsonIgnore
	private Long productId;
	private Product product;
	private int qty;
	
	public LineItem (Long productId, Product product, int qty) {
		this.productId = productId;
		this.product = product;
		this.qty = qty;
	}
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	
	
}
