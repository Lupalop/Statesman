package statesman;

import java.util.HashMap;

import statesman.commands.Command;
import statesman.commands.CommandGroup;

public class GameData {

    private int _maxPoints;
    private HashMap<String, String> _messages;
    private HashMap<String, Scene> _scenes;
    private HashMap<String, Command> _actions;
    private HashMap<String, CommandGroup> _commandGroups;
    
    public GameData() {
        _maxPoints = 0;
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
        this._maxPoints = maxPoints;
    }

    public HashMap<String, String> getMessages() {
        return _messages;
    }

    public void setMessages(HashMap<String, String> _messages) {
        this._messages = _messages;
    }

    public HashMap<String, Scene> getScenes() {
        return _scenes;
    }

    public void setScenes(HashMap<String, Scene> _scenes) {
        this._scenes = _scenes;
    }

    public HashMap<String, Command> getActions() {
        return _actions;
    }

    public void setActions(HashMap<String, Command> _actions) {
        this._actions = _actions;
    }

    public HashMap<String, CommandGroup> getCommandGroups() {
        return _commandGroups;
    }

    public void setCommandGroups(HashMap<String, CommandGroup> _commandGroups) {
        this._commandGroups = _commandGroups;
    }

}
