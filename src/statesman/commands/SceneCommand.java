package statesman.commands;

import statesman.Interpreter;
import statesman.Scene;

public class SceneCommand implements Command {

    public static final String Id = "scene";

    private String _targetScene;
    private boolean _hasKey;

    public SceneCommand() {
        _targetScene = "";
        _hasKey = false;
    }

    public SceneCommand(String targetScene) {
        if (targetScene.isEmpty()) {
            throw new IllegalArgumentException();
        }
        _targetScene = targetScene;
        _hasKey = false;
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
        if (_hasKey || Interpreter.getSource().getScenes().containsKey(_targetScene)) {
            Interpreter.setCurrentScene(Interpreter.getSource().getScenes().get(_targetScene));
            _hasKey = true;
        }
    }

}
