package databaseCon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import exception.IncompatibleParameterException;
import metricCalc.CurrentExec;
import metricCalc.ErrorCalc;

public class StoreErrorsDB {


	private ConnectionMgr connection;

	/**
	 * Constructor for InputDataset
	 * @param con connection manager for DB.
	 */
	public StoreErrorsDB(ConnectionMgr con) {
		this.connection = con;
	}

	/**
	 * Loads into the DB the error obtained for a specific configuration and execution.
	 * @param error calculated
	 * @throws IncompatibleParameterException
	 */
	public void loadRMSE(ErrorCalc error) throws IncompatibleParameterException{

		Connection con = null;

		try {
			con = connection.getConnection();

			String sql = "INSERT INTO rmse_results (configurationUID,execution,datasetUID,RMSE) VALUES (?,?,?,?)";
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setInt(1, error.getConfigurationUID());
			statement.setInt(2, error.getExec());
			statement.setInt(3, error.getDatasetUID());
			statement.setDouble(4, error.getRmse());
			statement.executeUpdate();
			connection.closeConnection();

		} catch ( SQLException | IncompatibleParameterException ex) {
			connection.closeConnection();
			throw new IncompatibleParameterException("Could not load error into the DB for (configuration,execution)"
					+ " ("+error.getConfigurationUID()+","+error.getExec()+")");

		}
	}


	/**
	 * Check if a configuration has results loaded. If any is missing, we delete them all.
	 * @return 0 if the results were loaded, 1 if there were missing values or none at all, -1 if no sim found.
	 * @throws IncompatibleParameterException when there were no executions
	 */
	public int checkConfiguration(int configurationUID, int datasetUID, CurrentExec current) throws IncompatibleParameterException {
		//we only have 2 models. check how many runs we have per model. Check rmse_results and count rows.
		current.clear();
		
		Connection con = null;

		try {
			con = connection.getConnection();

			String sql = "SELECT MAX(execution) as execution FROM main_configuration WHERE configurationUID = "+configurationUID;
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			if(rs.next()) {

				current.setMaxExec(rs.getInt("execution")); //total number of execs

				sql = "SELECT COUNT(execution) as execution FROM rmse_results WHERE configurationUID = "+configurationUID+" AND datasetUID="+datasetUID;
				st = con.createStatement();
				rs = st.executeQuery(sql);
				if(rs.next() && current.getMaxExec()!=0) {
					int execsResult = rs.getInt("execution");

					if(current.getMaxExec() != execsResult) {

						sql = "DELETE FROM rmse_results WHERE configurationUID = "+configurationUID + " AND datasetUID = "+datasetUID;
						PreparedStatement stat = con.prepareStatement(sql);
						stat.executeUpdate();
						connection.closeConnection();
						return 1;
					}

					else {
						connection.closeConnection();
						return 0;
					}
				}



			} else {
				connection.closeConnection();
				throw new IncompatibleParameterException("the database has no configurationUID = "+configurationUID); //no execs found
			}


		} catch ( SQLException ex) {
			connection.closeConnection();
			throw new IncompatibleParameterException("Could not check if "+configurationUID+" has results loaded");

		}
		connection.closeConnection();
		return -1;

	}


}
