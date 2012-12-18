package com.bernerbits.vandy.bevypro.model;

public class Beverage {
	private String id;
	private String imageUrl;
	private String name;
	private int unitPrice;
	private int slot;
	private boolean soldOut;
	
	public Beverage() {
	}

	public Beverage(String id, String imageUrl, String name, int unitPrice, int slot, boolean soldOut) {
		this.id = id;
		this.imageUrl = imageUrl;
		this.name = name;
		this.unitPrice = unitPrice;
		this.slot = slot;
		this.soldOut = soldOut;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(int unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public int getSlot() {
		return slot;
	}
	
	public void setSlot(int slot) {
		this.slot = slot;
	}
	
	public boolean isSoldOut() {
		return soldOut;
	}
	
	public void setSoldOut(boolean soldOut) {
		this.soldOut = soldOut;
	}
}
