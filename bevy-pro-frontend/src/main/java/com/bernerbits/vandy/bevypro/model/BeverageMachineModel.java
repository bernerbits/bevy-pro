package com.bernerbits.vandy.bevypro.model;

import java.util.List;

public class BeverageMachineModel {
	private List<Beverage> bevs;
	private int credit;
	private String message;
	
	public BeverageMachineModel() {
	}

	public BeverageMachineModel(List<Beverage> bevs, int credit, String message) {
		this.bevs = bevs;
		this.credit = credit;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
