package gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import control.Controller;


public class MetricDisplay{

	private Controller control;

	/**
	 * Constructor for MetricFrame.
	 * @param control
	 */
	public MetricDisplay(Controller control) {
		this.control = control;
	}


	/**
	 * Creates the metric panel and takes care of loading the metrics into the DB.
	 * @param mainPanel 
	 * @param optionPan option panel where it will place the new components
	 * @param messageLog message panel (console), to show progress
	 */
	public void loadMetricsSel(JComponent mainPanel, JPanel optionPan, JFrame messageLog) {
		//choose file parameters
		optionPan.removeAll(); //remove all

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		
		c.insets = new Insets(8, 2, 8, 2);

		HashMap<Integer, String> map = control.availableDataset();

		DefaultComboBoxModel<String> panelName = new DefaultComboBoxModel<String>();

		if(map ==null|| map.isEmpty()) {
			if(!messageLog.isVisible())messageLog.setVisible(true); //set visible if not before
			System.err.println("Datasets could not be retrieved. Make sure the database is running and there's datasets loaded");
			return;
		}
		for(Integer val : map.keySet()) {
			panelName.addElement(val+" "+map.get(val));
		}

		optionPan.add(new JLabel("Dataset"), c) ;
		c.gridwidth = 2;
		c.gridx = 1;
		JComboBox<String> listCombo = new JComboBox<String>(panelName);    

		listCombo.setSelectedIndex(0); //default is the first
		JScrollPane listComboScrollPane = new JScrollPane(listCombo);
		c.fill = GridBagConstraints.HORIZONTAL;
		optionPan.add(listComboScrollPane,c);

		
		//config from
		JFormattedTextField range = new JFormattedTextField(Integer.valueOf(1));
		JLabel lab1 = new JLabel("Configuration from");

		range.setColumns(12);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;

		optionPan.add(lab1,c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.gridwidth = 3;
		optionPan.add(range,c);
		
		
		//config to
		JFormattedTextField range2 = new JFormattedTextField(Integer.valueOf(1));
		JLabel lab2 = new JLabel("Configuration to");
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;

		optionPan.add(lab2,c);
		c.gridwidth = 2;
		c.gridx = 1;
		range2.setColumns(12);
		optionPan.add(range2,c);
		//execute

		JButton load_met = new JButton("Execute");
		load_met.addActionListener(new ActionListener() { //load metrics

			@Override
			public void actionPerformed(ActionEvent e) {

				if(!messageLog.isVisible())messageLog.setVisible(true); //set visible if not before
				String dataset = panelName.getElementAt(listCombo.getSelectedIndex());
				String init = dataset.split(" ")[0]; //separate by space and get first
				int datSel = Integer.parseInt(init);

				if(datSel < 1 || (Integer)range.getValue() < 1 || (Integer)range2.getValue() < 1) {
					System.out.println("No fields can be zero or less");
					return;
				} else if((Integer)range.getValue() > (Integer)range2.getValue()) {
					System.out.println("Configuration from can't be higher than configuration to");
					return;
				}



				int configFrom = ((Integer)range.getValue());
				int configTo = ((Integer)range2.getValue());

				System.out.println("Operation started. Calculating metrics for the simulations, please wait.");

				load_met.setEnabled(false); //disable buttons
				for(Component jc : mainPanel.getComponents()) {
					if(jc instanceof JButton) jc.setEnabled(false);
				}

				control.runMetrics(datSel, configFrom, configTo);

			}
		});

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 3;
		optionPan.add(load_met,c);
		optionPan.updateUI();
	}
}
