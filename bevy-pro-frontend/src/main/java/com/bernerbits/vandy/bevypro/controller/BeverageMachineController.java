package com.bernerbits.vandy.bevypro.controller;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import com.bernerbits.vandy.bevypro.dao.BeverageDao;
import com.bernerbits.vandy.bevypro.model.Beverage;

@Controller
public class BeverageMachineController {

	private HardwareController hardwareController;
	private BeverageDao beverageDao;
	private String message = "";
	
	public void setHardwareController(HardwareController hardwareController) {
		this.hardwareController = hardwareController;
	}
	public void setBeverageDao(BeverageDao beverageDao) {
		this.beverageDao = beverageDao;
	}
	
	@RequestMapping(value="/index.ftl",produces="text/html")
	public void populateModel(ModelMap model) {
		model.put("bevs", hardwareController.check(beverageDao.getBeverages()));
		model.put("credit", hardwareController.getCredit());
		model.put("message", message);
	}

	@RequestMapping(value="/dispense/{id}.ftl",produces="text/html")
	public String dispense(@PathVariable("id") String beverageId, ModelMap model) {
		Beverage beverage = beverageDao.getBeverage(beverageId);
		hardwareController.check(beverage);
		if(beverage.isSoldOut()) {
			message = beverage.getName() + " is sold out.";
		} else if(hardwareController.getCredit() < beverage.getUnitPrice()) {
			int difference = beverage.getUnitPrice() - hardwareController.getCredit();
			message = "Please insert " + difference + "&cent;.";
		} else {
			// purchaseController.purchase(beverage); // Track purchase
			hardwareController.dispense(beverage);
			message = "Vending...";
		}
		return "redirect:/index.ftl";
	}
}
