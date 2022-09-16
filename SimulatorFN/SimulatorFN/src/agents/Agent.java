package agents;

import context.Model;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.util.ContextUtils;

public abstract class Agent implements Comparable<Object>{
	
	private Network<Object>net;
	private Model model;
	private int id;
	private State state;
	private State nextState;

	
	/**
	 * Constructor for an agent in the Model.
	 * @param id agent ID
	 * @param model Spreading model
	 * @param state initial State
	 */
	public Agent(int id, Model model, State state) {
		this.setId(id);
		this.setState(state);
		this.setModel(model);
		this.setNextState(state);
		
	}

	/**
	 * Returns agent ID.
	 * @return agentID
	 */
	public int getId() {
		return id;
	}
	/**
	 * Sets agent ID.
	 * @param id new ID
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns current state of the agent.
	 * @return state
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * Sets new current state.
	 * @param state
	 */
	public void setState(State state) {
		this.state = state;
	}
	
	/**
	 * Step method that implements the behavior of each agent.
	 */
	public abstract void step();

	/**
	 * Returns the Network where all the nodes and links are located.
	 * @return Network
	 */
	@SuppressWarnings("unchecked")
	public Network<Object> getNet() {
		if(net == null) {
			
			Context<Object> context = ContextUtils.getContext(this);
			net = (Network<Object>)context.getProjection("network");
		}
		return net;
	}

	/**
	 * Sets the Network.
	 * @param net
	 */
	public void setNet(Network<Object> net) {
		this.net = net;
	}
	
	/**
	 * Returns the News Spreading Model.
	 * @return model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * Sets the News Spreading Model.
	 * @param model
	 */
	public void setModel(Model model) {
		this.model = model;
	}
	
	/**
	 * Sets next State for the agent.
	 * @param nextState
	 */
	public void setNextState(State nextState) {
		this.nextState = nextState;
	}
	/**
	 * Updates the next State for the agent. Scheduled at the beginning of each tick, before each agent acts.
	 */
	@ScheduledMethod(start = 2, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void updateNextState() {
		if(nextState != state)  
			this.setState(nextState);
	}
	
	@Override
	public int compareTo(Object o) { //sorted by network edges
		if(o instanceof Agent) {
			
			if(net.getDegree(o) < net.getDegree(this)) return -1;
			
			else if(net.getDegree(o) > net.getDegree(this)) return 1;
			
			else {
				if(((Agent) o).getId() > id) return -1;
				
				else if(((Agent) o).getId() < id) return 1;
				
				return 0;
			}
			
		} else return 0;
	}
}
