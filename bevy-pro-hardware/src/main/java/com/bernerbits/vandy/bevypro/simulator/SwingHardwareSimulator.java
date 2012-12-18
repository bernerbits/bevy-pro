package com.bernerbits.vandy.bevypro.simulator;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.bernerbits.vandy.bevypro.HardwareEventAdapter;
import com.bernerbits.vandy.bevypro.HardwareEventHandler;

/**
 * Swing-based simulation view for an instance of HardwareSimulationService.
 * 
 * Swing is chosen here due to the simplicity of rapidly creating a UI for internal testing.
 * This simulator can be run in the absence of any vending machine hardware to test the 
 * customer interface.
 * 
 * Accepts HardwareSimulationService (an extended HardwareService interface with
 * additional methods to simulate hardware-side actions) as a constructor parameter. This
 * way, the swing view remains decoupled from the implementation details of the simulator,
 * which can be modified and reimplemented independently.
 * 
 * @author derekberner
 *
 */
public class SwingHardwareSimulator extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	protected final JLabel[] currencyLabels;
	protected final JLabel[] sodaLabels;
	protected final JSpinner[] sodaSpinners;
	protected final JButton[] currencyInsertButtons;
	protected final JButton[] currencyEjectButtons;
	protected final JButton[] sodaButtons;
	
	/**
	 * @param hardware Implementation of MicrocontrollerSimulation that this UI wraps. 
	 */
	public SwingHardwareSimulator(final HardwareSimulationService hardware) {
		// Main window
		super("Bevy-Pro Simulator");
		setSize(1000,300);
		getContentPane().setLayout(new BorderLayout(2,2));
		
		JPanel mainPanel = new JPanel();
		getContentPane().add(mainPanel,BorderLayout.CENTER);
		
		BoxLayout mainLayout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
		mainPanel.setLayout(mainLayout);
		
		JPanel topPanel = new JPanel();
		BoxLayout topLayout = new BoxLayout(topPanel, BoxLayout.X_AXIS);
		topPanel.setLayout(topLayout);
		mainPanel.add(topPanel);
		
		// Currency Panel
		{
			JPanel currencyPanel = new JPanel();
			currencyPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Coin/Bill slots"));
			
			int currencyCount = hardware.listCurrencyTypes().length;
			GridLayout currencyGrid = new GridLayout(3,currencyCount,2,2);
			currencyPanel.setLayout(currencyGrid);
			
			currencyLabels = new JLabel[currencyCount];
			currencyInsertButtons = new JButton[currencyCount];
			currencyEjectButtons = new JButton[currencyCount];
			for(int i = 0; i < currencyCount; i++) {
				final int currencyType = hardware.listCurrencyTypes()[i];
				final JLabel label = new JLabel("" + hardware.getCurrencyValue(currencyType));
				final JButton insertButton = new JButton("Insert"); 
				final JButton ejectButton = new JButton("Eject"); 
				
				insertButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						hardware.insertCurrency(currencyType);
					}
				});
				
				ejectButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						hardware.refund(currencyType);
					}
				});
				
				currencyLabels[i] = label;
				currencyInsertButtons[i] = insertButton;
				currencyEjectButtons[i] = ejectButton;
			}
			
			for(JLabel label : currencyLabels) {
				currencyPanel.add(label);
			}
			for(JButton button: currencyInsertButtons) {
				currencyPanel.add(button);
			}
			for(JButton button: currencyEjectButtons) {
				currencyPanel.add(button);
			}
			
			topPanel.add(currencyPanel);
		}
		
		// Beverage Panel
		{
			JPanel sodaPanel = new JPanel();
			sodaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Beverage Slots"));
			
			int sodaCount = hardware.countSlots();
			GridLayout sodaGrid = new GridLayout(3,sodaCount,2,2);
			sodaPanel.setLayout(sodaGrid);
			
			sodaLabels = new JLabel[sodaCount];
			sodaSpinners = new JSpinner[sodaCount];
			sodaButtons = new JButton[sodaCount];
			for(int i = 0; i < sodaCount; i++) {
				final int slot = i;
				final JLabel label = new JLabel("Slot " + slot);
				final JSpinner spinner = new JSpinner(new SpinnerNumberModel(hardware.countVolume(slot),0,Integer.MAX_VALUE,1));
				
				final JButton button = new JButton("Dispense");
				
				spinner.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						hardware.setSodas(slot, (Integer)spinner.getValue());
					}
				});
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						hardware.dispense(slot, 1);
					}
				});
				hardware.registerHardwareEventHandler(new HardwareEventAdapter() {
					@Override
					public void handleDispense(int dispenseSlot, int amount) {
						if(dispenseSlot == slot) {
							spinner.setValue(hardware.countVolume(slot));
						}
					}
				});
				sodaLabels[i] = label;
				sodaSpinners[i] = spinner;
				sodaButtons[i] = button;
			}
			
			for(JLabel label : sodaLabels) {
				sodaPanel.add(label);
			}
			for(JSpinner spinner: sodaSpinners) {
				sodaPanel.add(spinner);
			}
			for(JButton button: sodaButtons) {
				sodaPanel.add(button);
			}
			topPanel.add(sodaPanel);
		}
		
		// Event Panel
		{
			JPanel eventPanel = new JPanel();
			eventPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Hardware Events"));
			GridLayout eventGrid = new GridLayout(1,1,2,2);
			eventPanel.setLayout(eventGrid);
			
			final JTextArea eventLogArea = new JTextArea();
			
			hardware.registerHardwareEventHandler(new HardwareEventHandler() {
				
				@Override
				public void handleSodaChange() {
					StringWriter sw = new StringWriter();
					sw.append("Beverage amounts changed: ");
					for(int i = 0; i < hardware.countSlots(); i++) {
						sw.append("Slot/tap " + i + ": " + hardware.countVolume(i) + " units, ");
					}	
					sw.append("\n");
					sw.flush();
					eventLogArea.append(sw.toString());
				}
				
				@Override
				public void handleDispense(int slot, int amount) {
					eventLogArea.append("Slot/tap " + slot + " dispensed " + amount + " unit\n");
				}
				
				@Override
				public void handleCurrencyRefund(int type, int value) {
					eventLogArea.append("Refunded coin/bill worth " + value + "\n");
				}
				
				@Override
				public void handleCurrencyDeposit(int type, int value) {
					eventLogArea.append("Deposited coin/bill worth " + value + "\n");
				}
				
			});
			
			JScrollPane scroll = new JScrollPane(eventLogArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			eventPanel.add(scroll);
			mainPanel.add(eventPanel);
		}
	}
	
}
