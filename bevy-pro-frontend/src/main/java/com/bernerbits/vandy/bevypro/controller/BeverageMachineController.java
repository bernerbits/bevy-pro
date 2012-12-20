package com.bernerbits.vandy.bevypro.controller;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
	private ScheduledThreadPoolExecutor schedule;
	
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
		schedule = new ScheduledThreadPoolExecutor(1);
	}
	
	@PreDestroy
	public synchronized void destroy() {
		schedule.shutdown();
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

	/**
	 * Set message and schedule a job to clear it.
	 * 
	 * @param tempMessage
	 */
	private void setTempMessage(final String tempMessage){
		message = tempMessage;

		// Schedule job to clear message after 2 seconds
		schedule.schedule(new Runnable() {
			@Override
			public void run() {
				synchronized(BeverageMachineController.this) {
					// Clear message only if it hasn't changed yet.
					if(message.equals(tempMessage)) {
						message = "";
						doModelUpdate();
					}
				}
			}
		}, 3, TimeUnit.SECONDS);
	}
	
	private void dispense(int beverageId) {
		Beverage beverage = beverageDao.getBeverage(beverageId);
		hardwareController.check(beverage);
		if(beverage.isSoldOut()) {
			setTempMessage(beverage.getName() + " is sold out.");
		} else if(hardwareController.getCredit() < beverage.getUnitPrice()) {
			int difference = beverage.getUnitPrice() - hardwareController.getCredit();
			setTempMessage("Please insert " + moneyFormatter.format(difference) + ".");
		} else {
			purchaseDao.purchase(beverage); // Track purchase
			hardwareController.dispense(beverage);
			setTempMessage("Vending...");
		}
		doModelUpdate();
	}
	
	@RequestMapping(value="/dispense/{id}.json")
	@ModelAttribute("m") // model root
	public synchronized BeverageMachineModel dispenseJSON(@PathVariable("id") int beverageId) {
		dispense(beverageId);
		return getModel();
	}

	@RequestMapping(value="/dispense/{id}.ftl")
	public synchronized String dispenseFreemarker(@PathVariable("id") int beverageId) {
		dispense(beverageId);
		return "redirect:/index.ftl";
	}

	@RequestMapping(value="/refund.json")
	@ModelAttribute("m") // model root
	public synchronized BeverageMachineModel refundJSON() {
		refund();
		return getModel();
	}

	@RequestMapping(value="/refund.ftl")
	public synchronized String refundFreemarker() {
		refund();
		return "redirect:/index.ftl";
	}

	private void refund() {
		int credit = hardwareController.getCredit();
		if(credit == 0) {
			setTempMessage("No credit to refund.");
		} else {
			hardwareController.refund();
			setTempMessage("Refunded " + moneyFormatter.format(credit) + ".");
		}
		doModelUpdate();
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
