package com.bernerbits.vandy.bevypro.format;

import java.math.BigDecimal;

public class USCentsFormatter implements MoneyFormatter {
	@Override public String format(int cents) {
		if(cents < 100) {
			return cents + "¢";
		} else {
			return "$" + new BigDecimal(cents).movePointLeft(2);
		}
	}
	
}
