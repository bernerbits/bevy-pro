package com.bernerbits.vandy.bevypro.model;

import java.util.List;

public class BeverageMachineModel {
	private List<Beverage> bevs;
	private int credit;
	private String creditString;
	private String message;
	
	public BeverageMachineModel() {
	}

	public BeverageMachineModel(List<Beverage> bevs, int credit, String creditString, String message) {
		this.bevs = bevs;
		this.credit = credit;
		this.creditString = creditString;
		this.message = message;
	}

	public List<Beverage> getBevs() {
		return bevs;
	}

	public void setBevs(List<Beverage> bevs) {
		this.bevs = bevs;
	}

	public int getCredit() {
		return credit;
	}
	
	public void setCredit(int credit) {
		this.credit = credit;
	}
	
	public String getCreditString() {
		return creditString;
	}

	public void setCreditString(String creditString) {
		this.creditString = creditString;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
