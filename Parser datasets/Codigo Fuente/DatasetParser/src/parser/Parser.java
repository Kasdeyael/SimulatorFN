package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Parser {

	private static final String delimiter = ",";
	private static final String TWITTER="EEE MMM dd HH:mm:ss X yyyy";
	private static final String NEW="dd/MM/yyyy H:mm";
	private HashMap <String, String> userId;
	private ArrayList<Integer> listaUsuariosDia;
	private ArrayList<Double> plotPoints;
	private int idSt = 1;

	/**
	 * Constructor for Parser.
	 */
	public Parser() {
		userId = new HashMap<String,String>();
		listaUsuariosDia = new ArrayList<Integer>();
		plotPoints = new ArrayList<Double>();
	}


	/**
	 * Parses a file, creating two temporal files, the first to remove unnecesary info from the file, and
	 * the second one to order it. After that, it gets parsed and we can generate the % of interaction.
	 * @param jsonFile the file to parse
	 * @param parseTime the time, in minutes, to parse each time unit.
	 */
	public void parse(String jsonFile, int parseTime) {
		userId.clear();
		listaUsuariosDia.clear();
		plotPoints.clear();
		idSt = 1;
		File json = new File(jsonFile);
		if(!json.exists()) {
			throw new IllegalArgumentException("File does not exist.");
		}
		File temp1 = parseUsers(json);
		File temp2 = orderByDate(temp1);
		parseJsonFile(temp2,parseTime);
		new WriteFile().writeSingle(plotPoints,json.getName());
		temp1.delete();
		temp2.delete();

	}


	/**
	 * Parses the initial file, selecting only the userID and the timestamp, to create another dataset without
	 * user information, only randomly asigned IDs.
	 * @param file hydrated tweet file
	 * @return anonimized file
	 */
	public File parseUsers(File file) {

		String fileStr = file.getName().replaceAll(".jsonl", "").concat("temp.csv");
		File fileNew = new File(fileStr);

		ObjectMapper mapper = new ObjectMapper();

		try {
			fileNew.createNewFile();

			BufferedWriter outputWriter = null;
			outputWriter = new BufferedWriter(new FileWriter(fileNew.getName()));
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();

			while (line != null) {

				JsonNode jsonNode = mapper.readTree(line);

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TWITTER,Locale.ENGLISH);
				LocalDateTime dateTimeStart = LocalDateTime.parse(jsonNode.get("created_at").asText(), formatter);
				DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(NEW,Locale.ENGLISH);
				String date = dateTimeStart.format(formatter2).toString(); //date

				String id = jsonNode.get("user").get("id_str").toString();
				id = id.substring(0, id.length());
				if(!userId.containsKey(id)) {
					userId.put(id, ""+idSt);
					idSt++;

				}
				outputWriter.write(date+delimiter+userId.get(id));
				line = br.readLine();
				if(line != null) outputWriter.newLine();

			}

			outputWriter.close();

			br.close();

			return fileNew;
		} catch (IOException e) {
			throw new IllegalArgumentException("The json file could not be parsed.");
		}

	}



	/**
	 * Orders a file by date.
	 * @param anonimized file to order
	 * @return the file ordered
	 */
	public File orderByDate(File file) {

		String fileStr = file.getName().replaceAll(".csv", "").concat("tempOr.csv");
		File file2 = new File(fileStr);

		Stream<String> rows1;
		try {
			rows1 = Files.lines(file.toPath());
			Stream<String> rows2=rows1.sorted(
					Comparator.comparing( ( String line ) -> 
					LocalDateTime.parse(
							line.split(delimiter)[0] ,  // Only the date
							DateTimeFormatter.ofPattern(NEW,Locale.ENGLISH)
							) 
							)
					);
			Files.write((file2.toPath()), (Iterable<String>)rows2::iterator);

			rows1.close();
			return file2;
		} catch (IOException e) {
			throw new IllegalArgumentException("The created file could not be ordered.");
		}

	}

	/**
	 * Parses the file and generates the % of spread.
	 * @param file ordered file with only a timestamp and the userID
	 * @param timeperiod time for each step.
	 */
	public void parseJsonFile(File file,int timeperiod){

		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			String[] tempArr;

			br.readLine();
			line = br.readLine();
			tempArr = line.split(delimiter);
			if(tempArr.length<2) {
				br.close();
				throw new IllegalArgumentException("The file needs to have 2 columns.");
			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(NEW);

			LocalDateTime dateTimeStart = LocalDateTime.parse(tempArr[0], formatter);

			dateTimeStart = LocalDateTime.of(dateTimeStart.getYear(), dateTimeStart.getMonth().getValue(), dateTimeStart.getDayOfMonth(), 0, 0); //start at 0:0

			ArrayList<String> currentUsers = new ArrayList<String>();
			currentUsers.add(tempArr[1]);
			while ((line = br.readLine()) != null) { 

				tempArr = line.split(delimiter);
				if(tempArr.length<2) {
					br.close();
					throw new IllegalArgumentException("The file needs to have 2 columns.");
				}
				LocalDateTime dateTimeNow = LocalDateTime.parse(tempArr[0], formatter);

				if(dateTimeStart.plusMinutes(timeperiod).isAfter(dateTimeNow)) {

					if(!currentUsers.contains(tempArr[1]))currentUsers.add(tempArr[1]);

				} else {

					dateTimeStart = dateTimeStart.plusMinutes(timeperiod);
					//update all users
					listaUsuariosDia.add(currentUsers.size());
					currentUsers.clear();
					currentUsers.add(tempArr[1]);

				}
			}
			if(currentUsers.size()!= 0) {
				listaUsuariosDia.add(currentUsers.size());

			}

			plotPoints.add(0.0);
			for(int timeCount = 0; timeCount < listaUsuariosDia.size(); timeCount++) {
				int usPerDay = listaUsuariosDia.get(timeCount);

				double pr = (double)usPerDay / (double) userId.entrySet().size();
				System.out.println("Time instant:"+(timeCount+1)+", spread: "+String.format("%.3f", pr*100)+"%");
				plotPoints.add(pr*100);
			}

			br.close();
		}catch(IOException ioe) {
			throw new IllegalArgumentException("The ordered file could not be parsed");
		}

	}

	/**
	 * Returns number of users parsed.
	 * @return
	 */
	public int getNumberUsers() {

		return userId.entrySet().size();
	}

	/**
	 * Returns user list
	 * @return
	 */
	public ArrayList<Integer> getListaUsuariosDia() {

		return listaUsuariosDia;
	}

	/**
	 * Returns percentages parsed
	 * @return
	 */
	public ArrayList<Double> getPercentages() {

		return plotPoints;
	}
}
