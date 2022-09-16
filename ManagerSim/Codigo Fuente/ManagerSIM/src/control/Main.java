package control;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import exception.IncompatibleParameterException;
import gui.MainFrame;



public class Main {


	/**
	 * GUI main
	 */
	public static void mainGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			Controller control = new Controller();
			MainFrame view = new MainFrame(control);
			view.setTitle("ManagerSim");
			control.setView(view);
			view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			view.getContentPane().add(view.getPanel());
			view.setPreferredSize(new Dimension(400,250));
			view.pack();       
			view.setLocationRelativeTo(null);
			view.setVisible(true);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}


	}


	/**
	 * CLI 
	 * @param args
	 * @param options
	 */
	public static void mainCLI(String[] args, Options options) {

		Controller control;
		try {
			control = new Controller();
			MainCLI view = new MainCLI(control,args,options);
			control.setView(view);
		} catch (IncompatibleParameterException ex) {

			System.err.println(ex.getMessage());
		}


	}

	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {


		Options options= createOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
			if(cmd.hasOption("gui")) {
				mainGUI();
			}else
				mainCLI(args,options);
		} catch (ParseException ex) {
			System.err.println("ERROR: Could not parse input.");
		}



	}

	/**
	 * Creates the options for the CLI.
	 * @return options object.
	 */
	private static Options createOptions() {
		//create options
		Options options = new Options();	
		//add options
		options.addOption("gui", false, "Displays GUI or CLI.");
		options.addOption("h","help", false, "Shows the possible options.");
		options.addOption("c","config", true, "Select the config file to use. If absent, it loads the default one.");
		Option loadingOut = new Option("o","output", true, "Load the simulation output specified by preffix and suffix parameters in the config file. If argument provided is 'all', it loads all the outputs. Otherwise, only loads the oldest.");
		loadingOut.setOptionalArg(true);
		options.addOption(loadingOut);
		Option dataset = new Option("d","dataset", true, "Loads a dataset specified by the name of the file into the DB and specifies if it's daily/smooth or not (Y/N). 4 arguments separated by comma: name,file,isDaily,isSmooth");
		dataset.setArgs(4);
		dataset.setValueSeparator(',');
		options.addOption(dataset);

		Option metr = new Option("m","metrics", true, "Obtains the metrics (RMSE) for each run not calculated yet and loads them into the DB. 3 arguments: dataset to use, first configuration, last configuration to calculate");
		metr.setArgs(3);
		metr.setValueSeparator(',');
		options.addOption(metr);

		options.addOption("r","run_Batch", true, "Runs Batch exec editing the parameter file, waiting for it to finish until all params executed. Needs an argument to know if it starts again the simulation (Y).");

		Option graph = new Option("g","graph", true, "Gets the best results per dataset and model and displays the graph. 2 arguments: model, dataset");
		graph.setArgs(2);
		graph.setValueSeparator(',');
		options.addOption(graph);

		return options;
	}


}
