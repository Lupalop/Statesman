package statesman;

import java.util.HashMap;

import statesman.commands.Command;
import statesman.commands.Function;

public class Script {

    private int _maxPoints;
    private HashMap<String, String> _messages;
    private HashMap<String, Scene> _scenes;
    private HashMap<String, Command> _actions;
    private HashMap<String, Function> _functions;

    public Script() {
        _maxPoints = 0;
        _messages = new HashMap<>();
        _scenes = new HashMap<>();
        _actions = new HashMap<>();
        _functions = new HashMap<>();
    }

    public int getMaxPoints() {
        return _maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        if (maxPoints < 0) {
            throw new IllegalArgumentException(
                    "The maximum number of points must be greater than or equal to zero");
        }
        _maxPoints = maxPoints;
    }

    public HashMap<String, String> getMessages() {
        return _messages;
    }

    public HashMap<String, Scene> getScenes() {
        return _scenes;
    }

    public HashMap<String, Command> getActions() {
        return _actions;
    }

    public HashMap<String, Function> getFunctions() {
        return _functions;
    }

    public String findMessage(String key, boolean replaceMissing) {
        String messageValue = getMessages().get(key);
        if (messageValue != null) {
            if (messageValue.startsWith("@")) {
                messageValue = messageValue.replace("\\e", "\033");
            }
            getMessages().put(key, messageValue);
        } else if (replaceMissing) {
            String template = "[Missing message: `%s`]";
            messageValue = String.format(template, key);
        }

        if (messageValue == null) {
            return key;
        }
        return messageValue;
    }

    public String findMessage(String key) {
        return findMessage(key, true);
    }

}
