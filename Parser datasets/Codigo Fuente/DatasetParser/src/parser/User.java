package parser;

import java.util.ArrayList;

public class User {
	private ArrayList<String> allStates;
	private String lastState;
	private String userID;

	/**
	 * Constructor for User
	 * @param userID 
	 * @param state the state it's in
	 */
	public User(String userID, String state) {
		allStates = new ArrayList<String>();
		this.setUserID(userID);
		setState(state);
	}

	/**
	 * Sets current state
	 * @param state
	 */
	public void setState(String state) {
		lastState = state;
	}

	/**
	 * Gets last state
	 * @return
	 */
	public String getLastState() {
		return lastState;
	}

	/**
	 * Gets ID
	 * @return
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * Sets userID
	 * @param userID
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * Updates the next state
	 */
	public void updateState() {
		allStates.add(lastState);
	}

	/**
	 * Returns the list of states
	 * @return
	 */
	public ArrayList<String>getStates(){
		return allStates;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof User)) return false;
		else {
			User user2 = (User)obj;
			if(user2.getUserID() == this.getUserID()) return true;
			return false;
		}
	}




}
