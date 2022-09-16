package databaseCon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import dataset.DatasetData;
import exception.IncompatibleParameterException;

public class LoadDataset {

	private ConnectionMgr connection;
	private int configUID;
	private int exec;

	/**
	 * Constructor for OutputDataset
	 * @param con connection manager for DB.
	 */
	public LoadDataset(ConnectionMgr con) {
		this.connection = con;
	}


	/**
	 * Loads a dataset into memory.
	 * @param datasetUID
	 * @return the dataset
	 * @throws IncompatibleParameterException if load failed
	 */
	public DatasetData loadDataset(int datasetUID) throws IncompatibleParameterException {

		Connection con = null;
		DatasetData data = new DatasetData(datasetUID);
		try {
			con = connection.getConnection();

			String sql = "SELECT * FROM real_dataset_spread WHERE datasetUID = "+datasetUID+" ORDER BY timestamp ASC";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			if(rs.next()) {

				double endorserL = rs.getDouble("endorserLabel");
				if(rs.wasNull()) { //endorser label is null, loads spreading

					double userSpread = rs.getDouble("totalSpreading");
					data.addPercentage(userSpread);
					while(rs.next()) {

						userSpread = rs.getDouble("totalSpreading");
						data.addPercentage(userSpread);
					}

				} else {
					double denierL = rs.getDouble("denierLabel");
					data.addPercentages(endorserL, denierL);
					while(rs.next()) {
						endorserL = rs.getDouble("endorserLabel");
						denierL = rs.getDouble("denierLabel");
						data.addPercentages(endorserL, denierL);
					}

				}

			} else {
				connection.closeConnection();
				throw new IncompatibleParameterException("Dataset could not be found");
			}

			sql = "SELECT * FROM dataset_information WHERE datasetUID = "+datasetUID;
			st = con.createStatement();
			rs = st.executeQuery(sql);
			if(rs.next()) {
				data.setDaily(rs.getBoolean("isDaily"));
				data.setSmooth(rs.getBoolean("isSmoothed"));
			} else {

				connection.closeConnection();
				throw new IncompatibleParameterException("Daily and Smooth status of dataset could not be found.");
			}
		} catch ( SQLException ex) {
			connection.closeConnection();
			throw new IncompatibleParameterException("Could not load dataset");

		}
		connection.closeConnection();
		return data;
	}


	/**
	 * Loads the best result for a model and a datasetUID. If no result was found, throws error.
	 * @param real Dataset
	 * @param model number (1-3), if model < 1, it loads best result globally.
	 * @return simulation with best model loaded
	 * @throws IncompatibleParameterException
	 */
	public DatasetData loadBestResult(DatasetData real, int model) throws IncompatibleParameterException {
		configUID = -1;
		exec = -1;
		getResult(model,real.getDatasetUID());

		return loadSimulation(configUID, real, exec);
	}



	/**
	 * Checks if said model for a specific dataset has results loaded and gets its configuration info.
	 * @param model model to compare
	 * @param datasetUID dataset used for the calculations
	 * @return true if there are results, false if there are not
	 * @throws IncompatibleParameterException
	 */
	private void getResult(int model, int datasetUID)  throws IncompatibleParameterException{
		Connection con = null;
		try {
			con = connection.getConnection();

			String sql;
			if(model>0) sql = "SELECT rmse_results.configurationUID, rmse_results.execution FROM rmse_results INNER JOIN "
					+ "data_configuration_by_model ON data_configuration_by_model.configurationUID = rmse_results.configurationUID WHERE "
					+ "data_configuration_by_model.modelType="+model+" AND datasetUID = "+datasetUID+" ORDER BY RMSE ASC LIMIT 1";
			else sql = "SELECT rmse_results.configurationUID, rmse_results.execution FROM rmse_results INNER JOIN "
					+ "data_configuration_by_model ON data_configuration_by_model.configurationUID = rmse_results.configurationUID WHERE "
					+ "data_configuration_by_model.modelType=3 AND datasetUID = "+datasetUID+" ORDER BY RMSE ASC LIMIT 1";

			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if(rs.next()) {

				configUID = rs.getInt("configurationUID");
				exec = rs.getInt("execution");

			} else {
				connection.closeConnection();
				throw new IncompatibleParameterException("No results loaded for said configuration and dataset");
			}

		}catch (SQLException ex) {
			connection.closeConnection();
			throw new IncompatibleParameterException("No results loaded for said configuration and dataset");

		}
		connection.closeConnection();
	}

	/**
	 * Loads the simulation for a specific configuration and execution. Has to obtain the percentage of spread.
	 * @param configurationUID configuration it's loading
	 * @param dataset dataset it's going to compare with
	 * @param exec execution to load
	 * @return dataset loaded for such configuration
	 */

	public DatasetData loadSimulation(int configurationUID, DatasetData dataset, int exec) throws IncompatibleParameterException{

		int nUsers = checkNumberUsersPerConfig(configurationUID);
		DatasetData simulation = new DatasetData();
		if(dataset.hasTwoValues()) simulation.addInitialPoints();
		else simulation.addInitialPoint();
		Connection con = null;
		try {
			con = connection.getConnection();

			String sql;
			if(dataset.isDaily()) sql = "SELECT * FROM state_per_run WHERE configurationUID="+configurationUID+" AND execution="+exec+" AND tick %24 = 0 ORDER BY tick ASC";
			else sql = "SELECT * FROM state_per_run WHERE configurationUID="+configurationUID+" AND execution="+exec+" ORDER BY tick ASC";
			Statement st = con.createStatement();

			ResultSet rs = st.executeQuery(sql);
			rs.next();
			int endorsers = 0;
			int deniers = 0;
			for(int i=0; i<(dataset.getLengthLoad()-1);i++) {
				if(dataset.hasTwoValues()) {
					endorsers = rs.getInt("infected") + rs.getInt("cured");
				}
				else endorsers = rs.getInt("infected");
				deniers = rs.getInt("vaccinated");
				double endorserPer = (double)endorsers / (double)nUsers * 100.0;
				double denierPer = (double)deniers / (double)nUsers * 100.0;
				if(dataset.hasTwoValues()) simulation.addPercentages(endorserPer, denierPer);
				else simulation.addPercentage(endorserPer);

				if(!rs.next()) {

					if(dataset.getLengthLoad() > simulation.getLengthLoad()) simulation.fillWithLast(dataset.getLengthLoad());
					break;
				}

			}

		} catch ( SQLException ex) {
			connection.closeConnection();
			throw new IncompatibleParameterException("Simulation could not be found for those parameters");

		}
		connection.closeConnection();
		simulation.setDatasetUID(configurationUID);
		simulation.setDaily(dataset.isDaily());
		return simulation;
	}

	/**
	 * Checks the number of users for a configuration
	 * @param configurationUID the configuration to check
	 * @return number of users
	 * @throws IncompatibleParameterException
	 */
	private int checkNumberUsersPerConfig(int configUID) throws IncompatibleParameterException {
		Connection con = null;
		try {
			con = connection.getConnection();

			String sql = "SELECT nUsers FROM initial_data_configuration WHERE configurationUID = "+configUID;
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			int nUsers = rs.getInt("nUsers");
			connection.closeConnection();
			return nUsers;
		} catch ( SQLException ex) {
			connection.closeConnection();
			throw new IncompatibleParameterException("this configuration="+configUID+" has no data loaded");

		}

	}

	/**
	 * Checks if a configuration has a countermeasure model done.
	 * @param configurationLoad configuration for best result being loaded
	 * @throws IncompatibleParameterException
	 */
	private void checkConfigurationForCounter(int configurationLoad) throws IncompatibleParameterException{
		Connection con = null;
		try {
			con = connection.getConnection();
			//compare randomSeed for config loaded and counter
			
			String sql = "SELECT randomSeed FROM main_configuration INNER JOIN rmse_results ON main_configuration.configurationUID = rmse_results.configurationUID AND "
					+ "main_configuration.execution = rmse_results.execution WHERE rmse_results.configurationUID="+configurationLoad+" ORDER BY RMSE ASC LIMIT 1";
			ResultSet rs = con.createStatement().executeQuery(sql);
			rs.next();
			int rSeed = rs.getInt("randomSeed");

			sql = "SELECT main_configuration.configurationUID, execution FROM main_configuration INNER JOIN data_configuration_by_model ON main_configuration.configurationUID = data_configuration_by_model.configurationUID "
					+ "WHERE modelType=4 AND randomSeed="+rSeed;

			rs = con.createStatement().executeQuery(sql);
			
			while(rs.next()) {

				int conf = rs.getInt("configurationUID");
				int execution = rs.getInt("execution");
				//compare probabilities
				sql = "SELECT DISTINCT probAcceptDeny,probInfect,probMakeDenier,nBots,nInfluencers,probInfl,engagement FROM data_configuration_by_model "
						+ "WHERE configurationUID="+configurationLoad+" OR configurationUID="+configUID;

				ResultSet rs2 = con.createStatement().executeQuery(sql);
				rs2.next();
				if(!rs2.next()) {//compare general parameters
					sql = "SELECT DISTINCT netType,nUsers,linksPerNode,networkSeed,nInfected,initialNodesNetwork,probConnect FROM initial_data_configuration "
							+ "WHERE configurationUID="+configurationLoad+" OR configurationUID="+configUID;
					ResultSet rs3 = con.createStatement().executeQuery(sql);
					rs3.next();
					if(!rs3.next()) {
						configUID = conf;
						exec = execution;
						break;
					}
					
				}
			}
			connection.closeConnection();
			if(configUID==-1) {
				throw new IncompatibleParameterException("there's no countermeasures loaded for this configuration of model and dataset");
			}

		} catch (SQLException ex) {
			connection.closeConnection();
			throw new IncompatibleParameterException("there was an error loading the countermeasures, could not be found.");
		}
	}

	/**
	 * Loads a simulation model for the countermeasures.
	 * @param dataset dataset it's using to compare 
	 * @param model model to load
	 * @param configurationLoad configuration used
	 * @return simulation loaded
	 * @throws IncompatibleParameterException
	 */

	public DatasetData loadSimulationModel(DatasetData dataset, int model, int configurationLoad) throws IncompatibleParameterException {
		configUID = -1;
		exec = -1;
		checkConfigurationForCounter(configurationLoad);
		int nUsers = checkNumberUsersPerConfig(configUID);
		DatasetData simulation = new DatasetData(); 
		if(dataset.hasTwoValues()) simulation.addInitialPoints();
		else simulation.addInitialPoint();
		Connection con = null;
		try {
			con = connection.getConnection();

			String sql;
			if(dataset.isDaily()) sql = "SELECT * FROM state_per_run WHERE configurationUID="+configUID+" AND execution="+exec+" AND tick %24 = 0 ORDER BY tick ASC";
			else sql = "SELECT * FROM state_per_run WHERE configurationUID="+configUID+" AND execution="+exec+" ORDER BY tick ASC";

			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			int endorsers = 0;
			int deniers = 0;
			for(int i=0; i<(dataset.getLengthLoad()-1);i++) {

				if(dataset.hasTwoValues()) {
					endorsers = rs.getInt("infected") + rs.getInt("cured");
				}
				else endorsers = rs.getInt("infected");
				deniers = rs.getInt("vaccinated");
				double endorserPer = (double)endorsers / (double)nUsers * 100.0;
				double denierPer = (double)deniers / (double)nUsers * 100.0;
				if(dataset.hasTwoValues()) simulation.addPercentages(endorserPer, denierPer);
				else simulation.addPercentage(endorserPer);

				if(!rs.next()) {

					if(dataset.getLengthLoad() > simulation.getLengthLoad()) simulation.fillWithLast(dataset.getLengthLoad());
					break;
				}
			}

		} catch ( SQLException ex) {
			connection.closeConnection();
			throw new IncompatibleParameterException("Simulation could not be found for those parameters");

		}
		connection.closeConnection();
		simulation.setDaily(dataset.isDaily());

		return simulation;


	}

	/**
	 * Gets display info on all the datasets loaded in the DB.
	 * @return map with dataset name and index.
	 * @throws IncompatibleParameterException
	 */
	public HashMap<Integer,String> checkAvailableDatasets() throws IncompatibleParameterException{
		HashMap<Integer, String> arrayData = new HashMap<Integer,String>();
		Connection con = null;
		try {
			con = connection.getConnection();

			String sql = "SELECT datasetUID, name FROM dataset_information";

			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {

				arrayData.put(rs.getInt("datasetUID"), rs.getString("name"));

			}

		} catch ( SQLException ex) {
			connection.closeConnection();
			throw new IncompatibleParameterException("No datasets could be found");

		}
		connection.closeConnection();

		return arrayData;
	}


}
