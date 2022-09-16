package context;

import exception.IncompatibleParameterException;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.dataLoader.ContextBuilder;

public class ContextBuilderDefault extends DefaultContext<Object> implements ContextBuilder<Object> {
	
	@Override
	public Context<Object> build(Context<Object> context) {
		
		context.setId("SimulatorFN"); 
		ModelCreator model = new ModelCreator();
		
		try {
			model.populateContext(context);
		} catch(IncompatibleParameterException exc) { 
			System.err.println("ERROR CREATING THE MODEL: "+exc.getMessage());
			System.exit(0);
		}
		
		
		return context;
	}
	

}
