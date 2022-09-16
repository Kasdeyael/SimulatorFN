package dataset;

import java.util.ArrayList;

public class DatasetData {

	private int datasetUID;
	private ArrayList<Double> endorsers; //if there's only one
	private ArrayList<Double> deniers; //if there are two values
	private boolean isDaily, isSmooth;

	/**
	 * Constructor for Dataset
	 * @param datasetUID
	 */
	public DatasetData(int datasetUID) {
		this.setDatasetUID(datasetUID);
		endorsers = new ArrayList<Double>();
		deniers = new ArrayList<Double>();

	}

	/**
	 * Constructor for dataset, used for simulations.
	 */
	public DatasetData() {

		endorsers = new ArrayList<Double>();
		deniers = new ArrayList<Double>();
	}

	/**
	 * Fills the dataset with the last known value if there are no more records and we need a specific size to compare.
	 * @param size
	 */
	public void fillWithLast(int size) {
		while(endorsers.size() < size) {
			endorsers.add(endorsers.get(endorsers.size()-1)); //add last
			if(hasTwoValues()) {
				deniers.add(deniers.get(deniers.size()-1)); //add last
			}
		}
	}

	/**
	 * Adds a percentage for the next timestamp.
	 * @param value
	 */
	public void addPercentage(double value) {
		endorsers.add(value);

	}


	/**
	 * Adds percentages to the dataset.
	 * @param value1 endorsers
	 * @param value2 deniers
	 */
	public void addPercentages(double value1, double value2) {
		endorsers.add(value1);
		deniers.add(value2);

	}

	/**
	 * Returns the first array (endorsers)
	 * @return arraylist with endorsers
	 */
	public ArrayList<Double> getFirstArray(){
		return endorsers;
	}

	/**
	 * Returns the second array (deniers)
	 * @return arraylist with deniers
	 */
	public ArrayList<Double> getSecondArray(){
		return deniers;
	}

	/**
	 * Checks if this dataset has two values or only one.
	 * @return true if 2 values, false if only one
	 */
	public boolean hasTwoValues() {
		if(deniers.size() != 0) return true;
		return false;
	}

	/**
	 * Returns the datasetUID
	 * @return datasetUID
	 */
	public int getDatasetUID() {
		return datasetUID;
	}

	/**
	 * Sets the datasetUID
	 * @param datasetUID
	 */
	public void setDatasetUID(int datasetUID) {
		this.datasetUID = datasetUID;
	}

	/**
	 * Returns the size of the dataset.
	 * @return
	 */
	public int getLengthLoad() {
		return endorsers.size();
	}

	/**
	 * Prints the values of this dataset.
	 */
	public void print() {
		if(hasTwoValues()) {
			System.out.println("% endorsers and deniers:");
			for(int i = 0; i < endorsers.size(); i++) {
				System.out.println(endorsers.get(i)+", "+deniers.get(i));
			}
		} else {
			System.out.println("% user spread:");
			for(int i = 0; i < endorsers.size(); i++) {
				System.out.println(endorsers.get(i));
			}
		}
	}

	/**
	 * Adds the initial point (endorsers)
	 */
	public void addInitialPoint() {
		endorsers.add(0,0.0);
	}

	/**
	 * Adds the initial points (endorsers and deniers)
	 */
	public void addInitialPoints() {
		endorsers.add(0,0.0);
		deniers.add(0,0.0);
	}

	/**
	 * Gets the minimum value of this dataset, either for endorsers or deniers if its appliable.
	 * @return minimum value.
	 */
	public Double getMinValue() {
		double minScore = Double.MAX_VALUE;
		for (int i=1; i< endorsers.size(); i++) {
			minScore = Math.min(minScore, endorsers.get(i));
		}

		if(hasTwoValues()) {
			for (int i=1; i< deniers.size(); i++) {
				minScore = Math.min(minScore, deniers.get(i));
			}
		}
		return minScore;
	}


	/**
	 * Gets the maximum value of this dataset, for one or the other
	 * @param dens indicates if it should take into account the deniers
	 * @return maximum value.
	 */

	public Double getMaxValue(boolean dens) {
		double maxScore = Double.MIN_VALUE;
		for (Double score : endorsers) {
			maxScore = Math.max(maxScore, score);
		}

		if(hasTwoValues() && dens) {
			maxScore = Double.MIN_VALUE; //reset
			for (Double score : deniers) {
				maxScore = Math.max(maxScore, score);
			}
		}
		return maxScore;
	}

	/**
	 * Check if dataset is daily
	 * @return true if daily, false if hourly
	 */
	public boolean isDaily() {
		return isDaily;
	}

	/**
	 * Sets daily dataset
	 * @param isDaily
	 */
	public void setDaily(boolean isDaily) {
		this.isDaily = isDaily;
	}

	/**
	 * Check if dataset is smoothed
	 * @return true if smoothed
	 */
	public boolean isSmooth() {
		return isSmooth;
	}

	/**
	 * Sets smoothed dataset.
	 * @param isSmooth
	 */
	public void setSmooth(boolean isSmooth) {
		this.isSmooth = isSmooth;
	}
}
