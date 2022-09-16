package dataset;

public class DatasetInput {

	private String filename;
	private String name;
	boolean isDaily;
	boolean isSmooth;

	/**
	 * Constructor for DatasetInput.
	 * @param filename file where it's located.
	 * @param name name of dataset
	 * @param isDaily if it's daily
	 * @param isSmooth if it's smooth
	 */
	public DatasetInput(String filename, String name, boolean isDaily, boolean isSmooth) {
		this.filename = filename;
		this.name = name;
		this.isDaily = isDaily;
		this.isSmooth = isSmooth;
	}

	/**
	 * Returns file where it's located.
	 * @return
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Gets name of dataset.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Check if it's parsed daily.
	 * @return
	 */
	public int isDaily() {
		return isDaily ? 1 : 0;
	}

	/**
	 * Check if it's smoothed
	 * @return
	 */
	public int isSmooth() {
		return isSmooth ? 1 : 0;
	}



}
