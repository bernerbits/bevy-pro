package com.bernerbits.vandy.bevypro.controller;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.bernerbits.vandy.bevypro.HardwareEventHandler;
import com.bernerbits.vandy.bevypro.HardwareService;
import com.bernerbits.vandy.bevypro.listener.ModelUpdateListener;
import com.bernerbits.vandy.bevypro.model.Beverage;

public class HardwareControllerTest {

	@Test
	public void testCheckBeverage() {
		HardwareService hwService = mock(HardwareService.class);
		when(hwService.countVolume(1)).thenReturn(0);
		when(hwService.countVolume(2)).thenReturn(1);
		
		Beverage bev1 = new Beverage();
		bev1.setSlot(1);
		
		Beverage bev2 = new Beverage();
		bev2.setSlot(2);

		HardwareControllerImpl hardware = new HardwareControllerImpl();
		hardware.setHardware(hwService);
		
		hardware.check(bev1);
		hardware.check(bev2);
		
		assertTrue(bev1.isSoldOut());
		assertFalse(bev2.isSoldOut());
	}
	
	@Test
	public void testCheckBeverages() {
		HardwareService hwService = mock(HardwareService.class);
		when(hwService.countVolume(1)).thenReturn(0);
		when(hwService.countVolume(2)).thenReturn(1);
		
		Beverage bev1 = new Beverage();
		bev1.setSlot(1);
		
		Beverage bev2 = new Beverage();
		bev2.setSlot(2);

		HardwareControllerImpl hardware = new HardwareControllerImpl();
		hardware.setHardware(hwService);
		
		hardware.check(Arrays.asList(bev1,bev2));
		
		assertTrue(bev1.isSoldOut());
		assertFalse(bev2.isSoldOut());
	}
	
	@Test
	public void testDepositHandler() {
		HardwareService hwService = mock(HardwareService.class);
		final AtomicReference<HardwareEventHandler> ref = new AtomicReference<HardwareEventHandler>();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ref.set((HardwareEventHandler)(invocation.getArguments()[0]));
				return null;
			}
		}).when(hwService).registerHardwareEventHandler(any(HardwareEventHandler.class));
		
		HardwareControllerImpl hardware = new HardwareControllerImpl();
		hardware.setHardware(hwService);
		
		ref.get().handleCurrencyDeposit(1,25);
		assertEquals(25, hardware.getCredit());
	}

	@Test
	public void testRefund() {
		HardwareService hwService = mock(HardwareService.class);
		final AtomicReference<HardwareEventHandler> ref = new AtomicReference<HardwareEventHandler>();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ref.set((HardwareEventHandler)(invocation.getArguments()[0]));
				return null;
			}
		}).when(hwService).registerHardwareEventHandler(any(HardwareEventHandler.class));
		
		HardwareControllerImpl hardware = new HardwareControllerImpl();
		hardware.setHardware(hwService);
		
		ref.get().handleCurrencyDeposit(1,25);
		ref.get().handleCurrencyDeposit(2,10);
		ref.get().handleCurrencyDeposit(2,10);
		ref.get().handleCurrencyDeposit(3,5);
		
		assertEquals(50, hardware.getCredit());
		
		hardware.refund();
		InOrder inOrder = inOrder(hwService);
		inOrder.verify(hwService).refund(1);
		inOrder.verify(hwService,times(2)).refund(2);
		inOrder.verify(hwService).refund(3);
	}

	/**
	 * Refunding a coin, if available, reduces current credit by that amount.
	 */
	@Test
	public void testRefundHandler() {
		HardwareService hwService = mock(HardwareService.class);
		final AtomicReference<HardwareEventHandler> ref = new AtomicReference<HardwareEventHandler>();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ref.set((HardwareEventHandler)(invocation.getArguments()[0]));
				return null;
			}
		}).when(hwService).registerHardwareEventHandler(any(HardwareEventHandler.class));
		
		HardwareControllerImpl hardware = new HardwareControllerImpl();
		hardware.setHardware(hwService);
		
		ref.get().handleCurrencyDeposit(1,25);
		ref.get().handleCurrencyDeposit(2,5);
		
		assertEquals(30, hardware.getCredit());
		
		ref.get().handleCurrencyRefund(2, 5);
		
		assertEquals(25, hardware.getCredit());
	}
	
	/**
	 * Refunding a coin that hasn't been deposited does nothing.
	 */
	@Test
	public void testRefundUnavailableCoin() {
		HardwareService hwService = mock(HardwareService.class);
		final AtomicReference<HardwareEventHandler> ref = new AtomicReference<HardwareEventHandler>();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ref.set((HardwareEventHandler)(invocation.getArguments()[0]));
				return null;
			}
		}).when(hwService).registerHardwareEventHandler(any(HardwareEventHandler.class));
		
		HardwareControllerImpl hardware = new HardwareControllerImpl();
		hardware.setHardware(hwService);
		
		ref.get().handleCurrencyDeposit(1,25);
		ref.get().handleCurrencyDeposit(2,5);
		
		assertEquals(30, hardware.getCredit());
		
		ref.get().handleCurrencyRefund(3, 10);
		
		assertEquals(30, hardware.getCredit());
	}

	@Test
	public void testDepositNotifiesListener() {
		HardwareService hwService = mock(HardwareService.class);
		final AtomicReference<HardwareEventHandler> ref = new AtomicReference<HardwareEventHandler>();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ref.set((HardwareEventHandler)(invocation.getArguments()[0]));
				return null;
			}
		}).when(hwService).registerHardwareEventHandler(any(HardwareEventHandler.class));
		
		HardwareControllerImpl hardware = new HardwareControllerImpl();
		hardware.setHardware(hwService);
		
		ModelUpdateListener listener = mock(ModelUpdateListener.class);
		hardware.registerModelUpdateListener(listener);
		
		ref.get().handleCurrencyDeposit(1,25);
		
		verify(listener,times(1)).doModelUpdate();
	}

	@Test
	public void testRefundNotifiesListener() {
		HardwareService hwService = mock(HardwareService.class);
		final AtomicReference<HardwareEventHandler> ref = new AtomicReference<HardwareEventHandler>();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ref.set((HardwareEventHandler)(invocation.getArguments()[0]));
				return null;
			}
		}).when(hwService).registerHardwareEventHandler(any(HardwareEventHandler.class));
		
		HardwareControllerImpl hardware = new HardwareControllerImpl();
		hardware.setHardware(hwService);
		
		ModelUpdateListener listener = mock(ModelUpdateListener.class);
		hardware.registerModelUpdateListener(listener);
		
		ref.get().handleCurrencyRefund(1,25);
		
		verify(listener,times(1)).doModelUpdate();
	}

	@Test
	public void testDispenseNotifiesListener() {
		HardwareService hwService = mock(HardwareService.class);
		final AtomicReference<HardwareEventHandler> ref = new AtomicReference<HardwareEventHandler>();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ref.set((HardwareEventHandler)(invocation.getArguments()[0]));
				return null;
			}
		}).when(hwService).registerHardwareEventHandler(any(HardwareEventHandler.class));
		
		HardwareControllerImpl hardware = new HardwareControllerImpl();
		hardware.setHardware(hwService);
		
		ModelUpdateListener listener = mock(ModelUpdateListener.class);
		hardware.registerModelUpdateListener(listener);
		
		ref.get().handleDispense(1, 1);
		
		verify(listener,times(1)).doModelUpdate();
	}

	@Test
	public void testSodaChangeNotifiesListener() {
		HardwareService hwService = mock(HardwareService.class);
		final AtomicReference<HardwareEventHandler> ref = new AtomicReference<HardwareEventHandler>();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ref.set((HardwareEventHandler)(invocation.getArguments()[0]));
				return null;
			}
		}).when(hwService).registerHardwareEventHandler(any(HardwareEventHandler.class));
		
		HardwareControllerImpl hardware = new HardwareControllerImpl();
		hardware.setHardware(hwService);
		
		ModelUpdateListener listener = mock(ModelUpdateListener.class);
		hardware.registerModelUpdateListener(listener);
		
		ref.get().handleSodaChange();
		
		verify(listener,times(1)).doModelUpdate();
	}


	@Test
	public void testMultipleListeners() {
		HardwareService hwService = mock(HardwareService.class);
		final AtomicReference<HardwareEventHandler> ref = new AtomicReference<HardwareEventHandler>();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ref.set((HardwareEventHandler)(invocation.getArguments()[0]));
				return null;
			}
		}).when(hwService).registerHardwareEventHandler(any(HardwareEventHandler.class));
		
		HardwareControllerImpl hardware = new HardwareControllerImpl();
		hardware.setHardware(hwService);
		
		ModelUpdateListener listener1 = mock(ModelUpdateListener.class);
		ModelUpdateListener listener2 = mock(ModelUpdateListener.class);
		ModelUpdateListener listener3 = mock(ModelUpdateListener.class);
		
		hardware.registerModelUpdateListener(listener1);
		hardware.registerModelUpdateListener(listener2);
		hardware.registerModelUpdateListener(listener3);
		
		ref.get().handleSodaChange();
		
		verify(listener1,times(1)).doModelUpdate();
		verify(listener2,times(1)).doModelUpdate();
		verify(listener3,times(1)).doModelUpdate();
	}
	
	@Test
	public void testDispenseMakesCorrectChange() {
		HardwareService hwService = mock(HardwareService.class);
		final AtomicReference<HardwareEventHandler> ref = new AtomicReference<HardwareEventHandler>();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ref.set((HardwareEventHandler)(invocation.getArguments()[0]));
				return null;
			}
		}).when(hwService).registerHardwareEventHandler(any(HardwareEventHandler.class));
		
		HardwareControllerImpl hardware = new HardwareControllerImpl();
		hardware.setHardware(hwService);
		
		when(hwService.listCurrencyTypes()).thenReturn(new int[]{1,2,3});
		when(hwService.getCurrencyValue(1)).thenReturn(5);
		when(hwService.getCurrencyValue(2)).thenReturn(10);
		when(hwService.getCurrencyValue(3)).thenReturn(25);
		
		ref.get().handleCurrencyDeposit(3, 25);
		ref.get().handleCurrencyDeposit(3, 25);
		ref.get().handleCurrencyDeposit(3, 25);
		
		Beverage bev = new Beverage();
		bev.setSlot(1);
		bev.setUnitPrice(45);
		
		hardware.dispense(bev);
		
		InOrder inOrder = inOrder(hwService);
		inOrder.verify(hwService).dispense(1, 1);
		inOrder.verify(hwService).refund(3);
		inOrder.verify(hwService).refund(1);
		
		assertEquals(0,hardware.getCredit());
	}

	
}
