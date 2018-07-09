package com.everis.drools.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order {

	private Customer customer;
	private List<Product> products = new ArrayList<Product>();
	private double totalPrice;
	private boolean decisionTable = false;
	private static LocalDate date;

	public Order() {

	}

	public Order(Customer customer, boolean decisionTable) {
		super();
		this.customer = customer;
		this.decisionTable = decisionTable;
		this.date = LocalDate.now();
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
		products.forEach(k -> totalPrice += k.getPrice());
	}

	public boolean isDecisionTable() {
		return decisionTable;
	}

	public void setDecisionTable(boolean decisionTable) {
		this.decisionTable = decisionTable;
	}

	public static boolean isComparedToDate(String dateTable, boolean greaterThan) {
		boolean result = true;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localdate = LocalDate.parse(dateTable, formatter);

		if (greaterThan) {
			if (date.isAfter(localdate) || date.isEqual(localdate))
				result = true;
			else
				result = false;
		} else {
			if (date.isBefore(localdate))
				result = true;
			else
				result = false;
		}

		return result;
	}

	public static LocalDate getDate() {
		return date;
	}

	public static void setDate(LocalDate date) {
		Order.date = date;
	}

}
