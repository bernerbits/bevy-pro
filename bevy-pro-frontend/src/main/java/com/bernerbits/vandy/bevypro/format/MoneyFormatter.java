package com.bernerbits.vandy.bevypro.format;

import java.io.Serializable;

/**
 * Interface for formatting money. Can be re-implemented for
 * non-US currency.
 * 
 * @author derekberner
 *
 */
public interface MoneyFormatter {

	String format(int value);
	
}
