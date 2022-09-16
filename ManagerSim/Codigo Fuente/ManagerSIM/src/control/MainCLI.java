package control;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import dataset.DatasetInput;
import exception.IncompatibleParameterException;
import inputFiles.Parameters;




public class MainCLI implements MainView {

	/**
	 * Parses options and performs the ones selected.
	 */
	public MainCLI(Controller control, String[] args, Options options) {
		Parameters params;
		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cmd = parser.parse(options, args);
			if(cmd.hasOption("help")) {

				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("help", options);
			}
			if(cmd.hasOption("config")) {

				params = new Parameters(cmd.getOptionValue("config"));
				control.changeParams(params);


			} else params = new Parameters();

			if(cmd.hasOption("run_Batch")) {
				boolean startOver =(!(cmd.getOptionValue("run_Batch") == null) && cmd.getOptionValue("run_Batch").toUpperCase().trim().equals("Y"));
				control.runBatch(startOver);

			}

			if(cmd.hasOption("output")) {

				File paramFile = new File(params.getParameter("output_folder"));
				control.runOutput(paramFile, (cmd.getOptionValue("output") != null) && cmd.getOptionValue("output").toLowerCase().trim().equals("all"));

			}
			if(cmd.hasOption("dataset")) {

				String[] data = cmd.getOptionValues("dataset");
				boolean daily = data[2].toUpperCase().equals("Y")? true : false;
				boolean smooth = data[3].toUpperCase().equals("Y")? true : false;
				control.runDataset(new DatasetInput(data[1], data[0], daily, smooth));

			}

			if(cmd.hasOption("metrics")) {
				String[] data = cmd.getOptionValues("metrics");

				int dataset = Integer.parseInt(data[0]);
				int configFrom = Integer.parseInt(data[1]);
				int configTo = Integer.parseInt(data[2]);
				control.runMetrics(dataset, configFrom, configTo);

			}

			if(cmd.hasOption("graph")) {

				String[] data = cmd.getOptionValues("graph");
				control.runGraph(Integer.parseInt(data[0]), Integer.parseInt(data[1]));

			}

		} catch (ParseException | IncompatibleParameterException e) {

			System.err.println("Error while executing: "+e.getMessage());
		}
	}


	public void finishOperation() {
		System.out.println("Operation finished");
	}


}
