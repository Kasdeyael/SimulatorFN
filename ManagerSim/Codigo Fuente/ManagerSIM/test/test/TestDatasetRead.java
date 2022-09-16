package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import databaseCon.ConnectionMgr;
import databaseCon.DatabaseConnector;
import dataset.DatasetData;
import dataset.DatasetInput;
import exception.IncompatibleParameterException;
import inputFiles.Parameters;

public class TestDatasetRead {


	static DatabaseConnector db_ins;
	static ConnectionMgr con;
	static Connection conn;
	static int config, dataset;
	static String path = "."+File.separator+"test"+File.separator;
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			Parameters params = new Parameters(path+"dbTest.properties");
			db_ins = DatabaseConnector.getInstance();
			db_ins.setParameters(params.getParameter("URL_conn"), params.getParameter("user"), params.getParameter("pass"));
			//load a dataset file
			DatasetInput data = new DatasetInput(path+"testingDataset.csv","test",true,false);
			db_ins.storeDataset(data);
			//load a simulation file
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
	public static void tearDownAfterClass() {
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

				sql = "DELETE FROM real_dataset_spread WHERE datasetUID="+dataset;
				conn.prepareStatement(sql).executeUpdate();
				sql = "DELETE FROM dataset_information WHERE datasetUID="+dataset;
				conn.prepareStatement(sql).executeUpdate();


				con.closeConnection();
			}
		} catch (SQLException | IncompatibleParameterException e) {
			fail("Error while connecting to the DB to delete testing files");
		}
	}


	private DatasetData loadDataset(DatasetData realData, boolean isReal) {


		realData.addInitialPoints();
		if(isReal) {

			realData.addPercentages(26.20599739,19.32855280);
			realData.addPercentages(36.96219035,35.26727509);
			realData.addPercentages(39.01564537,40.31942633);
			realData.addPercentages(40.2542372,41.75358539);
			realData.addPercentages(40.35202086,42.63363754);
			realData.addPercentages(40.38461538,42.95958279);
			realData.addPercentages(40.48239895,43.15514993);
			realData.addPercentages(40.51499348,43.22033898);
			realData.addPercentages(40.54758800,43.51368970);
			realData.addPercentages(40.54758800,43.54628422);
			realData.addPercentages(40.58018252,43.54628422);
			realData.addPercentages(40.93872229,43.44850065);
			realData.addPercentages(41.46023468,43.22033898);
		}else {

			realData.addPercentages(23.2,18.0);
			realData.addPercentages(37.6,33.3);
			realData.addPercentages(40.5,38.8);
			realData.addPercentages(41.7,41.2);
			realData.addPercentages(42.0,42.0);
			realData.addPercentages(42.5,43.0);
			realData.addPercentages(42.5,43.4);
			realData.addPercentages(42.5,44.4);
			realData.addPercentages(42.5,44.4);
			realData.addPercentages(42.6,44.7);
			realData.addPercentages(42.6,44.9);
			realData.addPercentages(42.6,45.1);
			realData.addPercentages(42.6,45.1);
		}
		return realData;
	}

	/**
	 * Test that it can input and recover correctly the real dataset and the simulation.
	 */
	@Test
	public void test() {
		//we test if it reads the correct numbers when loading the dataset.
		DatasetData realData = new DatasetData();
		loadDataset(realData,true);
		DatasetData real=null;

		try {
			HashMap<Integer, String> map = db_ins.checkAvailableDatasets();

			for(Integer val : map.keySet()) {
				if(map.get(val).equals("test")) {
					dataset = val;
					real = db_ins.loadDataset(val);
				}
			}

			if(real==null) fail("Dataset in the DB could not be found");

			DatasetData simData = new DatasetData();
			loadDataset(simData,false);

			String str = "SELECT * FROM main_configuration ORDER BY configurationUID DESC LIMIT 1"; //last one
			int exec=0;
			try {
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(str);
				if(rs.next()) {
					config = rs.getInt("configurationUID");
					exec = rs.getInt("execution");

				}else fail("not loaded the configuration into main");
			}catch ( SQLException ex) {
				fail("Error while connecting to the DB");
			}

			DatasetData data = db_ins.loadSimulation(config,real,exec); //config, real, 3, exec);


			if(realData.getFirstArray().size() != real.getFirstArray().size() ||
					realData.getSecondArray().size() != real.getSecondArray().size() ||
					realData.getFirstArray().size() != realData.getSecondArray().size()) fail("real dataset doesn't match in length");
			if(simData.getFirstArray().size() != data.getFirstArray().size() ||
					simData.getSecondArray().size() != data.getSecondArray().size() ||
					simData.getFirstArray().size() !=simData.getSecondArray().size()) fail("sim dataset doesn't match in length");
			if(data.getFirstArray().size() != real.getFirstArray().size()) fail("sim and real  don't match in length");

			for(int i=0; i < realData.getFirstArray().size(); i++) {
				assertEquals(realData.getFirstArray().get(i), real.getFirstArray().get(i),0.00001);
				assertEquals(simData.getFirstArray().get(i), data.getFirstArray().get(i),0.00001);
			}
		} catch (IncompatibleParameterException e) {
			fail("Error while loading simulations: "+e.getMessage());
		}

	}
}
