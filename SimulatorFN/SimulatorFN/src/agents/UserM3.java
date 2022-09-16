package agents;

import cern.jet.random.Uniform;
import context.Model;
import context.ModelM3;
import context.ModelM3Counter;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

public class UserM3 extends UserM2 {

	
	/**
	 * Constructor for UserM3.
	 * @param id agent ID
	 * @param model Spreading Model
	 * @param state current State
	 * @param type type of user
	 */
	public UserM3(int id, Model model, State state, UserType type) {
		super(id, model, state,type);
		
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
			
			double eng = ((ModelM3)this.getModel()).getEngagementForTime(RunEnvironment.getInstance().getCurrentSchedule().getTickCount());
			
			if(RandomHelper.nextDoubleFromTo(0,1) > eng) continue;
			
			if(this.getState() == State.VACCINATED)  vaccinatedAction(neigh);
			
			if(this.getState() == State.INFECTED) infectedAction(neigh);

		}
		
	}
	
	/**
	 * Infected users capacity to affect other users. If we're on the countermeasures model, we spread the FN (infecting or vaccinating) based on the probability to share.
	 * @param neigh neighbor.
	 */
	@Override
	public void infectedAction(Object neigh) {
		
		State neighState = ((Agent)neigh).getState();
		
		double prob = ((ModelM3)this.getModel()).getProbInfect();
		if(this.getType() == UserType.INFLUENCER) prob = prob + ((ModelM3)this.getModel()).getProbInfl();
		if (RandomHelper.nextDoubleFromTo(0,1) <= prob && neighState == State.NEUTRAL) {
			if(this.getModel() instanceof ModelM3Counter) {

				if(((Uniform)RandomHelper.getDistribution("counterDist")).nextDoubleFromTo(0,1) <= ((ModelM3Counter)this.getModel()).getProbDetect()) { //if the message is detected
					
					if(((Uniform)RandomHelper.getDistribution("counterDist")).nextDoubleFromTo(0,1) <= ((ModelM3Counter)this.getModel()).getProbCounter()) ((UserM2)neigh).setNextState(State.INFECTED);
				
				} else ((UserM2)neigh).setNextState(State.INFECTED); //can infect
			}
			else ((UserM2)neigh).setNextState(State.INFECTED); //can infect
		}
		else if (RandomHelper.nextDoubleFromTo(0,1) <= ((ModelM3)this.getModel()).getProbMakeDenier() && neighState == State.NEUTRAL) {
			
			if(this.getModel() instanceof ModelM3Counter) {
				if(((Uniform)RandomHelper.getDistribution("counterDist")).nextDoubleFromTo(0,1) <= ((ModelM3Counter)this.getModel()).getProbDetect()) { //if the message is detected
					
					if(((Uniform)RandomHelper.getDistribution("counterDist")).nextDoubleFromTo(0,1) <= ((ModelM3Counter)this.getModel()).getProbCounter()) ((UserM2)neigh).setNextState(State.VACCINATED);
				
				}else  ((UserM2)neigh).setNextState(State.VACCINATED); 
			} 
			else ((UserM2)neigh).setNextState(State.VACCINATED); //can vaccinate
		}
	}

}
