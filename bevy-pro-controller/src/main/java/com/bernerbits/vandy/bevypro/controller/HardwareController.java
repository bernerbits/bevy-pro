package com.bernerbits.vandy.bevypro.controller;

import java.util.List;

import com.bernerbits.vandy.bevypro.listener.ModelUpdateListener;
import com.bernerbits.vandy.bevypro.model.Beverage;

public interface HardwareController {

	/**
	 * Modifies fields on beverages and filters them out if necessary. 
	 * 
	 * @param beverages
	 * @return
	 */
	List<Beverage> check(List<Beverage> beverages);

	/**
	 * @return The current credit held by the machine. 
	 */
	int getCredit();

	/**
	 * Modifies fields on beverage. 
	 * 
	 * @param beverage
	 * @return
	 */
	Beverage check(Beverage beverage);

	/**
	 * Dispenses a beverage.
	 * 
	 * @param beverage
	 */
	void dispense(Beverage beverage);

	/**
	 * Refunds the deposited credit.
	 * 
	 * @param beverage
	 */
	void refund();

	/** 
	 * Register a listener for when the hardware's state is updated.
	 * 
	 * @param modelUpdateListener
	 */
	void registerModelUpdateListener(ModelUpdateListener modelUpdateListener);

}
