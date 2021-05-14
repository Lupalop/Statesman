package statesman.actions;

import statesman.Scene;

public class GotoCommand extends Command {

	public static final String Id = "goto";
	
	private Scene _parentScene;
	private String _groupName;
	
	public GotoCommand() {
		_parentScene = null;
		_groupName = "";
	}
	
	public GotoCommand(Scene parentScene, String commandGroupName) {
		if (parentScene == null) {
			throw new IllegalArgumentException();
		}
		_parentScene = parentScene;
		if (commandGroupName.isBlank()) {
			throw new IllegalArgumentException();
		}
		_groupName = commandGroupName;
	}

	@Override
	public Command createInstance(Scene parent, String[] arguments) {
		if (arguments.length == 2) {
			return new GotoCommand(parent, arguments[1]);
		}
		return null;
	}

	@Override
	public Command createInstance(String[] arguments) {
		return null;
	}

	@Override
	public void execute() {
		if (_parentScene != null && _parentScene.getGroupCommands().containsKey(_groupName)) {
			CommandGroup group = _parentScene.getGroupCommands().get(_groupName);
			group.execute();
		}
	}

}
