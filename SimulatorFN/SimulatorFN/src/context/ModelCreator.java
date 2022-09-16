package context;


import agents.*;
import networkBuilding.*;
import cern.jet.random.Uniform;
import exception.IncompatibleParameterException;
import repast.simphony.context.Context;
import repast.simphony.context.space.graph.AbstractGenerator;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;

public class ModelCreator {
	
	Model model;
	
	/**
	 * Main method to populate the context with users. Depending on the model selected, different users will be created.
	 * @param context
	 * @throws IncompatibleParameterException if any parameters have invalid values (negative or zero, or wrong range).
	 */
	public void populateContext(Context<Object> context) throws IncompatibleParameterException {


		Parameters params = RunEnvironment.getInstance().getParameters();
		int numberAgents = params.getInteger("numberAgents");
		int numBots = params.getInteger("numberBots");
		int numInfl = params.getInteger("numberInfluencers");
		model = getModel(params);
		switch(params.getInteger("modelType")) {
		case 1:

			int infectedCount = params.getInteger("infectedAgents");
			for(int i=0; i < numberAgents; i++) {
				if(infectedCount > 0) {
					context.add(new UserM1(i,model,State.INFECTED));
					infectedCount--;
				} else context.add(new UserM1(i,model,State.NEUTRAL));
			}
					
			break;

		case 2:
			
			for(int i=0; i < numberAgents; i++) { //we create all the agents.
				context.add(new UserM2(i,model,State.NEUTRAL, UserType.STANDARD));
			}
			
			break;
		case 3:

			for(int i=0; i < numberAgents; i++) { //we create all the agents.
				context.add(new UserM3(i,model,State.NEUTRAL, UserType.STANDARD));
			}
			
			break;
		case 4:

			for(int i=0; i < numberAgents; i++) { //we create all the agents.
				context.add(new UserM3(i,model,State.NEUTRAL, UserType.STANDARD));
			}
			
			break;
		default:
			throw new IncompatibleParameterException("The model introduced could not be found.");
		}
		
		//generate network
		generateNetwork(params, context, numberAgents);
		
		//countermeasures
		int n = 0;
		if(this.model instanceof ModelM3Counter) n = RandomHelper.nextInt();
		

		RandomHelper.setSeed(params.getInteger("randomSeed"));
		
		if(this.model instanceof ModelM3Counter) {
			RandomHelper.registerGenerator("counterGen", n);
			RandomHelper.registerDistribution("counterDist", new Uniform(RandomHelper.getGenerator("counterGen")));
		}
		
		//set type of users
		if(this.model instanceof ModelM2) {
			Iterable<Object> str = context.getRandomObjects(Agent.class, (numInfl+numBots));
			for(Object agent : str) {
				if(numInfl > 0) {
					numInfl--;
					((UserM2)agent).setType(UserType.INFLUENCER);
				} else if(numBots > 0){
					numBots--;
					((UserM2)agent).setType(UserType.BOT);
				}
				
			}
		}
		
		if(RunEnvironment.getInstance().isBatch()) RunEnvironment.getInstance().endAt(320); //if it's batch execution, end at 320
		
	}
	
	/**
	 * Creates the Spreading Model (1-4), by giving it the initial probabilities.
	 * @param params parameters of the configuration
	 * @return model created
	 * @throws IncompatibleParameterException
	 */
	public Model getModel(Parameters params) throws IncompatibleParameterException {
		
		//general values
		double probAcceptD = params.getDouble("probAcceptDeny");
		double probInfect = params.getDouble("probInfect");
		double probMakeD = params.getDouble("probMakeDenier");
		int numberAgents = params.getInteger("numberAgents");
		int infectedCount = params.getInteger("infectedAgents");
		int randomSeed = params.getInteger("randomSeed");
		//specific for model 2 onwards
		int inflCount = params.getInteger("numberInfluencers");
		double probInfl = params.getDouble("probInfl");
		double engagement = params.getDouble("engagement");
		
		switch(params.getInteger("modelType")) {
		
		case 1:
			
			return new ModelM1(probAcceptD, probInfect, probMakeD, numberAgents, infectedCount, randomSeed);
		case 2:
			
			infectedCount = params.getInteger("numberBots");
			
			return new ModelM2(probAcceptD, probInfect, probMakeD, probInfl, numberAgents, infectedCount, inflCount, randomSeed);	
		case 3:
			
			infectedCount = params.getInteger("numberBots");
			
			return new ModelM3(probAcceptD, probInfect, probMakeD, probInfl, numberAgents, infectedCount, inflCount, randomSeed, engagement);
		case 4:
			
			infectedCount = params.getInteger("numberBots");
			
			double probCounter = params.getDouble("probCounter");
			return new ModelM3Counter(probAcceptD, probInfect, probMakeD, probInfl, numberAgents, infectedCount, inflCount, randomSeed, engagement, probCounter);
		
		default:
			throw new IncompatibleParameterException("The model introduced could not be found.");
		}
		
	}
	
	/**
	 * Creates the network based on the parameters. Currently implemented Barabasi-Albert and Erdos-Renyi.
	 * @param params  initial parameters
	 * @param context
	 * @param numberAgents total number of agents
	 * @throws IncompatibleParameterException
	 */
	public Network<Object> generateNetwork(Parameters params, Context<Object> context, int numberAgents) throws IncompatibleParameterException{
		
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("network", context, false); 
		AbstractGenerator<Object> selGen = null;
		Network<Object> net = null;
		
		String netType = params.getString("networkType");
		
		if(netType.equals("Barabasi-Albert")) {
			selGen = new BarabasiAlbertGenerator(context,params.getInteger("networkSeed"), params.getInteger("linksPerNode"), params.getInteger("initialNodes"), numberAgents);

		} else if(netType.equals("Erdos-Renyi")){
			selGen = new ErdosRenyiGenerator(context, params.getInteger("networkSeed"), numberAgents, params.getDouble("probNetwork"));

		} else throw new IncompatibleParameterException("The network selected doesn't exist");
		
		netBuilder.setGenerator(selGen); //set generator based on user choice
		net = netBuilder.buildNetwork();
		return net;
	}
	

}
