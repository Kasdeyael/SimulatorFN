package metricCalc;


import databaseCon.DatabaseConnector;
import dataset.DatasetData;
import exception.IncompatibleParameterException;


public class MetricCalculator {


	/**
	 * Constructor for MetricCalculator
	 */
	public MetricCalculator() {

	}
	/**
	 * Obtains the RMSE linked to all the configurations contained between configTo and configFrom, using the datasetUID.
	 * @param datasetUID dataset to use
	 * @param configFrom starting configuration (inclusive)
	 * @param configTo ending configuration (inclusive)
	 * @throws IncompatibleParameterException
	 */
	public void startCalc(int datasetUID, int configFrom, int configTo) throws IncompatibleParameterException {

		DatabaseConnector db_ins = DatabaseConnector.getInstance();
		DatasetData data = db_ins.loadDataset(datasetUID);
		CurrentExec current = new CurrentExec();
		ErrorCalc error = new ErrorCalc();
		for(int i = configFrom; i<=configTo; i++) {
			int res = db_ins.checkConfiguration(i,datasetUID,current);

			switch(res) {

			case 0:
				System.out.println("results already loaded for "+i);
				break;
			case 1:

				System.out.println("Processing configuration = "+i+", with "+current.getMaxExec()+" executions");

				for(int exec=1; exec <= current.getMaxExec(); exec++) { //for all executions

					DatasetData sim = db_ins.loadSimulation(i, data, exec);

					double rmse = obtainRMSE(sim, data);

					error.setError(rmse, i, exec, datasetUID);
					db_ins.storeError(error);
				}

				break;

			case -1:
				System.out.println("Configuration "+i+" could not be found");
				break;
			}

		}

	}


	/**
	 * Calculates the RMSE for endorsers and vaccinated users, if the dataset has both values, and combines them into one RMSE
	 * for easier comparison. If there's only one value, it computes the traditional RMSE based on user engagement.
	 * @param simulation the simulated dataset
	 * @param real the real dataset
	 * @return error
	 */
	public double obtainRMSE(DatasetData simulation, DatasetData real) {


		double rmse1= 0.0;
		double rmse2= 0.0;

		double sum1 = 0.0;
		double sum2 = 0.0;
		for(int i=0; i<real.getLengthLoad(); i++) {

			if(real.hasTwoValues()) { //two datasets

				double val1 = real.getFirstArray().get(i) - simulation.getFirstArray().get(i);
				double val2 = real.getSecondArray().get(i) - simulation.getSecondArray().get(i);

				val1 = Math.pow(val1, 2);
				val2 = Math.pow(val2, 2);

				sum1 = sum1 + val1;
				sum2 = sum2 + val2;

			} else { //only one

				double val = real.getFirstArray().get(i) - simulation.getFirstArray().get(i);
				val = Math.pow(val, 2);
				sum1 = sum1 + val;
			}

		}
		sum1 = sum1 / (double)real.getLengthLoad(); //divide by length (days)

		rmse1 = Math.sqrt(sum1);
		if(real.hasTwoValues()) {
			sum2 = sum2 / (double)real.getLengthLoad();

			rmse2 = Math.sqrt(sum2);

			double rmse = Math.sqrt(Math.pow(rmse1, 2) + Math.pow(rmse2, 2));

			return rmse;
		}
		return rmse1;
	}


}
