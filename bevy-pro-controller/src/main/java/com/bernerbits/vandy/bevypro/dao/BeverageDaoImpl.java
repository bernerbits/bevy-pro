package com.bernerbits.vandy.bevypro.dao;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bernerbits.vandy.bevypro.model.Beverage;

public class BeverageDaoImpl implements BeverageDao {

	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public List<Beverage> getBeverages() {
		long start = System.currentTimeMillis();
		try {
			return jdbcTemplate.query("select b.*, bs.slot_number slot from beverage b inner join beverage_slot bs on b.id = bs.beverage_id", 
					new BeanPropertyRowMapper<Beverage>(Beverage.class));
		} finally {
			System.out.println("getBeverages() Query time: " + (System.currentTimeMillis() - start) + "ms");
		}
	}

	@Override
	public Beverage getBeverage(int id) {
		long start = System.currentTimeMillis();
		try {
			return jdbcTemplate.queryForObject("select b.*, bs.slot_number slot from beverage b inner join beverage_slot bs on b.id = bs.beverage_id where b.id = ?", 
					new Object[]{id}, 
					new BeanPropertyRowMapper<Beverage>(Beverage.class));
		} finally {
			System.out.println("getBeverage(" + id + ") Query time: " + (System.currentTimeMillis() - start) + "ms");
		}
	}

}
