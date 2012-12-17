package com.bernerbits.vandy.bevypro;

/**
 * Main facade interface from Bevy-Pro&#8482; to hardware module. </br>
 * 
 * Designed to have as simple an API as possible, to make integration of new modules
 * quick and easy.
 * 
 * @author derekberner
 */
public interface HardwareService {
	/** 
	 * Registers an event handler with the hardware, so that hardware events (such as a 
	 * currency deposit) can be handled by the software.
	 *  
	 * @param handler Implementation of a HardwareEventHandler.
	 */
	void registerHardwareEventHandler(HardwareEventHandler handler);
		
	/**
	 * Reports the number of coins/bills currently available for the given type. <br/>
	 * 
	 * The meaning of the type value is dependent on the hardware module used,
	 * as coin/bill codes may vary by hardware module, and modules may support 
	 * different currencies by country. <br/>
	 * 
	 * @param type The type of coin/bill to count. 
	 * @return The number of coins/bills present.
	 */
	int countCurrency(int type);
	
	/**
	 * Refunds one coin/bill of the given type. <br/>
	 * 
	 * The meaning of the type value is dependent on the hardware module used,
	 * as coin/bill codes may vary by hardware module, and modules may support 
	 * different currencies by country. <br/>
	 * 
	 * @param type The type of coin/bill to count. 
	 * @return The number of coins/bills present.
	 */
	void refund(int type);

	/**
	 * Reports, typically, how many sodas are currently available in the given slot.<br/> 
	 * 
	 * Depending on the hardware, however, this may also report the volume of soda, syrup 
	 * or carbonated water available from a given tap. The exact unit of volume is 
	 * hardware-dependent, however.<br/>
	 * 
	 * @param slot The slot or tap to check, starting from 0. 
	 * @return The number of cans/bottles or volume (in hardware-dependent units) of liquid
	 *   left in the given slot.
	 */
	int countVolume(int slot);

	/**
	 * Reports the valid currency types honored by this hardware module.
	 * 
	 * @return
	 */
	int[] listCurrencyTypes();
	
	/**
	 * Reports the monetary value of the given currency type for this hardware module. 
	 * 
	 * @param The hardware-dependent currency type.
	 * 
	 * @return The monetary value of the currency type.
	 */
	int getCurrencyValue(int type);

	/**
	 * @return The number of taps or bottle/can slots available.
	 */
	int countSlots();
	
	/**
	 * Dispenses soda from the given slot or tap.
	 * 
	 * @param slot The slot or tap to dispense from, starting from 0. 
	 * @param amount Hardware-dependent. For cans and bottles, this value is ignored. 
	 *   For liquid dispensers, the volume (in hardware-dependent units) of liquid
	 *   to dispense from the slot. Note that in a syrup/carbonated water setup, multiple 
	 *   simultaneous liquids will be dispensed at the same time, so multiple calls to
	 *   this method are appropriate.
	 */
	void dispense(int slot, int amount);
	
}
