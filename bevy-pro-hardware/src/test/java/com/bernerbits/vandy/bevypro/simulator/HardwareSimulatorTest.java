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
	 * Currency amount can be set, and fires an event when modified.
	 */
	@Test public void testSetCurrency() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		simulator.setCurrency(HardwareSimulator.QUARTER, 100);
		assertEquals(100, simulator.countCurrency(HardwareSimulator.QUARTER));
		
		verify(handler, times(1)).handleCurrencyChange();
		verify(handler, never()).handleCurrencyDeposit(anyInt(), anyInt());
	}

	/**
	 * Target hardware uses only quarters.
	 */
	@Test public void testOnlyQuarters() {
		HardwareSimulator simulator = new HardwareSimulator();
		
		assertArrayEquals(new int[]{HardwareSimulator.QUARTER}, simulator.listCurrencyTypes());
		assertEquals(25, simulator.getCurrencyValue(HardwareSimulator.QUARTER));
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
	 * Inserting a quarter increases quarter count by 1, and fires an event
	 */
	@Test public void testInsertQuarter() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		assertEquals(0,simulator.countCurrency(HardwareSimulator.QUARTER));
		
		simulator.insertCurrency(HardwareSimulator.QUARTER);
		
		assertEquals(1,simulator.countCurrency(HardwareSimulator.QUARTER));
		verify(handler, times(1)).handleCurrencyDeposit(HardwareSimulator.QUARTER, 25);
		verify(handler, never()).handleCurrencyChange();
	}
	
	/**
	 * Inserting a coin with an invalid type does nothing.
	 */
	@Test public void testInsertBadCoin() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		assertEquals(0,simulator.countCurrency(-1));
		
		simulator.insertCurrency(-1);
		
		assertEquals(0,simulator.countCurrency(-1));
		verify(handler, never()).handleCurrencyDeposit(anyInt(), anyInt());
		verify(handler, never()).handleCurrencyChange();
	}
	
	/**
	 * Inserting a coin with an invalid type does nothing.
	 */
	@Test public void testRefundBadCoin() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		simulator.setCurrency(-1, 10);
		verify(handler, never()).handleCurrencyChange();
		assertEquals(0,simulator.countCurrency(-1));
		
		simulator.refund(-1);
		
		assertEquals(0,simulator.countCurrency(-1));
		verify(handler, never()).handleCurrencyDeposit(anyInt(), anyInt());
		verify(handler, never()).handleCurrencyChange();
	}
	
	/**
	 * Setting coin count on an invalid type does nothing.
	 */
	@Test public void testSetBadCoin() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		assertEquals(0,simulator.countCurrency(-1));
		
		simulator.setCurrency(-1,100);
		
		assertEquals(0,simulator.countCurrency(-1));
		verify(handler, never()).handleCurrencyDeposit(anyInt(), anyInt());
		verify(handler, never()).handleCurrencyChange();
	}

	/**
	 * Ejecting a quarter decreases quarter count by 1, and fires an event
	 */
	@Test public void testEjectQuarter() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		assertEquals(0,simulator.countCurrency(HardwareSimulator.QUARTER));
		
		simulator.insertCurrency(HardwareSimulator.QUARTER);
		
		assertEquals(1,simulator.countCurrency(HardwareSimulator.QUARTER));
		verify(handler, times(1)).handleCurrencyDeposit(HardwareSimulator.QUARTER, 25);
		
		simulator.refund(HardwareSimulator.QUARTER);
		
		assertEquals(0,simulator.countCurrency(HardwareSimulator.QUARTER));
		verify(handler, times(1)).handleCurrencyRefund(HardwareSimulator.QUARTER, 25);
		
		verify(handler, never()).handleCurrencyChange();
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
	
	/**
	 * Refunding a quarter when there are no quarters does nothing.
	 */
	@Test public void testRefundEmptyQuarters() {
		HardwareSimulator simulator = new HardwareSimulator();
		HardwareEventHandler handler = mock(HardwareEventHandler.class);
		simulator.registerHardwareEventHandler(handler);
		
		simulator.setCurrency(HardwareSimulator.QUARTER, 0);
		assertEquals(0, simulator.countVolume(0));
		verify(handler, times(1)).handleCurrencyChange();
		reset(handler);
		
		simulator.refund(HardwareSimulator.QUARTER); 
		assertEquals(0, simulator.countVolume(0));
		
		verify(handler, never()).handleCurrencyRefund(anyInt(), anyInt());
		verify(handler, never()).handleCurrencyChange();
	}
}
