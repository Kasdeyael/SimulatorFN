package gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import control.Controller;

public class BatchDisplay{

	private Controller control;

	/**
	 * Constructor for BatchFrame
	 * @param control
	 */
	public BatchDisplay(Controller control) {
		this.control = control;
	}
	/**
	 * Creates the batch panel and relies on the controller for the batch run.
	 * @param mainPanel main panel
	 * @param optionPan option panel where it will place the new components
	 * @param messageLog message panel to show progress
	 */
	public void createBatch(JComponent mainPanel, JPanel optionPan, JFrame messageLog) {

		optionPan.removeAll(); //remove all
		GridBagConstraints c = new GridBagConstraints();
		c.anchor= GridBagConstraints.CENTER; //Layout
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(8, 2, 8, 2);
		
		JCheckBox checkb = new JCheckBox("Start over");

		checkb.setToolTipText("Check to regenerate the file. Recommended unless last batch execution was interrupted prematurely.");
		optionPan.add(checkb,c);
		//run button
		c.gridy = 1;
		
		JButton load_met = new JButton("Execute");

		load_met.addActionListener(new ActionListener() { //run batch

			@Override
			public void actionPerformed(ActionEvent e) {

				load_met.setEnabled(false); //disable buttons
				for(Component jc : mainPanel.getComponents()) {
					if(jc instanceof JButton) jc.setEnabled(false);
				}

				if(!messageLog.isVisible())messageLog.setVisible(true);

				System.out.println("Operation started. Executing batch runs, please wait.");

				control.runBatch(checkb.isSelected());

			}
		});
		
		optionPan.add(load_met,c);
		optionPan.updateUI();
	}


}
