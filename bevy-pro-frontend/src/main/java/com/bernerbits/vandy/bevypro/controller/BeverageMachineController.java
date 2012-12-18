package com.bernerbits.vandy.bevypro.controller;

import java.util.LinkedList;
import java.util.Queue;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import com.bernerbits.vandy.bevypro.dao.BeverageDao;
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
@Controller public class BeverageMachineController {

	private HardwareController hardwareController;
	private BeverageDao beverageDao;
	private String message = "";
	
	public synchronized void setHardwareController(HardwareController hardwareController) {
		this.hardwareController = hardwareController;
	}
	public synchronized void setBeverageDao(BeverageDao beverageDao) {
		this.beverageDao = beverageDao;
	}
	
	@RequestMapping(value="/index.ftl",produces="text/html")
	public synchronized BeverageMachineModel getModel() {
		BeverageMachineModel model = new BeverageMachineModel();
		model.setBevs(hardwareController.check(beverageDao.getBeverages()));
		model.setCredit(hardwareController.getCredit());
		model.setMessage(message);
		return model;
	}

	@RequestMapping(value="/dispense/{id}.ftl",produces="text/html")
	public synchronized String dispense(@PathVariable("id") int beverageId) {
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
		doModelUpdate();
		return "redirect:/index.ftl";
	}
	
	@RequestMapping(value="/refund.ftl",produces="text/html")
	public synchronized String refund() {
		int credit = hardwareController.getCredit();
		if(credit == 0) {
			message = "No credit to refund.";
		} else {
			hardwareController.refund();
			message = "Refunded " + credit + "&cent;.";
		}
		doModelUpdate();
		return "redirect:/index.ftl";
	}
	
	// Long polling support for model updates. 
	
	/**
	 * Queue of asynchronous responses awaiting a model update.
	 */
	private final Queue<DeferredResult<BeverageMachineModel>> awaitingResults = new LinkedList<DeferredResult<BeverageMachineModel>>(); 

	/**
	 * Places an asynchronous response on the queue, defaulting to the current model if nothing changes in 
	 * 30 seconds.
	 */
	@RequestMapping("/modelUpdate")
	@ResponseBody
	public synchronized DeferredResult<BeverageMachineModel> pollForModelUpdate(ModelMap model) {
		final DeferredResult<BeverageMachineModel> awaitingResult = new DeferredResult<BeverageMachineModel>(30000L,getModel());
		awaitingResult.onTimeout(new Runnable() {
			@Override
			public void run() {
				awaitingResults.remove(awaitingResult);
			}
		});
		awaitingResults.offer(awaitingResult);
		return awaitingResult;
	}
	
	/**
	 * Called when the model has been updated. All asynchronous responses waiting for the model to change
	 * are notified. 
	 */
	public synchronized void doModelUpdate() {
		if(awaitingResults.isEmpty()) return;
		DeferredResult<BeverageMachineModel> awaitingResult;
		BeverageMachineModel model = getModel();
		while((awaitingResult = awaitingResults.poll()) != null) {
			awaitingResult.setResult(model);
		}
	}
	
}
