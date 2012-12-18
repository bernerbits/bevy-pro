package com.bernerbits.vandy.bevypro.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.bernerbits.vandy.bevypro.HardwareEventAdapter;
import com.bernerbits.vandy.bevypro.HardwareService;
import com.bernerbits.vandy.bevypro.model.Beverage;
import com.bernerbits.vandy.bevypro.model.Currency;

public class HardwareControllerImpl implements HardwareController {

	private HardwareService hardware;
	private AtomicInteger credit;
	private List<Currency> deposit;
	
	public void setHardware(HardwareService hardware) {
		this.hardware = hardware;
		
		// Create a new AtomicInteger object (for mutability; atomicity is not needed)
		//   and Currency list for registering this handler. This ensures that only the last 
		//   handler registered updates the credit amount and avoids duplicate updates
		//   from duplicate handlers.
		final AtomicInteger credit = new AtomicInteger(0);
		final List<Currency> deposit = new ArrayList<Currency>();
		hardware.registerHardwareEventHandler(new HardwareEventAdapter() {
			@Override
			public void handleCurrencyDeposit(int type, int value) {
				credit.addAndGet(value);
				deposit.add(new Currency(type,value));
			}
			
			@Override
			public void handleCurrencyRefund(int type, int value) {
				if(deposit.remove(new Currency(type,value))) {
					credit.addAndGet(-value);
				}
			}
		});
		this.credit = credit;
		this.deposit = deposit;
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
		hardware.dispense(beverage.getSlot(), 1);
		makeChange(beverage.getUnitPrice());
	}

	private void makeChange(int purchasePrice) {
		// TODO: Precalculate this.
		Set<Currency> currencies = new TreeSet<Currency>(Collections.reverseOrder());
		for(int currencyType : hardware.listCurrencyTypes()) {
			Currency currency = new Currency(currencyType, hardware.getCurrencyValue(currencyType));
			currencies.add(currency);
		}
		
		// Use the greedy change-making algorithm. Optimal in the US; may not be optimal in all locales.
		int remainingChange = credit.get() - purchasePrice;
		for(Currency currency : currencies) {
			while(remainingChange > 0 && currency.getValue() <= remainingChange) {
				remainingChange -= currency.getValue();
				hardware.refund(currency.getType());
			}
			if(remainingChange == 0) {
				break; // Terminate early.
			}
		}
		
		// Finally, reset the credit.
		credit.set(0);
		deposit.clear();
	}

	@Override
	public void refund() {
		// Iterate through a clone of the deposit collection, 
		//   because refund will modify the deposit.
		for(Currency coinOrBill : new ArrayList<Currency>(deposit)) {
			hardware.refund(coinOrBill.getType());
		}
	}
}
