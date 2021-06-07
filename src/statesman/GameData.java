package statesman;

import java.util.HashMap;

import statesman.commands.Command;
import statesman.commands.CommandGroup;

public class GameData {

    public static final int defaultSwitchSize = 2000;
    
    private int _maxPoints;
    private int _switchSize;
    private HashMap<String, String> _messages;
    private HashMap<String, Scene> _scenes;
    private HashMap<String, Command> _actions;
    private HashMap<String, CommandGroup> _commandGroups;
    
    public GameData() {
        _maxPoints = 0;
        _switchSize = defaultSwitchSize;
        _messages = new HashMap<String, String>();
        _scenes = new HashMap<String, Scene>();
        _actions = new HashMap<String, Command>();
        _commandGroups = new HashMap<String, CommandGroup>(); 
    }

    public int getMaxPoints() {
        return _maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        if (maxPoints < 0) {
            throw new IllegalArgumentException("The maximum number of points must be greater than or equal to zero");
        }
        _maxPoints = maxPoints;
    }

    public int getSwitchSize() {
        return _switchSize;
    }

    public void setSwitchSize(int switchSize) {
        if (switchSize < 0) {
            throw new IllegalArgumentException("The number of allocated switches must be greater than or equal to zero");
        }
        _switchSize = switchSize;
    }

    public HashMap<String, String> getMessages() {
        return _messages;
    }

    public void setMessages(HashMap<String, String> messages) {
        _messages = messages;
    }

    public HashMap<String, Scene> getScenes() {
        return _scenes;
    }

    public void setScenes(HashMap<String, Scene> scenes) {
        _scenes = scenes;
    }

    public HashMap<String, Command> getActions() {
        return _actions;
    }

    public void setActions(HashMap<String, Command> actions) {
        _actions = actions;
    }

    public HashMap<String, CommandGroup> getCommandGroups() {
        return _commandGroups;
    }

    public void setCommandGroups(HashMap<String, CommandGroup> commandGroups) {
        _commandGroups = commandGroups;
    }
    
    public String getMessage(String key) {
        String template = "[Missing message: `%s`]";
        String defaultValue = String.format(template, key);
        return getMessages().getOrDefault(key, defaultValue);
    }

}
