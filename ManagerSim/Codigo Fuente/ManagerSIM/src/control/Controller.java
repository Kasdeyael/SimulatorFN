package control;


import java.io.File;
import java.util.HashMap;

import batch.BatchRuns;
import databaseCon.DatabaseConnector;
import dataset.DatasetInput;
import exception.IncompatibleParameterException;
import inputFiles.Parameters;
import inputFiles.SelectInput;
import metricCalc.MetricCalculator;
import resultSims.BestResults;
import resultSims.GraphChart;

public class Controller {

	private Parameters params;
	private MainView view;

	/**
	 * Constructor for Controller.
	 * @throws IncompatibleParameterException
	 */
	public Controller() throws IncompatibleParameterException {
		params = new Parameters();
		DatabaseConnector.getInstance().setParameters(params.getParameter("URL_conn"),params.getParameter("user"),params.getParameter("pass"));
	}

	/**
	 * Changes the parameter file
	 * @param params
	 * @throws IncompatibleParameterException
	 */
	public void changeParams(Parameters params) throws IncompatibleParameterException {
		this.params = params;
		view = null;
		DatabaseConnector.getInstance().setParameters(params.getParameter("URL_conn"),params.getParameter("user"),params.getParameter("pass"));

	}

	/**
	 * Sets the View
	 * @param view
	 */
	public void setView(MainView view) {
		this.view = view;
	}

	/**
	 * Runs batch executions.
	 * @param startOver if it's true, it resets the parameters for the simulator from the beginning.
	 */
	public void runBatch(boolean startOver) {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					BatchRuns run = new BatchRuns(params.getParameter("batch_file"),params.getParameter("batch_params"));

					run.startRuns(params.getParameter("batch_runner"),params.getParameter("batch_config"),startOver);


				} catch (IncompatibleParameterException e1) {
					System.err.println("ERROR: "+e1.getMessage());
				} finally {
					if(view!=null)view.finishOperation();
				}
			}


		});
		th.start();
	}

	/**
	 * Stores a dataset in the DB. 
	 * @param data dataset info to store
	 */
	public void runDataset(DatasetInput data) {

		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				try {


					DatabaseConnector db_ins = DatabaseConnector.getInstance();
					db_ins.storeDataset(data);

				} catch (IncompatibleParameterException e1) {

					System.err.println("ERROR: "+e1.getMessage());
				} finally {

					if(view!=null)view.finishOperation();
				}
			}

		});
		th.start();

	}

	/**
	 * Stores the output in the folder selected into the DB.
	 * @param fileSel the folder where the output files are
	 * @param runAll if it's true, it loads all the output files.
	 */
	public void runOutput(File fileSel, boolean runAll) {
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {

				int init = 0, finish = 0;
				boolean first= true;
				try {

					SelectInput input = new SelectInput(params.getParameter("directory"), params.getParameter("prefix"), params.getParameter("suffix"));
					DatabaseConnector db_ins = DatabaseConnector.getInstance();

					do {

						File paramFile = input.selectParameterFile(fileSel.getAbsolutePath());

						File sims = input.matchingSimulationFile(paramFile);

						if(!sims.exists()) throw new IncompatibleParameterException("Simulation file could not be found.");
						finish = db_ins.storeSimulations(paramFile, sims);

						input.storeFiles(paramFile, sims);
						if(first) {
							init=finish;
							first = false;
						}

					} while(input.hasNext(fileSel.getAbsolutePath()) && runAll); //if we have more and we want to add them all

					System.out.println("Configurations loaded from "+init+" to "+finish);
				} catch (IncompatibleParameterException e1) {

					System.err.println("ERROR: "+e1.getMessage());
				} finally {
					if(view!=null) {

						view.finishOperation();
					}

				}
			}

		});
		th.start();
	}

	/**
	 * Obtains the metrics for the configurations and loads them into the DB.
	 * @param datasetUID dataset selected for the runs
	 * @param configFrom number of configuration to start with
	 * @param configTo number of configuration to end with
	 */
	public void runMetrics(int datasetUID, int configFrom, int configTo) {
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					MetricCalculator metric = new MetricCalculator();
					metric.startCalc(datasetUID, configFrom, configTo);

				} catch (IncompatibleParameterException e1) {

					System.err.println("ERROR: "+e1.getMessage());
				} finally {
					if(view!=null)view.finishOperation();
				}
			}

		});
		th.start();
	}

	/**
	 * Displays the graph with the best results for a model and a dataset.
	 * @param model model to display
	 * @param datasetUID dataset used
	 */
	public void runGraph(int model, int datasetUID) {

		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					BestResults metric = new BestResults();
					if(model!= 4) metric.loadBestResult(model, datasetUID);
					else metric.compareCountermeasures(model,datasetUID);

					GraphChart gp = new GraphChart(metric.getFirst(), metric.getSecond());

					gp.generateGraph(model==4 ? true:false); 

				} catch (IncompatibleParameterException e1) {

					System.err.println("ERROR: "+e1.getMessage());
				} finally {
					if(view!=null)view.finishOperation();
				}
			}

		});
		th.start();

	}
	/**
	 * Checks the datasets loaded in the DB to allow its selection.
	 * @return map with the dataset name and its index.
	 */
	public HashMap<Integer,String> availableDataset() {

		DatabaseConnector db_ins = DatabaseConnector.getInstance();
		try{

			return db_ins.checkAvailableDatasets();

		} catch (IncompatibleParameterException e1) {

			System.err.println("ERROR: "+e1.getMessage());
		}
		return null;
	}

}
