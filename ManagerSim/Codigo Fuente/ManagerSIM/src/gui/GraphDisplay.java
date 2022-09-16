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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import control.Controller;

public class GraphDisplay{

	private Controller control;

	/**
	 * Constructor for GraphFrame.
	 * @param control
	 */
	public GraphDisplay(Controller control) {
		this.control = control;
	}

	/**
	 * Creates the graph panel and relies on the controller for the visualization.
	 * @param mainPanel main panel
	 * @param optionPan option panel where it will place the new components
	 * @param messageLog message panel to show progress
	 */
	public void showResults(JComponent mainPanel, JPanel optionPan, JFrame messageLog) {

		optionPan.removeAll();

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.insets = new Insets(8, 2, 8, 2);
		
		optionPan.add(new JLabel("Model"), c);
		

		DefaultComboBoxModel<String> panelName = new DefaultComboBoxModel<String>();

		panelName.addElement("Model 1");
		panelName.addElement("Model 2");
		panelName.addElement("Model 3");
		panelName.addElement("Countermeasures");

		JComboBox<String> listCombo = new JComboBox<String>(panelName); 
		listCombo.setToolTipText("Select model for the simulation");
		listCombo.setSelectedIndex(0);
		JScrollPane listComboScrollPane = new JScrollPane(listCombo);
		c.gridwidth = 2;
		c.gridx = 1;
		optionPan.add(listComboScrollPane,c);

		HashMap<Integer, String> map = control.availableDataset(); //datasets to show

		DefaultComboBoxModel<String> panel = new DefaultComboBoxModel<String>();

		if(map ==null || map.isEmpty()) {
			if(!messageLog.isVisible()) messageLog.setVisible(true); //set visible if not before
			System.err.println("Datasets could not be retrieved. Make sure the database is running and there's datasets loaded");
			return;
		}

		for(Integer val : map.keySet()) {
			panel.addElement(val+" "+map.get(val));
		}

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		optionPan.add(new JLabel("Dataset"), c) ;
		c.gridwidth = 2;
		c.gridx = 1;
		
		JComboBox<String> list2 = new JComboBox<String>(panel);  
		list2.setToolTipText("Select dataset to compare the results");
		list2.setSelectedIndex(0);
		JScrollPane listScroll = new JScrollPane(list2);
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		optionPan.add(listScroll,c);

		
		JButton load_met = new JButton("Show graph");
		load_met.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if(!messageLog.isVisible())messageLog.setVisible(true);
				String dataset = panel.getElementAt(list2.getSelectedIndex());
				String init = dataset.split(" ")[0];
				int datSel = Integer.parseInt(init);
				if(datSel < 1) {
					System.out.println("Dataset selected not valid. Aborting action");
					return;
				}


				int model = 1+ listCombo.getSelectedIndex(); //based on index, model 1 to 4
				System.out.println("Operation started. Showing model "+model+".");

				load_met.setEnabled(false); //disable buttons
				for(Component jc : mainPanel.getComponents()) {
					if(jc instanceof JButton) jc.setEnabled(false);
				}

				control.runGraph(model, datSel);


			}
		});
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 0;
		c.gridx = 0;
		c.gridy = 2;
		optionPan.add(load_met,c);

		optionPan.updateUI();
	}
}
