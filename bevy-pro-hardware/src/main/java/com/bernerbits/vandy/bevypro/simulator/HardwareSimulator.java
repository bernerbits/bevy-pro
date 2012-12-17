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
	
	private ItemCounter quarters = new ItemCounter();
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
	public int countCurrency(int type) {
		switch(type) {
		case QUARTER: return quarters.get();
		default: return 0;
		}
	}

	@Override
	public int countVolume(int slot) {
		return sodas[slot].get();
	}

	/*package-private*/ static final int QUARTER = 1;
	
	@Override
	public int[] listCurrencyTypes() {
		return new int[]{QUARTER};
	}

	@Override
	public int getCurrencyValue(int type) {
		switch(type) {
		case QUARTER: return 25;
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
		switch(type) {
		case QUARTER: 
			if(quarters.takeOne()) {
				for(HardwareEventHandler handler : handlers) {
					handler.handleCurrencyRefund(QUARTER,25);
				}
			}
		}
	}

	/* Simulator Methods */
	
	@Override
	public void setCurrency(int type, int count) {
		switch(type) {
		case QUARTER: 
			quarters.set(count);
			for(HardwareEventHandler handler : handlers) {
				handler.handleCurrencyChange();
			}
		}
	}
	
	@Override
	public void setSodas(int slot, int count) {
		sodas[slot].set(count);
		for(HardwareEventHandler handler : handlers) {
			handler.handleSodaChange();
		}
	}
	
	@Override
	public void insertCurrency(int type) {
		switch(type) {
		case QUARTER: 
			quarters.addOne();
			for(HardwareEventHandler handler : handlers) {
				handler.handleCurrencyDeposit(QUARTER,25);
			}
		}
	}
}
