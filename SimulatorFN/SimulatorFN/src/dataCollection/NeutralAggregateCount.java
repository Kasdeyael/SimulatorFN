package dataCollection;

import agents.Agent;
import agents.State;
import repast.simphony.data2.AggregateDataSource;

public class NeutralAggregateCount implements AggregateDataSource {

	@Override
	public String getId() {
		
		return "neutralAgents";
	}

	@Override
	public Class<?> getDataType() {
		
		return Integer.class;
	}

	@Override
	public Class<?> getSourceType() {
		
		return Agent.class;
	}

	@Override
	public Object get(Iterable<?> objs, int size) {
		
		int res = 0;
		for(Object ob : objs) {
			if(((Agent)ob).getState()==State.NEUTRAL)res++;
		}
		
		return res;
	}

	@Override
	public void reset() {
		

	}
}
