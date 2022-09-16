package databaseCon;

import java.io.File;
import java.util.HashMap;

import dataset.DatasetData;
import dataset.DatasetInput;
import exception.IncompatibleParameterException;
import metricCalc.CurrentExec;
import metricCalc.ErrorCalc;

public class DatabaseConnector {

	private static DatabaseConnector db_instance = null;
	private LoadDataset outData = null;
	private StoreSimsDB str = null;
	private StoreDatasetDB dataIn = null;
	private StoreErrorsDB res = null;
	private ConnectionMgr con = null;

	/**
	 * Constructor for DatabaseConnector.
	 */
	private DatabaseConnector() {
	}

	/**
	 * Returns the instance of DatabaseConnector. Creates one if it doesn't exist yet.
	 * @return this object
	 */
	public static DatabaseConnector getInstance() {
		if(db_instance ==null) {
			db_instance = new DatabaseConnector();
		}
		return db_instance;
	}

	/**
	 * Sets the DB connection parameters.
	 * @param url 
	 * @param usr
	 * @param pass
	 * @throws IncompatibleParameterException
	 */
	public void setParameters(String url, String usr, String pass) throws IncompatibleParameterException {
		con = new ConnectionMgr(url, usr, pass);

	}

	/**
	 * Stores a simulation in DB.
	 * @param paramFile parameters
	 * @param simFile simulation results
	 * @return configuration number loaded
	 * @throws IncompatibleParameterException
	 */
	public int storeSimulations(File paramFile, File simFile) throws IncompatibleParameterException {
		if(str == null) str = new StoreSimsDB(con);
		return str.loadFilesDB(paramFile, simFile);
	}

	/**
	 * Stores a dataset in DB.
	 * @param data dataset info
	 * @throws IncompatibleParameterException
	 */
	public void storeDataset(DatasetInput data) throws IncompatibleParameterException {
		if(dataIn==null) dataIn = new StoreDatasetDB(con);
		dataIn.loadDataset(data);
	}

	/**
	 * Stores error in DB.
	 * @param error error info
	 * @throws IncompatibleParameterException
	 */
	public void storeError(ErrorCalc error) throws IncompatibleParameterException {
		if(res == null) res = new StoreErrorsDB(con);
		res.loadRMSE(error);
	}

	/**
	 * Loads a dataset in memory for use.
	 * @param datasetUID dataset to load
	 * @return real dataset
	 * @throws IncompatibleParameterException
	 */
	public DatasetData loadDataset(int datasetUID) throws IncompatibleParameterException {
		if(outData==null) outData = new LoadDataset(con);
		return outData.loadDataset(datasetUID);

	}

	/**
	 * Loads a simulation in memory.
	 * @param real real dataset
	 * @param datasetUID dataset to use
	 * @return simulation dataset
	 * @throws IncompatibleParameterException
	 */
	public DatasetData loadBestResult(DatasetData real, int model) throws IncompatibleParameterException {
		if(outData==null) outData = new LoadDataset(con);
		return outData.loadBestResult(real, model);	
	}

	/**
	 * Loads specific simulation in memory. 
	 * @param configurationUID 
	 * @param dataset real dataset
	 * @param exec execution
	 * @return simulation dataset
	 * @throws IncompatibleParameterException
	 */
	public DatasetData loadSimulation(int configurationUID, DatasetData dataset, int exec) throws IncompatibleParameterException {
		if(outData==null) outData = new LoadDataset(con);
		return outData.loadSimulation(configurationUID, dataset, exec);
	}

	/**
	 * Loads countermeasures in the database.
	 * @param dataset to compare
	 * @param model model for countermeasures
	 * @param configurationLoad configuration used
	 * @return simulation dataset
	 * @throws IncompatibleParameterException
	 */
	public DatasetData loadCounter(DatasetData dataset, int model, int configurationLoad) throws IncompatibleParameterException {
		if(outData==null) outData = new LoadDataset(con);
		return outData.loadSimulationModel(dataset, model, configurationLoad);
	}

	/**
	 * Checks if a configuration has results loaded for the metrics. If they are incomplete, they get deleted. If 
	 * it's complete, it doesn't reload the metrics.
	 * @param configurationUID
	 * @param datasetUID dataset selected
	 * @param current current execution with its data
	 * @return 0 if the results are loaded, 1 if missing values or none, -1 if the configuration is missing
	 * @throws IncompatibleParameterException
	 */
	public int checkConfiguration(int configurationUID, int datasetUID, CurrentExec current) throws IncompatibleParameterException {
		// TODO Auto-generated method stub
		if(res==null) res = new StoreErrorsDB(con);
		return res.checkConfiguration(configurationUID, datasetUID, current);
	}

	/**
	 * Checks the real datasets loaded into the DB to select in the view.
	 * @return map with dataset name and its index.
	 * @throws IncompatibleParameterException
	 */
	public HashMap<Integer,String> checkAvailableDatasets() throws IncompatibleParameterException{
		if(outData==null) outData = new LoadDataset(con);
		return outData.checkAvailableDatasets();
	}

}
