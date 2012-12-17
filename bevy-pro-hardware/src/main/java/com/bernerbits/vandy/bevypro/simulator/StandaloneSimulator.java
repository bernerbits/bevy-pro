package com.bernerbits.vandy.bevypro.simulator;

import javax.swing.JFrame;

/**
 * Standalone application running the Swing hardware simulator.
 * 
 * @author derekberner
 *
 */
public class StandaloneSimulator {

	public static void main(String[] args) {
		JFrame frame = new SwingHardwareSimulator(new HardwareSimulator());
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
