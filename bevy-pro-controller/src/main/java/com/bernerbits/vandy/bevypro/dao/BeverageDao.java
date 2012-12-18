package com.bernerbits.vandy.bevypro.dao;

import java.util.List;

import com.bernerbits.vandy.bevypro.model.Beverage;

public interface BeverageDao {

	List<Beverage> getBeverages();

	Beverage getBeverage(int id);

}
