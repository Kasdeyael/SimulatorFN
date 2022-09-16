package metricCalc;

public class ErrorCalc {
	private double rmse;
	private int configurationUID;
	private int exec;
	private int datasetUID;

	/**
	 * Constructor for ErrorCalc
	 */
	public ErrorCalc() {}
	/**
	 * Constructor for ErrorCalc
	 * @param rmse
	 * @param configUID
	 * @param model
	 * @param exec
	 * @param datasetUID
	 */
	public ErrorCalc(double rmse, int configUID, int exec, int datasetUID) {
		this.setRmse(rmse);
		this.setConfigurationUID(configUID);
		this.setExec(exec);
		this.setDatasetUID(datasetUID);
	}

	/**
	 * Sets new parameters for a new error
	 * @param rmse
	 * @param configUID
	 * @param model
	 * @param exec
	 * @param datasetUID
	 */
	public void setError(double rmse, int configUID, int exec, int datasetUID) {
		this.setRmse(rmse);
		this.setConfigurationUID(configUID);
		this.setExec(exec);
		this.setDatasetUID(datasetUID);
	}

	/**
	 * Gets error for this simulation
	 * @return
	 */
	public double getRmse() {
		return rmse;
	}

	/**
	 * Sets error for this simulation
	 * @param rmse
	 */
	public void setRmse(double rmse) {
		this.rmse = rmse;
	}

	/**
	 * Gets configuration for this simulation
	 * @return
	 */
	public int getConfigurationUID() {
		return configurationUID;
	}

	/**
	 * Sets configuration for this simulation
	 * @param configurationUID
	 */
	public void setConfigurationUID(int configurationUID) {
		this.configurationUID = configurationUID;
	}

	/**
	 * Gets exec for this simulation
	 * @return
	 */
	public int getExec() {
		return exec;
	}

	/**
	 * Sets exec for this simulation
	 * @param exec
	 */
	public void setExec(int exec) {
		this.exec = exec;
	}

	/**
	 * Gets dataset for this simulation
	 * @return
	 */
	public int getDatasetUID() {
		return datasetUID;
	}

	/**
	 * Sets dataset for this simulation
	 * @param rmse
	 */
	public void setDatasetUID(int datasetUID) {
		this.datasetUID = datasetUID;
	}


}
