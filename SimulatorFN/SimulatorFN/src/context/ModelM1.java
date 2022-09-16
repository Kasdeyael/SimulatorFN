package context;

import exception.IncompatibleParameterException;

public class ModelM1 extends Model{
	
	private double probAcceptDeny;
	private double probInfect;
	private double probMakeDenier;
	
	/**
	 * Constructor for ModelM1.
	 * @param probAcceptDeny probability to accept the deny
	 * @param probInfect probability to infect
	 * @param probMakeDenier probability to make a denier
	 * @param nUsers number users
	 * @param nInf number of infected
	 * @param randomSeed random seed
	 */
	public ModelM1(double probAcceptDeny, double probInfect, double probMakeDenier, int nUsers, int nInf, int randomSeed) throws IncompatibleParameterException{
		super(nUsers,nInf,randomSeed);

		if(!checkProbability(probAcceptDeny))  
			throw new IncompatibleParameterException("The probability probAcceptDeny has to be in the range [0,1]");
		if(!checkProbability(probInfect)) 
			throw new IncompatibleParameterException("The probability probInfect has to be in the range [0,1]");
		if(!checkProbability(probMakeDenier))  
			throw new IncompatibleParameterException("The probability probMakeDenier has to be in the range [0,1]");
			
		this.probAcceptDeny = probAcceptDeny;
		this.probInfect = probInfect;
		this.probMakeDenier = probMakeDenier;
	}
	
	/**
	 * Returns the probability to accept the deny.
	 * @return probAcceptDeny
	 */
	public double getProbAcceptDeny() {
		return probAcceptDeny;
	}
	
	/**
	 * Sets the probability to accept the deny.
	 * @param probAcceptDeny
	 */
	public void setProbAcceptDeny(double probAcceptDeny) {
		this.probAcceptDeny = probAcceptDeny;
	}
	
	/**
	 * Returns the probability of getting infected.
	 * @return probInfect
	 */
	public double getProbInfect() {
		return probInfect;
	}
	
	/**
	 * Sets the probability of getting infected.
	 * @param probInfect
	 */
	public void setProbInfect(double probInfect) {
		this.probInfect = probInfect;
	}
	
	/**
	 * Returns the probability of making a denier.
	 * @return probMakeDenier
	 */
	public double getProbMakeDenier() {
		return probMakeDenier;
	}
	
	/**
	 * Sets the probability of making a denier.
	 * @param probMakeDenier
	 */
	public void setProbMakeDenier(double probMakeDenier) {
		this.probMakeDenier = probMakeDenier;
	}
	
}
