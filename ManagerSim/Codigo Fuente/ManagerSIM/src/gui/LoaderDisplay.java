package gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import control.Controller;

public class LoaderDisplay{

	private JTextField  nameField;
	private File fileSel;
	private Controller control;

	/**
	 * Constructor for LoaderFrame.
	 * @param control
	 */
	public LoaderDisplay(Controller control) {
		this.control = control;
	}

	/**
	 * Creates the output panel and relies on the controller for the loading of the simulation.
	 * @param mainPanel main panel
	 * @param optionPan option panel where it will place the new components
	 * @param messageLog message panel to show progress
	 */
	public void addOutput(JComponent mainPanel, JPanel optionPan, JFrame messageLog) {

		optionPan.removeAll();
		nameField = null;
		fileSel = null;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 0;
		c.insets = new Insets(8, 2, 8, 2);
		

		JButton browseInput =new JButton(new AbstractAction("Browse Input") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent event) { 
				selectFolder(optionPan) ;}
		});
		browseInput.setToolTipText("Selects the folder where the files are");

		optionPan.add(browseInput,c);

		//show file
		c.gridy = 1;
		c.gridwidth = 1;
		optionPan.add(new JLabel("Browsed directory"), c);
		
		c.gridx = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		nameField   = new JTextField(12) ;
		nameField.setEditable(false);
		nameField.setToolTipText("Browse file with the output");
		nameField.setName("outputFolder");

		optionPan.add(nameField,c);
		
		
		JCheckBox checkb = new JCheckBox("Add all output");
		checkb.setSelected(true);
		checkb.setToolTipText("Adds all the output files currently on the folder selected or only one.");
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 0;
		c.fill = GridBagConstraints.CENTER;
		optionPan.add(checkb,c);

		JButton load_met = new JButton("Execute");
		load_met.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if(!messageLog.isVisible())messageLog.setVisible(true);
				if(fileSel == null) {

					System.out.println("No file was selected, aborted action.");
					return;
				}
				boolean isSel = checkb.isSelected();

				System.out.println("Operation started. Loading simulations into the database, please wait.");
				load_met.setEnabled(false); //disable buttons
				browseInput.setEnabled(false);
				for(Component jc : mainPanel.getComponents()) {
					if(jc instanceof JButton) jc.setEnabled(false);
				}
				control.runOutput(fileSel, isSel);
			}
		});
		
		c.gridy = 3;
		optionPan.add(load_met,c);
		optionPan.updateUI();
	}

	/**
	 * Selects the output folder
	 * @param optionPan option panel used for showing new dialog.
	 */
	private void selectFolder(JPanel optionPan) {

		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setVisible(true) ;
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showOpenDialog(optionPan) == JFileChooser.APPROVE_OPTION) {
			File    file    = chooser.getSelectedFile() ;
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					System.err.println("Output folder could not be created.");
				}
			}

			if(file.exists() && file.isDirectory()){
				fileSel = file;
				nameField.setText(file.getName()) ;
			}

		}

	}
}
