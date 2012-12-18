package com.bernerbits.vandy.bevypro.controller;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.bernerbits.vandy.bevypro.HardwareEventAdapter;
import com.bernerbits.vandy.bevypro.HardwareService;
import com.bernerbits.vandy.bevypro.model.Beverage;

public class HardwareControllerImpl implements HardwareController {

	private HardwareService hardware;
	private AtomicInteger credit;
	
	public void setHardware(HardwareService hardware) {
		this.hardware = hardware;
		
		// Create a new mutable integer object (atomicity is not strictly needed)
		//   for registering this handler. This ensures that only the last 
		//   handler registered updates the credit amount and avoids duplicate updates
		//   from duplicate handlers.
		final AtomicInteger credit = new AtomicInteger(0);
		hardware.registerHardwareEventHandler(new HardwareEventAdapter() {
			@Override
			public void handleCurrencyDeposit(int type, int value) {
				credit.addAndGet(value);
			}
			
			@Override
			public void handleCurrencyRefund(int type, int value) {
				credit.addAndGet(-value);
			}
		});
		this.credit = credit;
	}
	
	@Override
	public List<Beverage> check(List<Beverage> beverages) {
		for(Beverage beverage : beverages) {
			check(beverage);
		}
		return beverages;
	}
	
	@Override
	public Beverage check(Beverage beverage) {
		beverage.setSoldOut(hardware.countVolume(beverage.getSlot()) == 0);
		return beverage;
	}

	@Override
	public int getCredit() { 
		return credit.get();
	}

	@Override
	public void dispense(Beverage beverage) {
		credit.addAndGet(-beverage.getUnitPrice());
		hardware.dispense(beverage.getSlot(), 1);
	}

}
