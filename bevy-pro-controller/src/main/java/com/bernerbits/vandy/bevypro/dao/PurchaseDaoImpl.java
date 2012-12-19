package com.bernerbits.vandy.bevypro.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import com.bernerbits.vandy.bevypro.model.Beverage;

public class PurchaseDaoImpl implements PurchaseDao {

	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public void purchase(Beverage beverage) {
		jdbcTemplate.update("insert into purchase(beverage_name, purchase_amount, purchase_price) " +
			"values(?,?,?)",new Object[]{beverage.getName(),1,beverage.getUnitPrice()});
	}

}
