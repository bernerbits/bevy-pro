package com.bernerbits.vandy.bevypro.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bernerbits.vandy.bevypro.model.Beverage;

/**
 * Simple cache decorator with manual invalidation for beverage DAO.
 * 
 * Since H2 queries can take up to half a second and beverages are not likely to change frequently,
 * and the MVC controller makes liberal use of the DAO, this cache decorator significantly improves
 * UI responsiveness. 
 * 
 * @author derekberner
 *
 */
public class CachingBeverageDao implements BeverageDao {

	private BeverageDao target;
	
	public void setTarget(BeverageDao target) {
		this.target = target;
	}
	
	private List<Beverage> beverages;
	private Map<Integer,Beverage> beveragesById = new HashMap<Integer,Beverage>();
	
	@Override
	public synchronized List<Beverage> getBeverages() {
		if(beverages == null) {
			beverages = target.getBeverages();
		}
		return beverages;
	}

	@Override
	public synchronized Beverage getBeverage(int id) {
		if(!beveragesById.containsKey(id)) {
			beveragesById.put(id, target.getBeverage(id));
		} 
		return beveragesById.get(id);
	}

	public synchronized void invalidateCache() {
		beverages = null;
		beveragesById.clear();
	}
	
}
