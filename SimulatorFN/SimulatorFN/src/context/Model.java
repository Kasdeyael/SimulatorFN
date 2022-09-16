package context;

import exception.IncompatibleParameterException;

public class Model {
	
	
	private int nUsers;
	private int nInf;
	private int randomSeed;
	
	/**
	 * Constructor for Model.
	 * @param nUsers number of users
	 * @param nInf number of infected
	 * @param randomSeed random seed
	 */
	public Model (int nUsers, int nInf, int randomSeed)throws IncompatibleParameterException{
		if(nUsers < 1) throw new IncompatibleParameterException("The total number of agents must be positive.");
		if(nInf < 1 || nInf>nUsers) throw new IncompatibleParameterException("The number of infected must be over 0, and no more than the total agents.");
		this.nUsers = nUsers;
		this.nInf = nInf;
		this.randomSeed = randomSeed;
	}
	
	/**
	 * Check if a probability is in the range of [0,1].
	 * @param prob the probability to check
	 * @return true if it's in range, false if it's not.
	 */
	public boolean checkProbability(double prob) {
		
		if(prob > 1.0 || prob < 0.0) return false;
		return true;
	}

	/**
	 * Get number of users.
	 * @return nUsers
	 */
	public int getnUsers() {
		return nUsers;
	}

	/**
	 * Set number of users
	 * @param nUsers
	 */
	public void setnUsers(int nUsers) {
		this.nUsers = nUsers;
	}
	
	/**
	 * Get number of infected
	 * @return nInfected
	 */
	public int getnInf() {
		return nInf;
	}
	
	/**
	 * Set number of infected.
	 * @param nInf
	 */
	public void setnInf(int nInf) {
		this.nInf = nInf;
	}
	
	/**
	 * Get random seed.
	 * @return randomSeed
	 */
	public int getRandomSeed() {
		return randomSeed;
	}
	
	/**
	 * Set random seed
	 * @param randomSeed
	 */
	public void setRandomSeed(int randomSeed) {
		this.randomSeed = randomSeed;
	}
	
}
