package displayNet;

import java.awt.Color;
import java.io.IOException;

import agents.Agent;
import agents.State;
import agents.UserM2;
import agents.UserType;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.VSpatial;

public class StateColors extends DefaultStyleOGL2D{
	
	private boolean error;
	@Override
	public void init(ShapeFactory2D factory) {
		this.shapeFactory = factory;
		try {
			shapeFactory.registerImage("vaccinated", "icons/vaccinatedHead.png");
			shapeFactory.registerImage("vaccinatedInfl", "icons/vaccinatedHead.png");
			shapeFactory.registerImage("cured", "icons/cured.png");
			shapeFactory.registerImage("curedInfl", "icons/curedInfl.png");
			shapeFactory.registerImage("infected", "icons/infectedHead.png");
			shapeFactory.registerImage("infectedInfl", "icons/infectedHeadInfl.png");
			shapeFactory.registerImage("neutral", "icons/neutralHead.png");
			shapeFactory.registerImage("bot", "icons/botHead.png");
			error = true;
		} catch (IOException e) {
			error = false;
			//if it can't register all images, go with standard

		}
	}
	
	public float getScale(Object agent) {
		return (float) 0.30;
	}
	@Override
	public Color getColor(Object agent) {
		
		State st = ((Agent)agent).getState();
		if(st == State.CURED) {
			if(agent instanceof UserM2 && ((UserM2)agent).getType() == UserType.INFLUENCER) { //applies for M3 as well
				return new Color(77,255,255); 
			}
			else return Color.CYAN;
		}
		else if (st == State.INFECTED) {
			if(agent instanceof UserM2 && ((UserM2)agent).getType() == UserType.INFLUENCER) {
				 return new Color(255,128,128);  
			} else if (agent instanceof UserM2 && ((UserM2)agent).getType() == UserType.BOT) return new Color(153,0,0);
			else return Color.RED;
		}
		else if(st == State.NEUTRAL) {
			if(agent instanceof UserM2 && ((UserM2)agent).getType() == UserType.INFLUENCER) {
				return new Color(230,230,230);
			}
			else return Color.GRAY;
		}
		else { //vaccinated
			if(agent instanceof UserM2 && ((UserM2)agent).getType() == UserType.INFLUENCER) {
				return new Color(102,102,255);
			} 
			else return Color.BLUE; 
		}
		
	}
	
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		//if no changes to the spatial, return the same
		State st = ((Agent)agent).getState();
		if(!error) {
			//default
			if (spatial == null) {
			      spatial = shapeFactory.createCircle(4, 16);
			    }
			return spatial;
		}
		if(st == State.CURED) {
			if(agent instanceof UserM2 && ((UserM2)agent).getType() == UserType.INFLUENCER) {
				return shapeFactory.getNamedSpatial("curedInfl");
			}
			else {
				return shapeFactory.getNamedSpatial("cured");
			}
		}
		else if (st == State.INFECTED) {
			if(agent instanceof UserM2 && ((UserM2)agent).getType() == UserType.INFLUENCER) {
				return shapeFactory.getNamedSpatial("infectedInfl");
			} else if (agent instanceof UserM2 && ((UserM2)agent).getType() == UserType.BOT) {
				
				return shapeFactory.getNamedSpatial("bot");
			}
			else {
				return shapeFactory.getNamedSpatial("infected");
			}
		}
		else if(st == State.NEUTRAL) {
			if(agent instanceof UserM2 && ((UserM2)agent).getType() == UserType.INFLUENCER) {
				return shapeFactory.getNamedSpatial("neutral");
			}
			else {
				return shapeFactory.getNamedSpatial("neutral");
			}
				
		}
		else { //vaccinated
			if(agent instanceof UserM2 && ((UserM2)agent).getType() == UserType.INFLUENCER) {
				return shapeFactory.getNamedSpatial("vaccinatedInfl");
			} 
			else {
				return shapeFactory.getNamedSpatial("vaccinated");
			}
		}
		
	}
	
	
}
