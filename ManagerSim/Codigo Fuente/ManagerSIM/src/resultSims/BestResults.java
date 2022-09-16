package resultSims;


import databaseCon.DatabaseConnector;
import dataset.DatasetData;
import exception.IncompatibleParameterException;

public class BestResults {

	private DatasetData first;
	private DatasetData second;

	/**
	 * Constructor for BestResults.
	 */
	public BestResults() {} 

	/**
	 * Loads the datasets (real and best simulated for a specific model) and displays them.
	 * @param model model selected
	 * @param datasetUID the real dataset to use
	 * @throws IncompatibleParameterException
	 */
	public void loadBestResult(int model, int datasetUID) throws IncompatibleParameterException{

		DatabaseConnector db_ins = DatabaseConnector.getInstance();

		first = db_ins.loadDataset(datasetUID);

		second = db_ins.loadBestResult(first, model);

	}

	/**
	 * Loads the datasets (countermeasure and best simulated) and displays them.
	 * @param datasetUID the real dataset to use
	 * @param isDaily if the dataset is daily.
	 * @throws IncompatibleParameterException
	 */
	public void compareCountermeasures(int counterModel,int datasetUID) throws IncompatibleParameterException{

		DatabaseConnector db_ins = DatabaseConnector.getInstance();
		DatasetData real = db_ins.loadDataset(datasetUID);

		second = db_ins.loadBestResult(real, -1); //second has the configurationUID. Need the exec too.

		first = db_ins.loadCounter(real, counterModel, second.getDatasetUID());

	}

	/**
	 * Returns first dataset loaded
	 * @return dataset
	 */
	public DatasetData getFirst() {
		return first;
	}

	/**
	 * Returns second dataset loaded
	 * @return dataset
	 */
	public DatasetData getSecond() {
		return second;
	}


}
