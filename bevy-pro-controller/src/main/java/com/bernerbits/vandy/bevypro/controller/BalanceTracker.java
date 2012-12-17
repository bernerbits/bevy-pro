package com.bernerbits.vandy.bevypro.controller;

import com.bernerbits.vandy.bevypro.controller.beans.Currency;

public interface BalanceTracker {

	void deposit(Currency currency);

	boolean purchase(int amount);
	boolean refund(int amount);
	
	int getCurrentBalance();

	void refund();
	
}
