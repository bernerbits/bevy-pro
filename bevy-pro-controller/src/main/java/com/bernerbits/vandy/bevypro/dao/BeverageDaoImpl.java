package com.bernerbits.vandy.bevypro.dao;

import java.util.ArrayList;
import java.util.List;

import com.bernerbits.vandy.bevypro.model.Beverage;

public class BeverageDaoImpl implements BeverageDao {

	@Override
	public List<Beverage> getBeverages() {
		// TODO Get these from the database.
		List<Beverage> bevs = new ArrayList<Beverage>();
		bevs.add(getBeverage(1));
		bevs.add(getBeverage(2));
		bevs.add(getBeverage(3));
		bevs.add(getBeverage(4));
		return bevs;
	}

	@Override
	public Beverage getBeverage(int id) {
		// TODO Get these from the database.
		switch(id) {
		case 1: return new Beverage(1,"images/cocacola.gif","Coca-Cola",65,0,false);
		case 2: return new Beverage(2,"images/dietcoke.png","Diet Coke",65,1,false);
		case 3: return new Beverage(3,"images/sprite.jpg","Sprite",65,2,false);
		case 4: return new Beverage(4,"images/mtndew.jpg","Mountain Dew",65,3,false);
		default: return null;
		}
	}

		
	
}
