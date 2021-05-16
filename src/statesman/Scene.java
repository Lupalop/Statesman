package statesman;

import java.util.*;
import statesman.actions.*;

public class Scene {

    public static final String entryCommandGroup = "$";
    
    private String _name;
    private HashMap<String, Command> _actions;
    private HashMap<String, CommandGroup> _groupCommands;

    public Scene(String name, HashMap<String, Command> actions, HashMap<String, CommandGroup> groupCommands, ArrayList<Command> entryActions) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException();
        }
        _name = name;
        _actions = actions;
        _groupCommands = groupCommands;
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

    public HashMap<String, CommandGroup> getGroupCommands() {
        return _groupCommands;
    }

    public void runEntry() {
        if (_groupCommands.containsKey(Scene.entryCommandGroup)) {
            _groupCommands.get(Scene.entryCommandGroup).execute();
        }
    }

}
