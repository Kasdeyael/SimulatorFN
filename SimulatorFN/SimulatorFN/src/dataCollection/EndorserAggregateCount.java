package dataCollection;

import agents.Agent;
import agents.State;
import repast.simphony.data2.AggregateDataSource;

public class EndorserAggregateCount implements AggregateDataSource {

	@Override
	public String getId() {
		
		return "endorserAgents";
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
			if(((Agent)ob).getState()==State.INFECTED || ((Agent)ob).getState()==State.CURED)res++;
		}
		
		return res;
	}

	@Override
	public void reset() {
		
		
	}

}
