package com.bernerbits.vandy.bevypro.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.bernerbits.vandy.bevypro.HardwareEventAdapter;
import com.bernerbits.vandy.bevypro.HardwareService;
import com.bernerbits.vandy.bevypro.controller.beans.Currency;

/**
 * Stateful class for tracking the current balance, making change on purchases,
 * and refunding the current balance. <br/>
 * 
 * This class is not thread safe.
 * 
 * @author derekberner
 *
 */
public class BalanceTrackerImpl extends HardwareEventAdapter implements BalanceTracker {

	private final HardwareService hardware;
	private final NavigableSet<Currency> currencyTypes;
	
	private int currentBalance = 0;
	private List<Currency> currentDeposit = new ArrayList<Currency>();
	
	public BalanceTrackerImpl(HardwareService hardware) {
		this.hardware = hardware;
		NavigableSet<Currency> currencyTypes = new TreeSet<Currency>(Collections.reverseOrder());
		for(int type : hardware.listCurrencyTypes()) {
			int value = hardware.getCurrencyValue(type);
			currencyTypes.add(new Currency(type,value));
		}
		this.currencyTypes = currencyTypes;
	}
	
	/**
	 * Hardware event handler for currency deposit. 
	 */
	@Override public void handleCurrencyDeposit(int type, int value) {
		deposit(new Currency(type,value));
	}
	
	/**
	 * Deposit one coin of the specified currency.
	 * 
	 * @param currency
	 */
	@Override public void deposit(Currency currency) {
		currentBalance += currency.getValue();
		currentDeposit.add(currency);
	}
	
	/**
	 * Make a purchase for the given amount. <br/>
	 * 
	 * Attempts to make change for the current balance minus the given amount.
	 * 
	 * @param amount
	 * @return true if change could be
	 */
	@Override public boolean purchase(int amount) {
		int changeRemaining = currentBalance - amount;
		if(refund(changeRemaining)) {
			currentBalance = 0;
			currentDeposit.clear();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @return The current balance.
	 */
	@Override public int getCurrentBalance() {
		return currentBalance;
	}
	
	/**
	 * Refund all coins/bills currently deposited.
	 */
	@Override public void refund() {
		currentBalance = 0;
		for(Currency coinOrBill : currentDeposit) {
			hardware.refund(coinOrBill.getType());
		}
		currentDeposit.clear();
	}
	
	/**
	 * Refund a specific amount.
	 */
	@Override public boolean refund(int changeRemaining) {
		List<Currency> change = new ArrayList<Currency>();
		for(Currency coinOrBill : currencyTypes) {
			int coinsOrBillsRemaining = hardware.countCurrency(coinOrBill.getType());
			while(coinsOrBillsRemaining > 0 && changeRemaining > coinOrBill.getValue()) {
				coinsOrBillsRemaining--;
				changeRemaining -= coinOrBill.getValue();
				change.add(coinOrBill);
			}
		}
		if(changeRemaining == 0) {
			for(Currency coinOrBill : change) {
				hardware.refund(coinOrBill.getType());
			}
			return true;
		} else {
			return false;
		}
	}

}
