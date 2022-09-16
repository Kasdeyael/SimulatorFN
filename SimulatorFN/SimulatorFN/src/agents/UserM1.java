package agents;

import context.Model;
import context.ModelM1;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

public class UserM1 extends Agent{

	/**
	 * Constructor for UserM1.
	 * @param id agent ID
	 * @param model Spreading Model
	 * @param state current State
	 */
	public UserM1(int id, Model model, State state) {
		super(id, model, state);
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
			
			State neighState = ((Agent)neigh).getState();
			
			if(this.getState() == State.VACCINATED && RandomHelper.nextDoubleFromTo(0,1) <= ((ModelM1)this.getModel()).getProbAcceptDeny()) {
				if(neighState == State.NEUTRAL) ((UserM1)neigh).setNextState(State.VACCINATED); 
				if(neighState == State.INFECTED) ((UserM1)neigh).setNextState(State.CURED); 
			}
			
			if(this.getState() == State.INFECTED) {
				if (RandomHelper.nextDoubleFromTo(0,1) <= ((ModelM1)this.getModel()).getProbInfect() && neighState == State.NEUTRAL) 
					((UserM1)neigh).setNextState(State.INFECTED);
				else if (RandomHelper.nextDoubleFromTo(0,1) <= ((ModelM1)this.getModel()).getProbMakeDenier() && neighState == State.NEUTRAL) 
					((UserM1)neigh).setNextState(State.VACCINATED);

			}
		}
		
	}
	
	
}
