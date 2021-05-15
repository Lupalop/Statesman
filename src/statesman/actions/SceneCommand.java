package statesman.actions;

import statesman.Interpreter;
import statesman.Scene;

public class SceneCommand implements Command {

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
        if (Interpreter.getScenes().containsKey(_targetScene)) {
            Interpreter.setCurrentScene(Interpreter.getScenes().get(_targetScene));
        }
    }

}
