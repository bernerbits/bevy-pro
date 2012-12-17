package com.bernerbits.vandy.bevypro.controller.beans;

/**
 * Simple value object representing a currency type. 
 * 
 * @author derekberner
 */
public class Currency implements Comparable<Currency> {
	/**
	 * The hardware-dependent currency type (retrieved from HardwareService)
	 */
	private int type;
	
	/**
	 * Monetary value of the hardware-dependent currency type (retrieved from HardwareService)
	 */
	private int value;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	public Currency(int type, int value) {
		this.type = type;
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + type;
		result = prime * result + value;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Currency other = (Currency) obj;
		if (type != other.type)
			return false;
		if (value != other.value)
			return false;
		return true;
	}
	
	@Override
	public int compareTo(Currency o) {
		return this.getValue() - o.getValue();
	}
}
