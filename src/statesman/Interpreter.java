package statesman;

import java.util.*;

import statesman.commands.*;

public class Interpreter {

    private static Scanner _scanner;
    private static HashMap<String, Command> _commands;
    private static Scene _scene;
    private static boolean[] _switches;
    private static HashMap<String, InventoryItem> _inventory;
    private static int _points;
    private static boolean _isRunning = false;

    static {
        _commands = new HashMap<String, Command>();
        // XXX: manually input IDs of new commands here!
        _commands.put(PrintCommand.ID, new PrintCommand());
        _commands.put(PrintRandomCommand.ID, new PrintRandomCommand());
        _commands.put(PrintCombineCommand.ID, new PrintCombineCommand());
        _commands.put(SceneCommand.ID, new SceneCommand());
        _commands.put(GotoCommand.ID, new GotoCommand());
        _commands.put(JumpCommand.ID, new JumpCommand());
        _commands.put(ReturnCommand.ID, new ReturnCommand());
        _commands.put(SwitchSetCommand.ID, new SwitchSetCommand());
        _commands.put(SwitchJumpCommand.ID, new SwitchJumpCommand());
        _commands.put(SwitchConditionalCommand.ID, new SwitchConditionalCommand());
        _commands.put(InventoryCommand.ID, new InventoryCommand());
        _commands.put(InventoryJumpCommand.ID, new InventoryJumpCommand());
        _commands.put(InventoryConditionalCommand.ID, new InventoryConditionalCommand());
        _commands.put(PointsCommand.ID, new PointsCommand());
        _commands.put(QuitCommand.ID, new QuitCommand());
        _commands.put(SaveCommand.ID, new SaveCommand());
        _commands.put(LoadCommand.ID, new LoadCommand());
        _commands.put(GotoBaseCommand.ID, new GotoBaseCommand());

        _scene = null;
        _inventory = new HashMap<String, InventoryItem>();
    }

    public static Command findCommand(String[] arguments) {
        String commandId = arguments[0].toLowerCase();
        Command command = getCommands().getOrDefault(commandId, null);
        if (command != null) {
            return command.createInstance(arguments);
        }
        return null;
    }
    
    public static Command findAction(String keyword) {
        Command localAction = _scene.getActions().get(keyword);
        Command globalAction = Content.getScript().getActions().get(keyword);
        Command localFallbackAction = _scene.getActions().get("fallback");
        Command fallbackAction = Content.getScript().getActions().get("fallback");
        
        if (localAction != null) {
            return localAction;
        } else if (globalAction != null) {
            return globalAction;
        } else if (localFallbackAction != null) {
            return localFallbackAction;
        } else if (fallbackAction != null) {
            return fallbackAction;
        } else if (App.debugMode) {
            System.out.println("Fallback message is missing");
        }
        
        return null;
    }

    public static void run() {
        if (_isRunning) {
            System.out.println("The interpreter is already running.");
            return;
        }
        
        if (Content.getScript() == null) {
            System.out.println("The game script is missing.");
            return;
        }

        _switches = new boolean[Content.getScript().getSwitchSize()];
        Arrays.fill(_switches, false);

        Scene initialScene = Content.getScript().getScenes().get("initial");
        if (initialScene == null) {
            System.out.println("Initial scene is missing.");
            return;
        }
        setScene(initialScene);
        
        _isRunning = true;

        while (_isRunning) {
            System.out.print("> ");
            String keyword = getScanner().nextLine().trim().toLowerCase();
            
            if (keyword.isBlank()) {
                continue;
            }
            
            System.out.println();

            Command currentAction = findAction(keyword);

            // Debug mode keywords
            try {
                if (App.debugMode) {
                    String[] keywordParts = keyword.split(" ");
                    if (keyword.startsWith("*tp")) {
                        currentAction = _commands.get(SceneCommand.ID).createInstance(keywordParts);
                    }
                    if (keyword.startsWith("*set")) {
                        currentAction = _commands.get(SwitchSetCommand.ID).createInstance(keywordParts);
                    }
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Invalid arguments.");
            }

            if (currentAction != null) {
                currentAction.execute();
                continue;
            }
        }
    }
    
    public static void stop() {
        _isRunning = false;
    }

    public static Scanner getScanner() {
        return _scanner;
    }

    public static void setScanner(Scanner scanner) {
        _scanner = scanner;
    }

    public static HashMap<String, Command> getCommands() {
        return _commands;
    }

    public static Scene getScene() {
        return _scene;
    }

    public static void setScene(Scene scene) {
        _scene = scene;
        _scene.runEntry();
    }

    public static boolean[] getSwitches() {
        return _switches;
    }

    public static HashMap<String, InventoryItem> getInventory() {
        return _inventory;
    }

    public static void setInventory(HashMap<String, InventoryItem> inventory) {
        _inventory = inventory;
    }

    public static int getPoints() {
        return _points;
    }

    public static void setPoints(int points) {
        _points = points;
    }

}
