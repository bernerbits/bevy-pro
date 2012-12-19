package com.bernerbits.vandy.bevypro.controller;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bernerbits.vandy.bevypro.dao.BeverageDao;
import com.bernerbits.vandy.bevypro.dao.PurchaseDao;
import com.bernerbits.vandy.bevypro.format.MoneyFormatter;
import com.bernerbits.vandy.bevypro.listener.ModelUpdateListener;
import com.bernerbits.vandy.bevypro.model.Beverage;
import com.bernerbits.vandy.bevypro.model.BeverageMachineModel;

/**
 * MVC Controller for Beverage Machine UI. <br/>
 * 
 * This controller is application-scoped, as the system architecture is such that
 * individual session state need not be tracked. <br/>
 * 
 * However, it's possible for a user to generate race conditions by pressing buttons
 * in rapid succession. Therefore, all controller actions are synchronized.<br/>
 * 
 * @author derekberner
 */
@Controller public class BeverageMachineController implements ModelUpdateListener {

	private HardwareController hardwareController;
	private PurchaseDao purchaseDao;
	private BeverageDao beverageDao;
	private String message = "";
	private MoneyFormatter moneyFormatter;
	
	public synchronized void setHardwareController(HardwareController hardwareController) {
		this.hardwareController = hardwareController;
	}
	public synchronized void setBeverageDao(BeverageDao beverageDao) {
		this.beverageDao = beverageDao;
	}
	public synchronized void setPurchaseDao(PurchaseDao purchaseDao) {
		this.purchaseDao = purchaseDao;
	}
	public synchronized void setMoneyFormatter(MoneyFormatter moneyFormatter) {
		this.moneyFormatter = moneyFormatter;
	}
	
	@PostConstruct
	public synchronized void initialize() {
		hardwareController.registerModelUpdateListener(this);
	}
	
	@RequestMapping(value="/index")
	@ModelAttribute("m") // model root
	public synchronized BeverageMachineModel getModel() {
		BeverageMachineModel model = new BeverageMachineModel();
		model.setBevs(hardwareController.check(beverageDao.getBeverages()));
		model.setCredit(hardwareController.getCredit());
		model.setCreditString(moneyFormatter.format(hardwareController.getCredit()));
		model.setMessage(message);
		return model;
	}

	@RequestMapping(value="/dispense/{id}.ftl")
	public synchronized String dispense(@PathVariable("id") int beverageId) {
		Beverage beverage = beverageDao.getBeverage(beverageId);
		hardwareController.check(beverage);
		if(beverage.isSoldOut()) {
			message = beverage.getName() + " is sold out.";
		} else if(hardwareController.getCredit() < beverage.getUnitPrice()) {
			int difference = beverage.getUnitPrice() - hardwareController.getCredit();
			message = "Please insert " + moneyFormatter.format(difference) + ".";
		} else {
			purchaseDao.purchase(beverage); // Track purchase
			hardwareController.dispense(beverage);
			message = "Vending...";
		}
		doModelUpdate();
		return "redirect:/index.ftl";
	}
	
	@RequestMapping(value="/refund.ftl")
	public synchronized String refund() {
		int credit = hardwareController.getCredit();
		if(credit == 0) {
			message = "No credit to refund.";
		} else {
			hardwareController.refund();
			message = "Refunded " + moneyFormatter.format(credit) + ".";
		}
		doModelUpdate();
		return "redirect:/index.ftl";
	}
	
	/**
	 * Waits for the model to change, defaulting to the current model if nothing changes in 30 seconds. Note
	 * that the act of waiting temporarily releases synchronization.
	 * 
	 * @throws InterruptedException 
	 */
	@RequestMapping("/modelUpdate")
	@ModelAttribute("m") // model root
	public synchronized BeverageMachineModel pollForModelUpdate() throws InterruptedException {
		this.wait(30000); 
		return getModel();
	}
	
	/**
	 * Called when the model has been updated. All responses waiting for the model to change
	 * are notified. 
	 */
	@Override public synchronized void doModelUpdate() {
		notifyAll();
	}
	
}
