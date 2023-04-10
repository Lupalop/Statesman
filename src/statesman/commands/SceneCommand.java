package statesman.commands;

import statesman.Content;
import statesman.Interpreter;

public class SceneCommand extends Command {

    public static final String ID_SCENE = "scene";

    private String _targetScene;
    private boolean _hasKey;

    private SceneCommand() {
        _targetScene = "";
        _hasKey = false;
    }

    private static Command _defaultInstance;

    public static Command getDefault() {
        if (_defaultInstance == null) {
            _defaultInstance = new SceneCommand();
        }
        return _defaultInstance;
    }

    public SceneCommand(String targetScene) {
        if (targetScene.isEmpty()) {
            throw new IllegalArgumentException();
        }
        _targetScene = targetScene;
        _hasKey = false;
    }

    @Override
    public Command fromText(String commandId, String[] arguments) {
        if (arguments.length == 2) {
            return new SceneCommand(arguments[1]);
        }
        return null;
    }

    @Override
    public void execute() {
        if (_hasKey
                || Content.getScript().getScenes().containsKey(_targetScene)) {
            Interpreter.setScene(
                    Content.getScript().getScenes().get(_targetScene));
            _hasKey = true;
        }
    }

}
