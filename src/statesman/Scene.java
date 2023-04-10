package statesman;

import java.util.*;

import statesman.commands.*;

public class Scene {

    public static final String FN_ENTRY = "$";

    private String _name;
    private HashMap<String, Command> _actions;
    private HashMap<String, Function> _functions;
    private HashMap<String, InventoryItem> _items;

    public Scene(String name, HashMap<String, Command> actions,
            HashMap<String, Function> functions,
            HashMap<String, InventoryItem> items) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException();
        }
        _name = name;
        _actions = actions;
        _functions = functions;
        _items = items;
    }

    public Scene(String name) {
        this(name, new HashMap<String, Command>(),
                new HashMap<String, Function>(),
                new HashMap<String, InventoryItem>());
    }

    public String getName() {
        return _name;
    }

    public HashMap<String, Command> getActions() {
        return _actions;
    }

    public HashMap<String, Function> getFunctions() {
        return _functions;
    }

    public HashMap<String, InventoryItem> getItems() {
        return _items;
    }

    public void runEntry() {
        if (_functions.containsKey(Scene.FN_ENTRY)) {
            _functions.get(Scene.FN_ENTRY).execute();
        }
    }

}
