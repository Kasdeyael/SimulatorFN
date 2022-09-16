package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import metricCalc.MetricCalculator;
import databaseCon.ConnectionMgr;
import databaseCon.DatabaseConnector;
import dataset.DatasetInput;
import exception.IncompatibleParameterException;
import inputFiles.Parameters;

public class TestCalculatorRMSE {

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
			//load dataset and one config
			DatasetInput data = new DatasetInput(path+"testingDataset.csv","test",true,false);
			db_ins.storeDataset(data);
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
				sql = "DELETE FROM rmse_results WHERE configurationUID="+config;
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


	/**
	 * Test that it obtains the right RMSE for a specific simulation.
	 */
	@Test
	public void test() {

		try {
			String str = "SELECT * FROM main_configuration ORDER BY configurationUID DESC LIMIT 1"; //the last
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(str);
			if(rs.next()) {
				config = rs.getInt("configurationUID");
			}else fail("not loaded the configuration into main");

			str = "SELECT * FROM dataset_information ORDER BY datasetUID DESC LIMIT 1"; //the last

			st = conn.createStatement();
			rs = st.executeQuery(str);
			//do the calculations
			if(rs.next()) {
				dataset = rs.getInt("datasetUID");
			}else fail("not loaded the dataset into the db");

			MetricCalculator metric = new MetricCalculator();
			metric.startCalc(dataset, config, config);

			//it should be loaded, now we check if it's the same result 
			String sql = "SELECT * FROM rmse_results WHERE configurationUID="+config+" AND datasetUID="+dataset;
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if(rs.next()) {
				assertEquals(2.161607172,rs.getDouble("RMSE"),0.00000001);

			}else fail("result not loaded");

		} catch (IncompatibleParameterException e) {
			fail("Error while calculating RMSE:" +e.getMessage());
		} catch (SQLException e) {
			fail("Error while connecting to the DB");
		}

	}


}
