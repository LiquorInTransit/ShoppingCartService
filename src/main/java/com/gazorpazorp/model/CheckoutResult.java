package com.gazorpazorp.model;

import org.springframework.http.HttpStatus;

public class CheckoutResult {
	private String resultMessage;
	private Order order;
	Integer status;
	
	public CheckoutResult() {}
	public CheckoutResult(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public CheckoutResult(String resultMessage, Order order) {
		this.resultMessage = resultMessage;
		this.order = order;
	}
	

	public String getResultMessage() {
		return resultMessage;
	}
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public void appendResultMessage(String resultMessage) {
		if (this.resultMessage == null)
			this.resultMessage = new String(resultMessage);
		else
			this.resultMessage += (this.resultMessage.isEmpty()?"":". ") + resultMessage;
	}
	
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	
	
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "CheckoutResult [resultMessage=" + resultMessage + "]";
	}
	
	
}
