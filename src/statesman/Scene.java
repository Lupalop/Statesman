package statesman;

import java.util.*;
import statesman.actions.*;

public class Scene {

    private String _name;
    private ArrayList<Action> _actions;
    private HashMap<String, CommandGroup> _groupCommands;

    public Scene(String name, ArrayList<Action> actions, HashMap<String, CommandGroup> groupCommands) {
        _actions = actions;
        _groupCommands = groupCommands;
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException();
        }
        _name = name;
    }

    public Scene(String name) {
        this(name, new ArrayList<Action>(), new HashMap<String, CommandGroup>());
    }

    public String getName() {
        return _name;
    }

    public ArrayList<Action> getActions() {
        return _actions;
    }

    public HashMap<String, CommandGroup> getGroupCommands() {
        return _groupCommands;
    }

}
