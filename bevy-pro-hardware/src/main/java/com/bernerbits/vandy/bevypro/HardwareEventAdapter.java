package com.bernerbits.vandy.bevypro;

/**
 * Convenience class for handling hardware events.
 * 
 * @author derekberner
 */
public abstract class HardwareEventAdapter implements HardwareEventHandler {
	/**
	 * Method called from the hardware layer when a coin is deposited. 
	 * 
	 * @param type The type of coin/bill inserted.
	 * @param value Monetary value of coin/bill inserted.  
	 */
	@Override public void handleCurrencyDeposit(int type, int value) {}
	
	/**
	 * Method called from the hardware layer when a coin/bill is refunded. 
	 * 
	 * @param type The type of coin/bill inserted.
	 * @param value Monetary value of coin/bill inserted.  
	 */
	@Override public void handleCurrencyRefund(int type, int value) {}
	
	/**
	 * Method called from the hardware layer when beverage is dispensed. 
	 * 
	 * @param slot The beverage slot or tap dispensed from.
	 * @param amount Hardware dependent. For cans/bottles, this is always 1. For liquid dispensers,
	 *   this is the volume (in hardware-dependent units) of liquid dispensed.
	 */
	@Override public void handleDispense(int slot, int amount) {}
	
	/**
	 * Method called from the hardware layer when soda count changes, other than dispensing
	 * soda (i.e. when a technician opens the machine and adds sodas, or when a syrup container 
	 * is replaced). <br/>
	 * 
	 * Use the MicrocontrollerService to determine new counts.
	 */
	@Override public void handleSodaChange() {}
	
}
