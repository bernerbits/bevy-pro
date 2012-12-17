package com.bernerbits.vandy.bevypro.simulator;

import com.bernerbits.vandy.bevypro.HardwareService;

/**
 * Additional microcontroller methods for simulation only. 
 * 
 * @author derekberner
 *
 */
public interface HardwareSimulationService extends HardwareService {

	void setCurrency(int type, int count);

	void setSodas(int slot, int count);

	void insertCurrency(int type);

}
