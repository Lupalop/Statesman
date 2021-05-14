package statesman.actions;

import statesman.Scene;

public abstract class Command {

	public static final String Id = "";
	
	public abstract Command createInstance(Scene parent, String[] arguments);
	
	public abstract Command createInstance(String[] arguments);
	
	public abstract void execute();
	
}
