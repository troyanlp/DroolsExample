package com.everis.drools.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Order {

	private Customer customer;
	private List<Product> products = new ArrayList<Product>();
	private double totalPrice;
	private boolean decisionTable = false;
	
	public Order(Customer customer, boolean decisionTable) {
		super();
		this.customer = customer;
		this.decisionTable = decisionTable;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void addProduct(Product product1) {
		products.add(product1);
	}
	
	public void calculateTotalPrice() {
		products.forEach(k-> totalPrice += k.getPrice() );
	}

	public boolean isDecisionTable() {
		return decisionTable;
	}

	public void setDecisionTable(boolean decisionTable) {
		this.decisionTable = decisionTable;
	}
	
}
