package com.everis.drools.entity;

public class Customer {

	public static final int DEFAULT_CUSTOMER = 0;
	public static final int SILVER_CUSTOMER = 1;
	public static final int GOLD_CUSTOMER = 2;
	
	private int status;
	private String name;
	
	public Customer(int status, String name) {
		super();
		this.status = status;
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
