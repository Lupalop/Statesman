package statesman;

import java.util.*;
import statesman.actions.*;

public class Scene {

    private String _name;
    private HashMap<String, Command> _actions;
    private HashMap<String, CommandGroup> _groupCommands;
    private ArrayList<Command> _entryActions;

    public Scene(String name, HashMap<String, Command> actions, HashMap<String, CommandGroup> groupCommands, ArrayList<Command> entryActions) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException();
        }
        _name = name;
        _actions = actions;
        _groupCommands = groupCommands;
        _entryActions = entryActions;
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

    public ArrayList<Command> getEntryActions() {
        return _entryActions;
    }
    
    public void runEntryActions() {
        Iterator<Command> iterator = _entryActions.iterator();
        while (iterator.hasNext()) {
            Command currentCommand = iterator.next();
            currentCommand.execute();
        }
    }

}
