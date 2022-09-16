package context;

import exception.IncompatibleParameterException;

public class ModelM3 extends ModelM2 {
	

	private double engagement;
	
	/**
	 * Constructor for ModelM3.
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
	public ModelM3(double probAcceptDeny, double probInfect, double probMakeDenier, double probInfl, int nUsers, int nInf, int nInfl, int randomSeed, double engagement) throws IncompatibleParameterException{
		super(probAcceptDeny,probInfect,probMakeDenier, probInfl, nUsers, nInf, nInfl, randomSeed);
		
		if(!checkProbability(engagement))
				throw new IncompatibleParameterException("The engagement has to be in the range [0,1]");
			
		this.setEngagement(engagement);
	}
	
	
	/**
	 * Returns the initial engagement of the news.
	 * @return engagement
	 */
	public double getEngagement() {
		return engagement;
	}
	
	
	/**
	 * Sets the initial engagement of the news.
	 * @param engagement
	 */
	public void setEngagement(double engagement) {
		this.engagement = engagement;
	}

	/**
	 * Returns the value of the engagement, following an exponential decline:
	 * 	engagement(t) = engagement_i \cdot \exp(- \frac{1}{8} \cdot time) where time is computed in sets of 12 ticks (hours).
	 * @param tickCount
	 * @return engagement based on current tick.
	 */
	public double getEngagementForTime(double tickCount) {
		int number = (int) (tickCount / 12);
		
		return engagement * Math.exp(-number/8);
	}
	

}
