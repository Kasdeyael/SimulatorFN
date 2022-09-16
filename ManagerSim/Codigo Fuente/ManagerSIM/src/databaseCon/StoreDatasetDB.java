package databaseCon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import dataset.DatasetInput;
import exception.IncompatibleParameterException;

public class StoreDatasetDB {

	private ConnectionMgr connection;

	/**
	 * Constructor for InputDataset
	 * @param con connection manager for DB.
	 */
	public StoreDatasetDB(ConnectionMgr con) {
		this.connection = con;
	}

	/**
	 * Checks in the DB the last datasetUID loaded and gets the next int for dataset.
	 * @return next DatasetUID.
	 * @throws IncompatibleParameterException if dataset doesn't exist
	 */
	private int getNextDatasetUID() throws IncompatibleParameterException{
		Connection con = null;
		int id = 0;
		try {
			con = connection.getConnection();
			String sql = "SELECT MAX(datasetUID) as datasetUID FROM real_dataset_spread";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			id = rs.getInt("DatasetUID") + 1;

			connection.closeConnection();

		}catch (SQLException | IncompatibleParameterException ex) {

			connection.closeConnection();

			throw new IncompatibleParameterException("Error connecting to the DB");
		}

		return id;

	}

	/**
	 * Loads the dataset, parsed into percentages for user spread or endorsers/deniers into the database.
	 * @param dataIn the info on the dataset
	 * @throws IncompatibleParameterException if dataset doesn't exist or load failed
	 */
	public void loadDataset(DatasetInput dataIn) throws IncompatibleParameterException {
		Connection con = null;
		File file = new File(dataIn.getFilename());
		if(!file.exists()) throw new IncompatibleParameterException("the dataset doesn't exist or couldn't be found.");
		int nextDatasetUID = getNextDatasetUID();
		int timestamp = 0;
		int batchSize = 20;
		try {
			BufferedReader lineReader = new BufferedReader(new FileReader(file));
			lineReader.readLine();
			String line;
			con = connection.getConnection();

			String sql = "INSERT INTO dataset_information (datasetUID,name,isDaily,isSmoothed) VALUES (?,?,?,?)";
			PreparedStatement statement = con.prepareStatement(sql);

			statement = con.prepareStatement(sql);
			statement.setInt(1, nextDatasetUID);
			statement.setString(2, dataIn.getName());

			statement.setInt(3, dataIn.isDaily());
			statement.setInt(4, dataIn.isSmooth());

			statement.executeUpdate();

			con.setAutoCommit(false);

			sql = "INSERT INTO real_dataset_spread (datasetUID,timestamp,endorserLabel,denierLabel,totalSpreading) VALUES (?,?,?,?,?)";
			statement = con.prepareStatement(sql);
			while((line = lineReader.readLine()) != null) {
				if(line.isBlank()) break;
				statement.setInt(1, nextDatasetUID);
				statement.setInt(2, timestamp);
				String[] data = line.split(",");

				if(data.length == 1) { //there was no split
					statement.setDouble(5, Double.parseDouble(data[0]));
					statement.setNull(3, Types.DECIMAL);
					statement.setNull(4, Types.DECIMAL);
				} else if (data.length == 2){ //there's split
					statement.setDouble(3, Double.parseDouble(data[0]));
					statement.setDouble(4, Double.parseDouble(data[1]));
					statement.setNull(5, Types.DECIMAL);
				} else {
					lineReader.close();
					throw new IncompatibleParameterException("Dataset file doesn't have the right format");
				}
				timestamp++;

				statement.addBatch();
				if((timestamp) % batchSize == 0) statement.executeBatch();

			}
			lineReader.close();
			statement.executeBatch();
			con.commit();


			connection.closeConnection();
		} catch (NumberFormatException | SQLException | IOException ex) {

			try {
				
				con.setAutoCommit(true);
				String sql = "DELETE FROM real_dataset_spread WHERE datasetUID ="+nextDatasetUID;
				PreparedStatement statement = con.prepareStatement(sql);
				statement.executeUpdate();
				sql = "DELETE FROM dataset_information WHERE datasetUID ="+nextDatasetUID;
				statement = con.prepareStatement(sql);
				statement.executeUpdate();

				connection.closeConnection();

				throw new IncompatibleParameterException("Error parsing dataset. It was deleted from DB.");
			} catch (SQLException exc) {
				connection.closeConnection();
				throw new IncompatibleParameterException("Error parsing dataset. Could not delete partial results from DB");
			}
		}


	}


}
