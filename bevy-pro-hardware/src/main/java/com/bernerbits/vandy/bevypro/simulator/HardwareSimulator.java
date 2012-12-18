package com.bernerbits.vandy.bevypro.simulator;

import java.util.ArrayList;
import java.util.List;

import com.bernerbits.vandy.bevypro.HardwareEventHandler;

/** 
 * In-Memory hardware simulator for Bevy-Pro software. <br/> 
 * 
 * Simulation module uses 4 soda slots, and accepts only quarters. <br/>
 * 
 * It is assumed that the hardware makes no validation as to whether a particular product has been paid for,
 * or a refund of change is permissible. This is all expected to be handled at the software layer.
 * 
 * @author derekberner
 *
 */
public class HardwareSimulator implements HardwareSimulationService {

	protected static final int SODA_SLOTS = 4; // 4 soda slots.
	
	private ItemCounter[] sodas;
	
	public HardwareSimulator() {
		sodas = new ItemCounter[SODA_SLOTS];
		for(int i = 0; i < SODA_SLOTS; i++) {
			sodas[i] = new ItemCounter();
		}
	}
	
	private List<HardwareEventHandler> handlers = new ArrayList<HardwareEventHandler>();
	
	/* API Methods */
	
	@Override
	public void registerHardwareEventHandler(HardwareEventHandler handler) {
		handlers.add(handler);
	}

	@Override
	public int countVolume(int slot) {
		return sodas[slot].get();
	}

	/*package-private*/ static final int NICKEL = 1;
	/*package-private*/ static final int DIME = 2;
	/*package-private*/ static final int QUARTER = 3;
	/*package-private*/ static final int DOLLAR = 4;
	
	@Override
	public int[] listCurrencyTypes() {
		return new int[]{NICKEL,DIME,QUARTER,DOLLAR};
	}

	@Override
	public int getCurrencyValue(int type) {
		switch(type) {
		case NICKEL: return 5;
		case DIME: return 10;
		case QUARTER: return 25;
		case DOLLAR: return 100;
		default: return 0;
		}
	}
	
	@Override
	public int countSlots() {
		return SODA_SLOTS; 
	}

	@Override
	public void dispense(int slot, int amount) {
		if(sodas[slot].takeOne()) {
			for(HardwareEventHandler handler : handlers) {
				handler.handleDispense(slot, amount);
			}
		}
	}

	@Override
	public void refund(int type) {
		for(HardwareEventHandler handler : handlers) {
			handler.handleCurrencyRefund(QUARTER,getCurrencyValue(type));
		}
	}

	/* Simulator Methods */
	
	@Override
	public void setSodas(int slot, int count) {
		sodas[slot].set(count);
		for(HardwareEventHandler handler : handlers) {
			handler.handleSodaChange();
		}
	}
	
	@Override
	public void insertCurrency(int type) {
		for(HardwareEventHandler handler : handlers) {
			handler.handleCurrencyDeposit(QUARTER,getCurrencyValue(type));
		}
	}
}
