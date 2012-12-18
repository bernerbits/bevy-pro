package com.bernerbits.vandy.bevypro.simulator;

import static org.junit.Assert.*;

import org.junit.Test;
import static org.mockito.Mockito.*;

import com.bernerbits.vandy.bevypro.HardwareEventHandler;

public class HardwareSimulatorTest {

	/**
	 * Soda volume can be set, and fires an event when modified.
	 */
	@Test public void testSetSodas() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		simulator.setSodas(0, 5);
		assertEquals(5, simulator.countVolume(0));
		
		simulator.setSodas(1, 10);
		assertEquals(10, simulator.countVolume(1));
		
		simulator.setSodas(2, 11);
		assertEquals(11, simulator.countVolume(2));

		simulator.setSodas(3, 1);
		assertEquals(1, simulator.countVolume(3));
		
		verify(handler, times(4)).handleSodaChange();
	}

	/**
	 * Target hardware uses nickels, dimes, quarters and dollar bills.
	 */
	@Test public void testCurrencyTypes() {
		HardwareSimulator simulator = new HardwareSimulator();
		
		assertArrayEquals(new int[]{HardwareSimulator.NICKEL,HardwareSimulator.DIME,HardwareSimulator.QUARTER,HardwareSimulator.DOLLAR}, 
				simulator.listCurrencyTypes());
		assertEquals(5, simulator.getCurrencyValue(HardwareSimulator.NICKEL));
		assertEquals(10, simulator.getCurrencyValue(HardwareSimulator.DIME));
		assertEquals(25, simulator.getCurrencyValue(HardwareSimulator.QUARTER));
		assertEquals(100, simulator.getCurrencyValue(HardwareSimulator.DOLLAR));
	}

	/**
	 * Invalid coin type has value 0.
	 */
	@Test public void testBadCoinValue() {
		HardwareSimulator simulator = new HardwareSimulator();
		
		assertEquals(0, simulator.getCurrencyValue(-1));
	}
	
	/**
	 * Target hardware has four soda slots.
	 */
	@Test public void testFourSlots() {
		HardwareSimulator simulator = new HardwareSimulator();
		
		assertEquals(4, simulator.countSlots());
	}

	/**
	 * Inserting a dollar fires an event
	 */
	@Test public void testInsertDollar() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		simulator.insertCurrency(HardwareSimulator.DOLLAR);
		
		verify(handler, times(1)).handleCurrencyDeposit(HardwareSimulator.DOLLAR, 100);
	}
	
	/**
	 * Inserting a quarter fires an event
	 */
	@Test public void testInsertQuarter() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		simulator.insertCurrency(HardwareSimulator.QUARTER);
		
		verify(handler, times(1)).handleCurrencyDeposit(HardwareSimulator.QUARTER, 25);
	}
	
	/**
	 * Inserting a dime fires an event
	 */
	@Test public void testInsertDime() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		simulator.insertCurrency(HardwareSimulator.DIME);
		
		verify(handler, times(1)).handleCurrencyDeposit(HardwareSimulator.DIME, 10);
	}
	
	/**
	 * Inserting a nickel fires an event
	 */
	@Test public void testInsertNickel() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		simulator.insertCurrency(HardwareSimulator.NICKEL);
		
		verify(handler, times(1)).handleCurrencyDeposit(HardwareSimulator.NICKEL, 5);
	}

	/**
	 * Inserting a coin with an invalid type does nothing.
	 */
	@Test public void testInsertBadCoin() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		simulator.insertCurrency(-1);
		
		verify(handler, never()).handleCurrencyDeposit(anyInt(), anyInt());
	}
	
	/**
	 * Refunding a coin with an invalid type does nothing.
	 */
	@Test public void testRefundBadCoin() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		simulator.refund(-1);
		
		verify(handler, never()).handleCurrencyDeposit(anyInt(), anyInt());
	}

	/**
	 * Ejecting a quarter decreases quarter count by 1, and fires an event
	 */
	@Test public void testEjectQuarter() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		simulator.refund(HardwareSimulator.QUARTER);
		
		verify(handler, times(1)).handleCurrencyRefund(HardwareSimulator.QUARTER, 25);
	}
	
	/**
	 * Dispensing soda decreases count by one, and fires an event.
	 */
	@Test public void testDispenseSodas() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		simulator.setSodas(0, 5);
		assertEquals(5, simulator.countVolume(0));
		
		simulator.setSodas(1, 10);
		assertEquals(10, simulator.countVolume(1));
		
		simulator.setSodas(2, 11);
		assertEquals(11, simulator.countVolume(2));

		simulator.setSodas(3, 1);
		assertEquals(1, simulator.countVolume(3));
		
		verify(handler, times(4)).handleSodaChange();
		
		simulator.dispense(0, 0); // "Amount" is ignored for cans/bottles
		assertEquals(4, simulator.countVolume(0));
		
		simulator.dispense(1, 0); 
		assertEquals(9, simulator.countVolume(1));
		
		simulator.dispense(2, 0); 
		assertEquals(10, simulator.countVolume(2));

		simulator.dispense(3, 0);
		assertEquals(0, simulator.countVolume(3));
		
		verify(handler, times(1)).handleDispense(0, 0);
		verify(handler, times(1)).handleDispense(1, 0);
		verify(handler, times(1)).handleDispense(2, 0);
		verify(handler, times(1)).handleDispense(3, 0);
	}
	
	/**
	 * Dispensing soda when the slot is empty does nothing.
	 */
	@Test public void testDispenseEmptySodaSlot() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		simulator.setSodas(0, 0);
		assertEquals(0, simulator.countVolume(0));
		verify(handler, times(1)).handleSodaChange();
		reset(handler);
		
		simulator.dispense(0, 0); 
		assertEquals(0, simulator.countVolume(0));
		
		verify(handler, never()).handleDispense(anyInt(), anyInt());
		verify(handler, never()).handleSodaChange();
	}
	
}
