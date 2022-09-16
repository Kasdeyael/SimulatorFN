package dataCollection;

import agents.Agent;
import agents.State;
import repast.simphony.data2.AggregateDataSource;

public class SpreadingUsrAggregate implements AggregateDataSource {
	
	@Override
	public String getId() {
		
		return "percentageSpread";
	}

	@Override
	public Class<?> getDataType() {
		
		return Double.class;
	}

	@Override
	public Class<?> getSourceType() {
		
		return Agent.class;
	}

	@Override
	public Object get(Iterable<?> objs, int size) {
		
		double res = 0;
		for(Object ob : objs) {
			if(((Agent)ob).getState()==State.INFECTED)res++;
		}
		
		return res / (double)size * 100.0;
	}

	@Override
	public void reset() {
		

	}

}
