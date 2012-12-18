package com.bernerbits.vandy.bevypro;

/**
 * Callback interface for hardware events.
 * 
 * @author derekberner
 */
public interface HardwareEventHandler {
	/**
	 * Method called from the hardware layer when a coin/bill is deposited. 
	 * 
	 * @param type The type of coin/bill inserted.
	 * @param value Monetary value of coin/bill inserted.  
	 */
	void handleCurrencyDeposit(int type, int value);
	
	/**
	 * Method called from the hardware layer when a coin/bill is refunded. 
	 * 
	 * @param type The type of coin/bill inserted.
	 * @param value Monetary value of coin/bill inserted.  
	 */
	void handleCurrencyRefund(int type, int value);
	
	/**
	 * Method called from the hardware layer when beverage is dispensed. 
	 * 
	 * @param slot The beverage slot or tap dispensed from.
	 * @param amount Hardware dependent. For cans/bottles, this is always 1. For liquid dispensers,
	 *   this is the volume (in hardware-dependent units) of liquid dispensed.
	 */
	void handleDispense(int slot, int amount);
	
	/**
	 * Method called from the hardware layer when soda count changes (e.g. when a technician opens 
	 * the machine and adds sodas, or when a syrup container is replaced). <br/>
	 * 
	 * Use the MicrocontrollerService to determine new counts.
	 */
	void handleSodaChange();
	
}
