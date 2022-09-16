package databaseCon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

import exception.IncompatibleParameterException;

public class StoreSimsDB {

	private int confUID;
	private int modelType;
	private ConnectionMgr connection;

	/**
	 * Constructor for StoreDB.
	 * @param con connection for DB
	 */
	public StoreSimsDB(ConnectionMgr con) {
		this.connection = con;

	}

	/**
	 * Main method to insert the parameter file and the simulation file into the database.
	 * @param paramFile configuration
	 * @param simFile simulation results
	 * @return number of configuration
	 * @throws IncompatibleParameterException
	 */
	public int loadFilesDB(File paramFile, File simFile) throws IncompatibleParameterException {
		Connection con = null;
		try {
			confUID = -1;
			modelType = -1;
			con = connection.getConnection();
			//gather main info into the class
			setMainData(paramFile,con); 
			//load main_configuration and initial_data_configuration
			insertConfig(paramFile,con);
			//load initial_probabilities depending on modelType
			insertProbs(paramFile,con);
			//load state_per_run
			insertStepsCSV(simFile,con);
			connection.closeConnection();
			return confUID;
		} catch(IncompatibleParameterException e) {

			if(con!=null && confUID!=-1) removeLoadedInfo(con);
			connection.closeConnection();
			throw new IncompatibleParameterException("File could not be loaded, removed from DB.");
		}

	}




	/**
	 * Loads the main parameters (model) about the batch run into the class for later use.
	 * @param paramFile the configuration file
	 * @throws IncompatibleParameterException 
	 */
	private void setMainData(File paramFile, Connection con) throws IncompatibleParameterException {

		confUID = getNextConfiguration(con);

		try {
			BufferedReader lineReader = new BufferedReader(new FileReader(paramFile));


			String[] data = lineReader.readLine().split(",");
			String[] firstLine = lineReader.readLine().split(",");

			for(int i = 0; i<data.length; i++) {
				String nameH = data[i].replaceAll("\"", "");
				if(nameH.equals("modelType")) { //we found model
					modelType = Integer.parseInt(firstLine[i]);
					break;
				}
			}
			lineReader.close();
			if(modelType == -1) throw new IncompatibleParameterException("Could not find the model. Simulation files are incomplete");
		} catch(NumberFormatException | IOException ex) {
			throw new IncompatibleParameterException("Could not parse parameter file");
		} 



	}

	/**
	 * Loads the probabilities per model into the database.
	 * @param paramFile the configuration file
	 */
	private void insertProbs(File paramFile, Connection con) throws IncompatibleParameterException {

		try {
			BufferedReader lineReader = new BufferedReader(new FileReader(paramFile));
			String[] data = lineReader.readLine().split(",");
			String[] firstLine = lineReader.readLine().split(",");
			if(data.length != firstLine.length) {
				lineReader.close();
				throw new IncompatibleParameterException("Could not parse simulation file, missing parameters");
			}
			if(modelType == 1) { //type is 1
				double probAccept = -1, probInf = -1, probMakeDen = -1;
				for(int i = 0; i<data.length; i++) {
					String nameH = data[i].replaceAll("\"", "");
					if(nameH.equals("probAcceptDeny")) {
						probAccept = Double.parseDouble(firstLine[i]);

					} else if(nameH.equals("probInfect")) {
						probInf = Double.parseDouble(firstLine[i]);

					} else if(nameH.equals("probMakeDenier")) {
						probMakeDen = Double.parseDouble(firstLine[i]);
					}
					if(probAccept != -1 && probInf != -1 && probMakeDen != -1) break;
				}

				lineReader.close();
				
				if(probAccept == -1 || probInf == -1 || probMakeDen == -1) throw new IncompatibleParameterException("Could not parse parameter file, missing parameters.");
					
				String sql = "INSERT INTO data_configuration_by_model (configurationUID,modelType,probAcceptDeny,probInfect,probMakeDenier,nBots,nInfluencers,probInfl) VALUES (?,?,?,?,?,NULL,NULL,NULL)";
				PreparedStatement statement = con.prepareStatement(sql);
				statement.setInt(1, confUID);
				statement.setInt(2, modelType);
				statement.setDouble(3, probAccept); 
				statement.setDouble(4, probInf);
				statement.setDouble(5, probMakeDen);

				statement.executeUpdate();


			} else { //type is 2-4

				int nBots = -1, nInfl = -1;
				double probInfl = -1, probAccept = -1, probInf = -1, probMakeDen = -1, engagement = -1;
				for(int i = 0; i<data.length; i++) {
					String nameH = data[i].replaceAll("\"", "");
					if(nameH.equals("probAcceptDeny")) {
						probAccept = Double.parseDouble(firstLine[i]);

					} else if(nameH.equals("probInfect")) {
						probInf = Double.parseDouble(firstLine[i]);

					} else if(nameH.equals("probMakeDenier")) {
						probMakeDen = Double.parseDouble(firstLine[i]);
					}
					else if(nameH.equals("numberBots")) {
						nBots = Integer.parseInt(firstLine[i]);

					} else if(nameH.equals("numberInfluencers")) {
						nInfl = Integer.parseInt(firstLine[i]);

					} else if(nameH.equals("probInfl")) {
						probInfl = Double.parseDouble(firstLine[i]);
					} else if(nameH.equals("engagement")) {
						engagement = Double.parseDouble(firstLine[i]);
					}
					if(probAccept != -1 && probInf != -1 && probMakeDen != -1 && nBots != -1 && nInfl != -1 && probInfl!=-1 && engagement != -1)break; //they're found
				}
				lineReader.close();
				
				if(probAccept == -1 || probInf== -1 || probMakeDen == -1 || nBots == -1 || nInfl == -1 || probInfl == -1) {
					throw new IncompatibleParameterException("Could not parse simulation file, missing parameters");
				}
				
				String sql = "INSERT INTO data_configuration_by_model (configurationUID,modelType,probAcceptDeny,probInfect,probMakeDenier,nBots,nInfluencers,probInfl, engagement) VALUES (?,?,?,?,?,?,?,?,?)";
				PreparedStatement statement = con.prepareStatement(sql);
				statement.setInt(1, confUID);
				statement.setInt(2, modelType);
				statement.setDouble(3, probAccept);
				statement.setDouble(4, probInf);
				statement.setDouble(5, probMakeDen);
				statement.setInt(6, nBots);
				statement.setInt(7, nInfl);
				statement.setDouble(8, probInfl); 
				if(modelType > 2) statement.setDouble(9, engagement); 
				else statement.setNull(9, java.sql.Types.DOUBLE);

				statement.executeUpdate();
			}
			


		}catch(NumberFormatException |IOException | SQLException ex){

			throw new IncompatibleParameterException("Error while parsing and loading parameter file into DB");
		}

	}

	/**
	 * Loads the main databases with the simulation run information and other static parameters.
	 * @param paramFile the configuration file
	 */
	private void insertConfig(File paramFile, Connection con) throws IncompatibleParameterException{


		int batchSize = 20;
		try {

			con.setAutoCommit(false);

			String sql = "INSERT INTO main_configuration (configurationUID,execution,randomSeed) VALUES (?,?,?)";
			PreparedStatement statement = con.prepareStatement(sql);

			BufferedReader lineReader = new BufferedReader(new FileReader(paramFile));

			String[] header = lineReader.readLine().split(",");

			int exec = -1, rand= -1;
			for(int i = 0; i<header.length; i++) {
				String nameH = header[i].replaceAll("\"", "");
				if(nameH.equals("run")) {
					exec = i;
				} else if(nameH.equals("randomSeed")) {
					rand = i;
				}
				if(exec != -1 && rand != -1) break;
			}
			if(exec == -1 || rand == -1) {
				lineReader.close();
				throw new IncompatibleParameterException("Could not parse simulation file, missing parameters");
			}
			String line = null;
			int count = 0;
			while((line = lineReader.readLine()) != null) {
				count++;
				statement.setInt(1, confUID);
				String[] data = line.split(",");
				if(data.length != header.length) {
					lineReader.close();
					throw new IncompatibleParameterException("Could not parse simulation file, missing parameters");
				}
				statement.setInt(2, Integer.parseInt(data[exec]));
				statement.setInt(3, Integer.parseInt(data[rand]));

				statement.addBatch();

				if(count % batchSize == 0) statement.executeBatch();
			}

			statement.executeBatch();

			con.commit();

			con.setAutoCommit(true);

			sql = "INSERT INTO initial_data_configuration (configurationUID,netType,nUsers,linksPerNode,networkSeed,nInfected,initialNodesNetwork,probConnect) VALUES (?,?,?,?,?,?,?,?)";
			statement = con.prepareStatement(sql);
			statement.setInt(1, confUID);


			double pCon = -1;
			int nUsers = -1, links = -1, netSeed = -1, nInfect= -1, iniNodes = -1, nwType = -1;
			lineReader.close();

			lineReader = new BufferedReader(new FileReader(paramFile));
			lineReader.readLine(); //header
			String[] data = lineReader.readLine().split(",");
			for(int i = 0; i<header.length; i++) {
				String nameH = header[i].replaceAll("\"", "");
				if(nameH.equals("numberAgents")) {
					nUsers = Integer.parseInt(data[i]);
				} else if(nameH.equals("initialNodes")) {
					iniNodes = Integer.parseInt(data[i]);
				} else if(nameH.equals("linksPerNode")) {
					links = Integer.parseInt(data[i]);
				} else if(nameH.equals("networkSeed")) {
					netSeed = Integer.parseInt(data[i]);
				}else if(nameH.equals("infectedAgents") && modelType ==1) {
					nInfect = Integer.parseInt(data[i]);
				}else if(nameH.equals("numberBots") && modelType !=1) {
					nInfect = Integer.parseInt(data[i]);
				} else if(nameH.equals("probNetwork")) {
					pCon = Double.parseDouble(data[i]);
				} else if(nameH.equals("networkType")) {
					nwType = (data[i].equals("\"Barabasi-Albert\"")) ? 1 : 2;

				}

				if(nUsers != -1 && links!= -1 && netSeed != -1 && nInfect != -1 && iniNodes != -1 && pCon != -1 && nwType != -1) break; //they're found
			}
			lineReader.close();
			
			if(nwType == -1) nwType = 1;										
			if(nUsers == -1 || links== -1 || netSeed == -1 || nInfect == -1 || iniNodes == -1 || pCon == -1) {
				throw new IncompatibleParameterException("Could not parse simulation file, missing parameters");
			}
			statement.setInt(2, nwType);
			statement.setInt(3,nUsers);
			if(nwType == 1) statement.setInt(4, links);
			else statement.setNull(4, java.sql.Types.SMALLINT);
			statement.setInt(5, netSeed);
			statement.setInt(6, nInfect);
			if(nwType == 1) statement.setInt(7, iniNodes);
			else statement.setNull(7, java.sql.Types.SMALLINT);
			if(nwType == 2) statement.setDouble(8, pCon);
			else statement.setNull(8, java.sql.Types.DOUBLE);
			

			statement.executeUpdate();


		}catch(NumberFormatException |IOException ex) {

			throw new IncompatibleParameterException("Error while parsing parameter file");
		} catch ( SQLException ex) {

			try {
				con.rollback();

			} catch (SQLException exc) {

				throw new IncompatibleParameterException("Error while loading parameter file into DB, could not delete partial results");
			}
		}

	}

	/**
	 * Loads the ticks per run with its state into the database.
	 * @param simFile simulation results
	 */
	private void insertStepsCSV(File simFile, Connection con) throws IncompatibleParameterException{

		int batchSize = 20;

		try {

			con.setAutoCommit(false);
			String sql = "INSERT INTO state_per_run (configurationUID,execution,tick,vaccinated,cured,infected,neutral) VALUES (?,?,?,?,?,?,?)";
			PreparedStatement statement = con.prepareStatement(sql);

			BufferedReader lineReader = new BufferedReader(new FileReader(simFile));

			String line = null;
			lineReader.readLine();

			int count = 0;
			while((line = lineReader.readLine()) != null) {
				count++;
				String[] data = line.split(",");
				if(data.length<6) {
					lineReader.close();
					throw new IncompatibleParameterException("Could not parse simulation file, wrong format.");
				}
				int run = Integer.parseInt(data[0]);
				int tick = (int)Double.parseDouble(data[1]);

				int vaccinated = Integer.parseInt(data[5]);
				int cured = Integer.parseInt(data[2]);
				int infected = Integer.parseInt(data[3]);
				int neutral = Integer.parseInt(data[4]);
				statement.setInt(1, confUID);
				statement.setInt(2, run);
				statement.setInt(3, tick);
				statement.setInt(4, vaccinated);
				statement.setInt(5, cured);
				statement.setInt(6, infected);
				statement.setInt(7, neutral);

				statement.addBatch();

				if(count % batchSize == 0) statement.executeBatch();
			}

			lineReader.close();

			statement.executeBatch();

			con.commit();

			con.setAutoCommit(true);

		} catch (NumberFormatException ex) {

			throw new IncompatibleParameterException("Could not parse simulation file, wrong format.");
		} catch(IOException ex) {

			throw new IncompatibleParameterException("Error while parsing parameter file");
		} catch ( SQLException ex) {

			try {
				con.rollback();

			} catch (SQLException exc) {
				throw new IncompatibleParameterException("Error while loading parameter file into DB, could not delete partial results");
			}
		}


	}


	/**
	 * Reads main database, finds highest configurationUID and returns the next.
	 * @return next configurationUID value
	 */
	private int getNextConfiguration(Connection con) throws IncompatibleParameterException{

		try {

			String sql = "SELECT MAX(configurationUID) as configurationUID FROM main_configuration";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			int id = 0;
			if(rs.next()) {
				id = rs.getInt("configurationUID") + 1;
			}
			
			return id;
		} catch ( SQLException ex) {

			throw new IncompatibleParameterException("Error while connecting to the DB");

		}


	}

	/**
	 * Removes any loaded info in the DB if there was a partial load.
	 * @throws IncompatibleParameterException
	 */
	private void removeLoadedInfo(Connection con) throws IncompatibleParameterException{

		
		try {
			con.setAutoCommit(true);
			String sql = "DELETE FROM state_per_run WHERE configurationUID="+confUID;
			con.prepareStatement(sql).executeUpdate();
			sql = "DELETE FROM initial_data_configuration WHERE configurationUID="+confUID;
			con.prepareStatement(sql).executeUpdate();
			sql = "DELETE FROM data_configuration_by_model WHERE configurationUID="+confUID;
			con.prepareStatement(sql).executeUpdate();
			sql = "DELETE FROM main_configuration WHERE configurationUID="+confUID;
			con.prepareStatement(sql).executeUpdate();

		} catch ( SQLException ex) {
			throw new IncompatibleParameterException("Could not restore DB.");

		}
	}

	public int getConfUID() {
		return confUID;
	}
}
