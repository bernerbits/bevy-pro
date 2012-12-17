package com.bernerbits.vandy.bevypro.controller.beans;

public class Beverage {
	private String name;
	private int unitPrice;
	
	public Beverage() {
	}
	
	public Beverage(String name, int unitPrice) {
		this.name = name;
		this.unitPrice = unitPrice;
	}
	
	public String getName() {
		return name;
	}
	public int getUnitPrice() {
		return unitPrice;
	}
}
