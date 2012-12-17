package com.bernerbits.vandy.bevypro.controller;

import com.bernerbits.vandy.bevypro.HardwareService;
import com.bernerbits.vandy.bevypro.controller.beans.Beverage;

/**
 * Main controller class for Bevy-Pro Beverage Dispenser. 
 * 
 * @author derekberner
 */
public class DispenserController {

	private final HardwareService hardware;
	private final BalanceTracker balanceTracker;
	private final BeverageDispenser beverageDispenser;
	
	public DispenserController(HardwareService hardware, BalanceTracker balanceTracker) {
		this.hardware = hardware;
		this.balanceTracker = balanceTracker;
	}

	public PurchaseResult purchase(Beverage beverage) {
		int price = beverage.getPrice();
		int currentDeposit = balanceTracker.getCurrentBalance();
		if(balanceTracker.getCurrentBalance() < price) {
			return PurchaseResult.fail("Insufficient funds deposited.");
		} else if(!balanceTracker.purchase(price)) {
			return PurchaseResult.fail("Unable to produce correct change.");
		} else if(!beverageDispenser.dispense(beverage)) {
			if(balanceTracker.refund(price)) {
				return PurchaseResult.fail("Hardware failure. Purchase refunded.");
			} else {
				return PurchaseResult.fail("Hardware failure and unable to refund purchase.");
			}
		} else {
			return PurchaseResult.success();
		}
	}
	
	public void refund() {
		balanceTracker.refund();
	}
	
	
}
