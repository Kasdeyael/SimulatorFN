package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import org.junit.Test;

import databaseCon.ConnectionMgr;
import databaseCon.DatabaseConnector;
import exception.IncompatibleParameterException;
import inputFiles.Parameters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestLoaderSims {

	static DatabaseConnector db_ins;
	static ConnectionMgr con;
	static Connection conn;
	static int config;
	static String path = "."+File.separator+"test"+File.separator;
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			Parameters params = new Parameters(path+"dbTest.properties");
			db_ins = DatabaseConnector.getInstance();
			db_ins.setParameters(params.getParameter("URL_conn"), params.getParameter("user"), params.getParameter("pass"));

			File testingFile1 = new File(path+"configDataTest.txt");
			File testingFile2 = new File(path+"DataTestRun.txt");
			db_ins.storeSimulations(testingFile1, testingFile2);
			con = new ConnectionMgr(params.getParameter("URL_conn"), params.getParameter("user"), params.getParameter("pass"));
			conn = con.getConnection();
		} catch (IncompatibleParameterException e) {
			fail("Error while setting up test: "+e.getMessage());
		}
	}


	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//deletes
		try {
			if(conn!=null) {
				String sql = "DELETE FROM state_per_run WHERE configurationUID="+config;
				conn.prepareStatement(sql).executeUpdate();
				sql = "DELETE FROM initial_data_configuration WHERE configurationUID="+config;
				conn.prepareStatement(sql).executeUpdate();
				sql = "DELETE FROM data_configuration_by_model WHERE configurationUID="+config;
				conn.prepareStatement(sql).executeUpdate();
				sql = "DELETE FROM main_configuration WHERE configurationUID="+config;
				conn.prepareStatement(sql).executeUpdate();

				con.closeConnection();
			}
		} catch (SQLException | IncompatibleParameterException e) {
			fail("Error while connecting to the DB to delete testing files");
		}
	}

	/**
	 * Test that it can input the main configuration into the DB
	 */
	@Test
	public void testLoadMainInitial() {
		//check database for the results in main and initial
		String str = "SELECT * FROM main_configuration ORDER BY configurationUID DESC LIMIT 1"; //the last

		try {

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(str);
			int randomSeed = -1;
			int exec = -1;
			if(rs.next()) {
				config = rs.getInt("configurationUID");
				randomSeed = rs.getInt("randomSeed");
				exec = rs.getInt("execution");
			}else fail("not loaded the configuration into main");

			assertEquals(768,randomSeed);
			assertEquals(1,exec);


			str = "SELECT * FROM initial_data_configuration ORDER BY configurationUID DESC LIMIT 1"; //we check the main
			st = conn.createStatement();
			rs = st.executeQuery(str);
			int nUsers = 0, linksPerNode = 0, networkSeed = 0, nInf = 0, initialNodes = 0, netType= 0;
			if(rs.next()) {
				netType = rs.getInt("netType");
				nUsers = rs.getInt("nUsers");
				linksPerNode = rs.getInt("linksPerNode");
				networkSeed = rs.getInt("networkSeed");
				nInf = rs.getInt("nInfected");
				initialNodes = rs.getInt("initialNodesNetwork");

			}else fail("not loaded the configuration into initial_data");
			assertEquals(1, netType);
			assertEquals(1000, nUsers);
			assertEquals(17, networkSeed);
			assertEquals(2, nInf);
			assertEquals(10, linksPerNode);
			assertEquals(10, initialNodes);

		} catch ( SQLException ex) {
			fail("Cannot connect to the database");

		}

	}

	/**
	 * Test that it can input the probabilities configuration into the DB
	 */
	@Test
	public void testLoadProbs() {

		String str = "SELECT * FROM data_configuration_by_model ORDER BY configurationUID DESC LIMIT 1"; //we check the main

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(str);
			int modelType = -1;
			int nBots = -1, nInfluencers = -1;
			double probAD = -1, probMD = -1, probInf = -1, probInfl = -1, engagement = -1;
			if(rs.next()) {
				modelType = rs.getInt("modelType");
				nBots = rs.getInt("nBots");
				nInfluencers = rs.getInt("nInfluencers");

				probAD = rs.getDouble("probAcceptDeny");
				probMD = rs.getDouble("probMakeDenier");
				probInf = rs.getDouble("probInfect");
				probInfl = rs.getDouble("probInfl");
				engagement = rs.getDouble("engagement");
			}else fail("not loaded the configuration into main");

			//compare to results
			assertEquals(3,modelType);
			assertEquals(2,nBots);
			assertEquals(5,nInfluencers);
			assertEquals(0.0045,probAD, 0.00001);
			assertEquals(0.0047,probMD,0.00001);
			assertEquals(0.0107,probInf,0.00001);
			assertEquals(0.019,probInfl,0.00001);
			assertEquals(1.0,engagement,0.00001);

		} catch ( SQLException ex) {
			fail("Cannot connect to the database");

		}

	}

	/**
	 * Test that it can input the state information of the agents into the DB.
	 */
	@Test
	public void testLoadStates() {
		//open file and compare
		File testingFile = new File(path+"DataTestRun.txt");

		try {

			String sql = "SELECT execution,tick,vaccinated,cured,infected,neutral FROM state_per_run WHERE configurationUID="+config;
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			BufferedReader lineReader = new BufferedReader(new FileReader(testingFile));

			String line = null;
			lineReader.readLine(); //header

			while((line = lineReader.readLine()) != null) {
				String[] data = line.split(",");
				int run = Integer.parseInt(data[0]);
				int tick = (int)Double.parseDouble(data[1]);

				int vaccinated = Integer.parseInt(data[5]);
				int cured = Integer.parseInt(data[2]);
				int infected = Integer.parseInt(data[3]);
				int neutral = Integer.parseInt(data[4]);

				if(rs.next()) {
					assertEquals(run, rs.getInt("execution"));
					assertEquals(tick, rs.getInt("tick"));
					assertEquals(infected, rs.getInt("infected"));
					assertEquals(vaccinated, rs.getInt("vaccinated"));
					assertEquals(cured, rs.getInt("cured"));
					assertEquals(neutral, rs.getInt("neutral"));

				}else fail("not loaded correctly. There's more in the file");

			}
			if(rs.next()) fail("not loaded correctly. There's more in the DB");
			lineReader.close();
		} catch ( SQLException ex) {
			fail("Could not connect to the DB");
		} catch (FileNotFoundException e) {
			fail("Simulation file could not be found");

		} catch (IOException e) {
			fail("Error parsing the file");

		}
	}
}
