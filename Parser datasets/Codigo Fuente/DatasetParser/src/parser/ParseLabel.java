package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class ParseLabel {

	private HashMap<String, User> userMap;
	private ArrayList<User> listaUsers;
	private static final String delimiter = ";";
	private ArrayList<Integer> usersInfected;
	private ArrayList<Integer> usersVaccinated;
	private ArrayList<Double> infected;
	private ArrayList<Double> vaccinated;

	/**
	 * Constructor ParseLabel
	 * @param csvFile the file to parse
	 */
	public ParseLabel() {
		userMap = new HashMap<String, User>();
		listaUsers = new ArrayList<User>();
		usersVaccinated = new ArrayList<Integer>();
		usersInfected = new ArrayList<Integer>();

	}

	/**
	 * Parses a labeled file.
	 * @param csvFile the file to parse
	 */
	public void parse(String csvFile) {
		userMap.clear();
		listaUsers.clear();
		usersVaccinated.clear();
		usersInfected.clear();
		File file = new File(csvFile);
		if(!file.exists()) {
			throw new IllegalArgumentException("File does not exist.");
		}
		loadUsers(file);
		parseLabeledFile(file);
		
		new WriteFile().writeDouble(infected,vaccinated,csvFile);
	}
	
	/**
	 * Loads all the users into the map.
	 * @param csvFile the file to parse
	 */
	public void loadUsers(File file){
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			String[] tempArr;
			br.readLine();
			while ((line = br.readLine()) != null) {
				tempArr = line.split(delimiter);
				if(tempArr.length <3) {
					br.close();
					throw new IllegalArgumentException("The parsed file needs to have 3 columns");
				}
				if(!userMap.containsKey(tempArr[1])) {
					User user = new User(tempArr[1],"neutral");
					listaUsers.add(user);
					userMap.put(tempArr[1], user);
				}
			}
			br.close();

		}catch(IOException ioe) {
			throw new IllegalArgumentException("Could not parse file");
		}

	}

	/**
	 * Parses the file, creating arrays for infected and vaccinated users.
	 * @param csvFile the file to parse
	 */
	public void parseLabeledFile(File file) {

		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			br.readLine();
			line = br.readLine();
			String[] tempArr = line.split(delimiter);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm");
			LocalDateTime dateTimeStart = LocalDateTime.parse(tempArr[0], formatter);

			LocalDate dateDayMonth = LocalDate.of(dateTimeStart.getYear(), dateTimeStart.getMonth().getValue(), dateTimeStart.getDayOfMonth());
			User us = userMap.get(tempArr[1]);
			us.setState(tempArr[2]);
			while ((line = br.readLine()) != null) { 

				tempArr = line.split(delimiter);
				LocalDateTime dateTimeNow = LocalDateTime.parse(tempArr[0], formatter);
				LocalDate dateDayMonthNow = LocalDate.of(dateTimeNow.getYear(), dateTimeNow.getMonth().getValue(), dateTimeNow.getDayOfMonth());

				if(dateDayMonth.isEqual(dateDayMonthNow)) {

					us = userMap.get(tempArr[1]);
					if(!tempArr[2].equals("unrelated") && !tempArr[2].equals("questions") && !tempArr[2].equals("undetermined") ) 
						us.setState(tempArr[2]); //if not unrelated or question
				} else {
					dateDayMonth = dateDayMonthNow;
					//update all users
					for(User usuar : listaUsers) {
						usuar.updateState();
					}
					us = userMap.get(tempArr[1]);
					if(!tempArr[2].equals("unrelated") && !tempArr[2].equals("questions") && !tempArr[2].equals("undetermined") ) 
						us.setState(tempArr[2]);
				}
			}
			br.close();

			for(User usuar : listaUsers) {
				usuar.updateState();
			}

			infected = new ArrayList<Double>();
			vaccinated = new ArrayList<Double>();
			infected.add(0.0);
			vaccinated.add(0.0);
			for(int timeCount = 0; timeCount < listaUsers.get(0).getStates().size(); timeCount++) {
				int spreading = 0;
				int disproving = 0;
				for(User usuario : listaUsers) {
					String state = usuario.getStates().get(timeCount);

					if(state.equals("endorses")) spreading++;
					if(state.equals("denies")) disproving++;
				}

				usersInfected.add(spreading);
				usersVaccinated.add(disproving);
				double perc = (double)spreading / (double)listaUsers.size();
				double perDisp = (double)disproving / (double)listaUsers.size();
				
				System.out.println("Time instant:"+(timeCount+1)+", infected:"+String.format("%.3f", perc*100)+"% and vaccinated:"+String.format("%.3f", perDisp*100)+"%");
				infected.add(perc*100);
				vaccinated.add(perDisp*100);

			}

		}catch(DateTimeParseException | IOException ioe) {
			throw new IllegalArgumentException("Could not parse the file");
		}

	}

	/**
	 * Returns user map
	 * @return
	 */
	public HashMap<String, User> getUserMap() {
		return userMap;
	}

	/**
	 * Returns number of users
	 * @return
	 */
	public int getNumberUsers() {
		return listaUsers.size();
	}

	/**
	 * Returns vaccinated percentages
	 * @return
	 */
	public ArrayList<Double> getVaccinatedPercentage() {
		return vaccinated;
	}

	/**
	 * Returns infected percentages
	 * @return
	 */
	public ArrayList<Double> getInfectedPercentage() {
		return infected;
	}

	/**
	 * Return vaccinated users
	 * @return
	 */
	public ArrayList<Integer> getVaccinated() {
		return usersVaccinated;
	}

	/**
	 * Return infected users
	 * @return
	 */
	public ArrayList<Integer> getInfected() {
		return usersInfected;
	}
}
