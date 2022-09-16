package agents;

import context.Model;
import context.ModelM2;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

public class UserM2 extends UserM1{

	private UserType type;
	
	/**
	 * Constructor for UserM2.
	 * @param id agent ID
	 * @param model Spreading Model
	 * @param state current State
	 * @param type type of user
	 */
	public UserM2(int id, Model model, State state, UserType type) {
		super(id, model, state);
		setType(type);
	}
	
	/**
	 * Get type of user (standard, bot or influencer).
	 * @return type of user
	 */
	public UserType getType() {
		return type;
	}
	
	/**
	 * Sets type of user (standard, bot or influencer).
	 * @param type
	 */
	public void setType(UserType type) {
		this.type=type;
		if(this.type == UserType.BOT) {
			this.setState(State.INFECTED);
			this.setNextState(State.INFECTED);
		}
	}
	
	/**
	 * Step method that implements the behavior of each user. Might modify the next state of its neighbors.
	 * 
	 */
	@Override
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		if(this.getState() == State.NEUTRAL || this.getState() == State.CURED) return;
		for(Object neigh : this.getNet().getAdjacent(this)) {
			
			if(this.getState() == State.VACCINATED /*&& neighState == State.NEUTRAL*/)   //if agent is vaccinated
				vaccinatedAction(neigh);

			if(this.getState() == State.INFECTED)  //if agent is infected
				infectedAction(neigh);
			
		}
		
	}
	
	/**
	 * Infected users capacity to affect other users.
	 * @param neigh neighbor
	 */
	public void infectedAction(Object neigh) {
		
		State neighState = ((Agent)neigh).getState();
		
		double prob = ((ModelM2)this.getModel()).getProbInfect();
		if(this.getType() == UserType.INFLUENCER) prob = prob + ((ModelM2)this.getModel()).getProbInfl();
		
		if (RandomHelper.nextDoubleFromTo(0,1) <= prob && neighState == State.NEUTRAL) //prob infect increased
			((UserM2)neigh).setNextState(State.INFECTED); //can infect
		else if (RandomHelper.nextDoubleFromTo(0,1) <= ((ModelM2)this.getModel()).getProbMakeDenier() && neighState == State.NEUTRAL) 
			((UserM2)neigh).setNextState(State.VACCINATED); //can vaccinate
	}
	
	/**
	 * Vaccinated users capacity to affect other users.
	 * @param neigh
	 */
	public void vaccinatedAction(Object neigh) {
		State neighState = ((Agent)neigh).getState();
		
		if(/*this.getState() == State.VACCINATED &&*/ neighState == State.NEUTRAL) {
			
			double prob = ((ModelM2)this.getModel()).getProbAcceptDeny();
			if(this.getType() == UserType.INFLUENCER) prob = prob +((ModelM2)this.getModel()).getProbInfl();
			if(RandomHelper.nextDoubleFromTo(0,1) <= prob) ((UserM2)neigh).setNextState(State.VACCINATED);
			
		}else if(/*this.getState() == State.VACCINATED &&*/ neighState == State.INFECTED && ((UserM2)neigh).getType()!= UserType.BOT) { //if infected and not bot
			if(RandomHelper.nextDoubleFromTo(0,1) <= ((ModelM2)this.getModel()).getProbAcceptDeny()) ((UserM2)neigh).setNextState(State.CURED);
		}
		
	}
	
}
