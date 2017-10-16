package com.gazorpazorp.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Product implements Serializable{
	
	private Long id;
	private String name, producerName, imageThumbUrl, packageType;
	private double price;
	
	public Product() {}
	
	
	public Product(Long id, String name/*, String description*/, double price) {
		this.id = id;
		this.name = name;
	//	this.description = description;
		this.price = price;
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
//	public String getDescription() {
//		return description;
//	}
//	public void setDescription(String description) {
//		this.description = description;
//	}
	
	@JsonProperty("producer_name")
	public String getProducerName() {
		return producerName;
	}
	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}

	@JsonProperty("image_thumb_url")
	public String getImageThumbUrl() {
		return imageThumbUrl;
	}
	public void setImageThumbUrl(String imageThumbUrl) {
		this.imageThumbUrl = imageThumbUrl;
	}

	@JsonProperty("package")
	public String getPackageType() {
		return packageType;
	}
	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}


	@JsonAlias("price_in_cents")
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	public void Incorporate() {
		System.out.println("HERES THE BEFORE PRICE: " + this.price);
		this.price = this.price/100.0;
	}


	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name  + ", priceInCents="
				+ price + "]";
	}
	
	
}
