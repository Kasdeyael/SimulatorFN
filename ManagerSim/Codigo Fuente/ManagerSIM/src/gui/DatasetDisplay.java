package gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import control.Controller;
import dataset.DatasetInput;


public class DatasetDisplay{

	private JTextField  fileNameField = null;
	private JTextField  dataName = null;
	private File fileSel = null;
	private Controller control;

	/**
	 * Constructor for DatasetFrame.
	 * @param control
	 */
	public DatasetDisplay(Controller control) {
		this.control = control;
	}

	/**
	 * Creates the dataset panel and relies on the controller for the dataset loading.
	 * @param mainPanel main panel
	 * @param optionPan option panel where it will place the new components
	 * @param messageLog message panel to show progress
	 */
	public void loadDataset(JComponent mainPanel, JPanel optionPan, JFrame messageLog) {

		optionPan.removeAll(); //remove all
		fileNameField = null;
		dataName = null;
		fileSel = null;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = new Insets(8, 2, 8, 2);
		

		JButton browseOutput =new JButton(new AbstractAction("Browse Dataset") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent event) { 
				selectDataset(optionPan) ;}
		});
		browseOutput.setToolTipText("Browse file with the dataset");

		optionPan.add(browseOutput,c);

		c.gridy = 1;
		c.gridwidth = 1;
		//show file
		optionPan.add(new JLabel("Browsed file"), c) ;
		fileNameField   = new JTextField(12) ;
		fileNameField.setEditable(false);
		fileNameField.setToolTipText("Shows the folder selected");
		fileNameField.setName("outputFolder");
		c.gridx = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		optionPan.add(fileNameField,c);
		
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.CENTER;
		optionPan.add(new JLabel("Name dataset"), c);
		
		dataName   = new JTextField(20) ;
		dataName.setEditable(true);
		dataName.setToolTipText("Name to give to the dataset in the DB");
		dataName.setName("nameDataset");
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		optionPan.add(dataName,c);

		c.gridx = 0;
		c.gridy = 3;
		JCheckBox checkbox = new JCheckBox("Is dataset daily");
		checkbox.setSelected(true);
		checkbox.setToolTipText("Check this option if the dataset is parsed by days, not hours.");
		optionPan.add(checkbox,c);
		
		JCheckBox checkbox2 = new JCheckBox("Is dataset smooth");
		checkbox2.setSelected(false);
		checkbox2.setToolTipText("Check this option if the dataset points have been smoothed.");
		c.gridx = 1;
		optionPan.add(checkbox2,c);
		
		JButton run = new JButton("Execute");
		run.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if(!messageLog.isVisible())messageLog.setVisible(true);

				if(fileSel == null || dataName.getText().isEmpty()) {
					System.out.println("No file was selected or name was not set, aborted action.");
					return;
				}
				System.out.println("Operation started. Loading dataset into the database.");
				run.setEnabled(false); //disable buttons
				browseOutput.setEnabled(false);
				for(Component jc : mainPanel.getComponents()) {
					if(jc instanceof JButton) jc.setEnabled(false);
				}

				DatasetInput data = new DatasetInput(fileSel.getAbsolutePath(),dataName.getText(), checkbox.isSelected(),checkbox2.isSelected());

				control.runDataset(data);

			}
		});

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.CENTER;
		optionPan.add(run,c);

		optionPan.updateUI();
	}

	/**
	 * Selects the dataset file. Only txt allowed
	 * @param optionPan option panel used for showing new dialog.
	 */
	private void selectDataset(JPanel optionPan) {
		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileNameExtensionFilter("TXT Files", "txt"));
		chooser.setVisible(true) ;
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showOpenDialog(optionPan) == JFileChooser.APPROVE_OPTION) {
			File    file    = chooser.getSelectedFile();
			if(file.exists() && file.getName().endsWith(".txt")) {
				fileSel = file;
				fileNameField.setText(file.getName()) ;
			}

		}

	}
}
