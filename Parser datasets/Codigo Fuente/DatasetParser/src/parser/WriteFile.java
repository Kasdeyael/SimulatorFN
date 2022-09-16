package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class WriteFile {

	private static final String delimiter = ",";

	/**
	 * Truncates numbers, so it doesn't write numbers with trailing zeros
	 * @param number
	 * @return
	 */
	private String truncatedDouble(double number) {
		return new BigDecimal(number)
				.setScale(8, RoundingMode.DOWN)
				.stripTrailingZeros()
				.toString();
	}
	

	/**
	 * Writes the output into a file
	 * @param infected
	 * @param vaccinated
	 * @param csvFile
	 */
	public void writeDouble(ArrayList<Double> infected, ArrayList<Double> vaccinated, String csvFile) {
		String file = csvFile.replaceAll(".csv", "").concat("parsed.txt");
		File fileNew = new File(file);
		try {
			fileNew.createNewFile();
			BufferedWriter outputWriter = null;
			outputWriter = new BufferedWriter(new FileWriter(fileNew.getName()));
			outputWriter.write("endorses"+delimiter+"deniers");
			for(int i=0; i < infected.size(); i++) {
				outputWriter.newLine();
				outputWriter.write(truncatedDouble(infected.get(i))+delimiter+truncatedDouble(vaccinated.get(i)));
			}

			outputWriter.flush();
			outputWriter.close();


		} catch (IOException e) {

			System.out.println("Error saving the output file");
		}
	}

	/**
	 * Writes the output into a file
	 * @param infected
	 * @param csvFile
	 */
	public void writeSingle(ArrayList<Double> infected, String csvFile) {
		String file = csvFile.replaceAll(".csv", "").concat("parsed.txt");
		File fileNew = new File(file);
		try {
			fileNew.createNewFile();
			BufferedWriter outputWriter = null;
			outputWriter = new BufferedWriter(new FileWriter(fileNew.getName()));
			outputWriter.write("totalSpreading");
			for(int i=0; i < infected.size(); i++) {
				outputWriter.newLine();
				outputWriter.write(infected.get(i).toString());
			}

			outputWriter.flush();
			outputWriter.close();


		} catch (IOException e) {

			System.out.println("Error saving the output file");
		}
	}

}
