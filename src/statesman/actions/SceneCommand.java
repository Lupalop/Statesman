package statesman.actions;

import statesman.App;
import statesman.Scene;

public class SceneCommand extends Command {

	public static final String Id = "scene";
	
	private String _targetScene;
	
	public SceneCommand() {
		_targetScene = "";
	}
	
	public SceneCommand(String targetScene) {
		if (targetScene.isEmpty()) {
			throw new IllegalArgumentException();
		}
		_targetScene = targetScene;
	}

	@Override
	public Command createInstance(Scene parent, String[] arguments) {
		if (arguments.length == 2) {
			return new SceneCommand(arguments[1]);
		}
		return null;
	}

	@Override
	public Command createInstance(String[] arguments) {
		return createInstance(null, arguments);
	}

	@Override
	public void execute() {
		if (App.Content.getScenes().containsKey(_targetScene)) {
			App.Interpreter.setCurrentScene(App.Content.getScenes().get(_targetScene));
		}
	}

}
