package statesman;

import java.util.*;
import statesman.actions.*;

public class Scene {

    public static final String entryCommandGroup = "$";
    
    private String _name;
    private HashMap<String, Command> _actions;
    private HashMap<String, CommandGroup> _commandGroups;

    public Scene(String name, HashMap<String, Command> actions, HashMap<String, CommandGroup> commandGroups, ArrayList<Command> entryActions) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException();
        }
        _name = name;
        _actions = actions;
        _commandGroups = commandGroups;
    }

    public Scene(String name) {
        this(name, new HashMap<String, Command>(), new HashMap<String, CommandGroup>(), new ArrayList<Command>());
    }

    public String getName() {
        return _name;
    }

    public HashMap<String, Command> getActions() {
        return _actions;
    }

    public HashMap<String, CommandGroup> getCommandGroups() {
        return _commandGroups;
    }

    public void runEntry() {
        if (_commandGroups.containsKey(Scene.entryCommandGroup)) {
            _commandGroups.get(Scene.entryCommandGroup).execute();
        }
    }

}
