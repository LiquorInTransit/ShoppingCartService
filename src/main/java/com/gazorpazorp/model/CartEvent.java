package com.gazorpazorp.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name="CART_EVENT", indexes = {@Index(name = "IDX_CART_EVENT_CUSTOMER", columnList = "id,customer_Id")})
public class CartEvent implements Serializable{

	private Long id;
	private Long customerId;
	
	@Enumerated(EnumType.STRING)
	private CartEventType cartEventType;
	
	private Long productId;
	private Integer qty;
	
	@LastModifiedDate
	private Date lastModified;
	@CreatedDate
	private Date createdAt;
	
	public CartEvent() {}


	
	@Id
	@GenericGenerator(name = "incrementGenerator", strategy = "org.hibernate.id.IncrementGenerator")
	@GeneratedValue(generator="incrementGenerator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="customer_id")
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	@Column(name="cart_event_type")
	public CartEventType getCartEventType() {
		return cartEventType;
	}
	public void setCartEventType(CartEventType cartEventType) {
		this.cartEventType = cartEventType;
	}


	@Column(name="product_id")
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}


	@Column(name="qty")
	public Integer getQty() {
		return qty;
	}
	public void setQty(Integer qty) {
		this.qty = qty;
	}

	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public Date getLastModified() {
        return lastModified;
    }
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    @Column(name="created_at", columnDefinition="BIGINT")
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }



	@Override
	public String toString() {
		return "CartEvent [id=" + id + ", customerId=" + customerId + ", cartEventType=" + cartEventType
				+ ", productId=" + productId + ", qty=" + qty + ", lastModified=" + lastModified + ", createdAt="
				+ createdAt + "]";
	}
    
    
	
}
