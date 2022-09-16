package context;

import exception.IncompatibleParameterException;

public class ModelM3Counter extends ModelM3 {
	
	private double probDetect = 0.7; //70% initially
	private double probCounter;
	/**
	 * Constructor for ModelM3Counter.
	 * @param probAcceptDeny probability to accept the deny
	 * @param probInfect probability to infect
	 * @param probMakeDenier probability to make a denier
	 * @param probInfl probability to influence if influencer
	 * @param nUsers number of users
	 * @param nInf number of infected  (bots)
	 * @param nInfl number of influencers
	 * @param randomSeed random seed for simulation
	 * @param engagement engagement of the news
	 */
	public ModelM3Counter(double probAcceptDeny, double probInfect, double probMakeDenier, double probInfl, int nUsers, int nInf, int nInfl, int randomSeed, double engagement, double probCounter) throws IncompatibleParameterException{
		super(probAcceptDeny,probInfect,probMakeDenier, probInfl, nUsers, nInf, nInfl, randomSeed, engagement);
		
		if(!checkProbability(probCounter))
				throw new IncompatibleParameterException("The probCounter has to be in the range [0,1]");
			
		this.setProbCounter(probCounter);
	}
	
	
	/**
	 * Returns the initial engagement of the news.
	 * @return engagement
	 */
	public double getProbCounter() {
		return probCounter;
	}
	
	
	/**
	 * Sets the initial engagement of the news.
	 * @param engagement
	 */
	public void setProbCounter(double probCounter) {
		this.probCounter = probCounter;
	}

	/**
	 * Returns probability to detect tweets
	 * @return probDetect
	 */
	public double getProbDetect() {
		return probDetect;
	}

	/**
	 * Sets probability to detect tweets
	 * @param probDetect
	 */
	public void setProbDetect(double probDetect) {
		this.probDetect = probDetect;
	}
	

}
