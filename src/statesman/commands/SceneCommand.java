package statesman.commands;

import statesman.Content;
import statesman.Interpreter;

public class SceneCommand extends Command {

    public static final String ID = "scene";

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
    public Command createInstance(String[] arguments) {
        if (arguments.length == 2) {
            return new SceneCommand(arguments[1]);
        }
        return null;
    }

    @Override
    public void execute() {
        if (_hasKey || Content.getScript().getScenes().containsKey(_targetScene)) {
            Interpreter.setScene(Content.getScript().getScenes().get(_targetScene));
            _hasKey = true;
        }
    }

}
