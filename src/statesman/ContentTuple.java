package statesman;

import java.util.HashMap;
import statesman.actions.Command;
import statesman.actions.CommandGroup;

public class ContentTuple {

    private HashMap<String, String> _messages;
    private HashMap<String, Scene> _scenes;
    private HashMap<String, Command> _actions;
    private HashMap<String, CommandGroup> _commandGroups;
    
    public ContentTuple() {
        _messages = new HashMap<String, String>();
        _scenes = new HashMap<String, Scene>();
        _actions = new HashMap<String, Command>();
        _commandGroups = new HashMap<String, CommandGroup>(); 
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
