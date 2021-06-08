package statesman;

import java.util.*;

import statesman.commands.*;

public class Interpreter {

    private static Scanner _scanner;
    private static HashMap<String, Command> _commands;
    private static Scene _currentScene;
    private static boolean[] _switches;
    private static HashMap<String, InventoryItem> _inventory;
    private static int _points;
    private static boolean _isRunning = false;

    static {
        _commands = new HashMap<String, Command>();
        // XXX: manually input IDs of new commands here!
        _commands.put(PrintCommand.Id, new PrintCommand());
        _commands.put(PrintRandomCommand.Id, new PrintRandomCommand());
        _commands.put(PrintCombineCommand.Id, new PrintCombineCommand());
        _commands.put(SceneCommand.Id, new SceneCommand());
        _commands.put(GotoCommand.Id, new GotoCommand());
        _commands.put(JumpCommand.Id, new JumpCommand());
        _commands.put(ReturnCommand.Id, new ReturnCommand());
        _commands.put(SwitchSetCommand.Id, new SwitchSetCommand());
        _commands.put(SwitchJumpCommand.Id, new SwitchJumpCommand());
        _commands.put(SwitchConditionalCommand.Id, new SwitchConditionalCommand());
        _commands.put(InventoryCommand.Id, new InventoryCommand());
        _commands.put(InventoryJumpCommand.Id, new InventoryJumpCommand());
        _commands.put(InventoryConditionalCommand.Id, new InventoryConditionalCommand());
        _commands.put(PointsCommand.Id, new PointsCommand());
        _commands.put(QuitCommand.Id, new QuitCommand());
        _commands.put(SaveCommand.Id, new SaveCommand());
        _commands.put(LoadCommand.Id, new LoadCommand());

        _currentScene = null;
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
        Command localAction = _currentScene.getActions().get(keyword);
        Command globalAction = Content.getScript().getActions().get(keyword);
        Command localFallbackAction = _currentScene.getActions().get("fallback");
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
        setCurrentScene(initialScene);
        
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
                        currentAction = _commands.get(SceneCommand.Id).createInstance(keywordParts);
                    }
                    if (keyword.startsWith("*set")) {
                        currentAction = _commands.get(SwitchSetCommand.Id).createInstance(keywordParts);
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

    public static Scene getCurrentScene() {
        return _currentScene;
    }

    public static void setCurrentScene(Scene scene) {
        _currentScene = scene;
        _currentScene.runEntry();
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
