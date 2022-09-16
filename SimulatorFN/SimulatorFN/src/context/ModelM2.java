package context;

import exception.IncompatibleParameterException;

public class ModelM2 extends ModelM1{
	

	private double probInfl;
	private int influencers;
	
	/**
	 * Constructor for ModelM2.
	 * @param probAcceptDeny probability to accept the deny
	 * @param probInfect probability to infect
	 * @param probMakeDenier probability to make a denier
	 * @param probInfl probability to influence
	 * @param nUsers number of users
	 * @param nInf number of infected
	 * @param nInfl number influencers
	 * @param randomSeed random seed
	 */
	public ModelM2(double probAcceptDeny, double probInfect, double probMakeDenier, double probInfl, int nUsers, int nInf, int nInfl, int randomSeed) throws IncompatibleParameterException{
		super(probAcceptDeny, probInfect, probMakeDenier, nUsers,nInf,randomSeed);
		if(nInfl < 0) throw new IncompatibleParameterException("The number of influencers can't be a negative number.");
		if(nUsers < (nInf+nInfl)) throw new IncompatibleParameterException("The number of total agents must be at least the sum of bots and influencers");
		
		if(!checkProbability(probInfl))
			throw new IncompatibleParameterException("The probability probInfl has to be in the range [0,1]");
		
		setInfluencers(nInfl);
		this.setProbInfl(probInfl);
	}

	/**
	 * Get probability to influence
	 * @return probInfl
	 */
	public double getProbInfl() {
		return probInfl;
	}
	
	/**
	 * Set probability to influence
	 * @param probInfl
	 */
	public void setProbInfl(double probInfl) {
		this.probInfl = probInfl;
	}
	
	/**
	 * Get  number of influencers.
	 * @return influencers
	 */
	public int getInfluencers() {
		return influencers;
	}

	/**
	 * Set number of influencers. 
	 * @param influencers
	 */
	public void setInfluencers(int influencers) {
		this.influencers = influencers;
	}
	
}
