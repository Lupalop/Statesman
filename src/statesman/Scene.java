package statesman;

import java.util.*;

import statesman.commands.*;

public class Scene {

    public static final String CG_ENTRY = "$";

    private String _name;
    private HashMap<String, Command> _actions;
    private HashMap<String, CommandGroup> _commandGroups;
    private HashMap<String, InventoryItem> _items;

    public Scene(String name, HashMap<String, Command> actions,
            HashMap<String, CommandGroup> commandGroups,
            HashMap<String, InventoryItem> items) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException();
        }
        _name = name;
        _actions = actions;
        _commandGroups = commandGroups;
        _items = items;
    }

    public Scene(String name) {
        this(name, new HashMap<String, Command>(),
                new HashMap<String, CommandGroup>(),
                new HashMap<String, InventoryItem>());
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

    public HashMap<String, InventoryItem> getItems() {
        return _items;
    }

    public void runEntry() {
        if (_commandGroups.containsKey(Scene.CG_ENTRY)) {
            _commandGroups.get(Scene.CG_ENTRY).execute();
        }
    }

}
