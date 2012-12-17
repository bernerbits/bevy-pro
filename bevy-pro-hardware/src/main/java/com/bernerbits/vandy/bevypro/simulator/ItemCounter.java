package com.bernerbits.vandy.bevypro.simulator;

/**
 * Basic item counter class. Useful to simulate both soda and coin slots.
 * 
 * @author derekberner
 */
public class ItemCounter {

	private int count; 
	
	public ItemCounter() { 
		this.count = 0;
	}
	
	public void set(int count) {
		this.count = count;
	}

	public int get() {
		return count;
	}
	
	public void addOne() {
		this.count ++;
	}
	
	public boolean takeOne() {
		if(this.count == 0) {
			return false;
		}
		this.count --;
		return true;
	}
}
