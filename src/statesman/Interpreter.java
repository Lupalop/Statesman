package statesman;

import java.util.*;

import statesman.commands.*;

public class Interpreter {

    private static Scanner _scanner;
    private static Scene _scene;
    private static HashMap<String, Boolean> _switches;
    private static HashMap<String, InventoryItem> _inventory;
    private static int _points;
    private static boolean _isRunning = false;

    static {
        _scene = null;
    }

    public static Command findAction(String keyword) {
        Command localAction = _scene.getActions().get(keyword);
        Command globalAction = Content.getScript().getActions().get(keyword);
        Command localFallbackAction = _scene.getActions().get("fallback");
        Command fallbackAction = Content.getScript().getActions()
                .get("fallback");

        if (localAction != null) {
            return localAction;
        } else if (globalAction != null) {
            return globalAction;
        } else if (localFallbackAction != null) {
            return localFallbackAction;
        } else if (fallbackAction != null) {
            return fallbackAction;
        } else if (Program.debugMode) {
            System.out.println("Fallback message is missing");
        }

        return null;
    }

    public static void run(String initialSceneName) {
        if (_isRunning) {
            System.out.println("The interpreter is already running.");
            return;
        }

        if (Content.getScript() == null) {
            System.out.println("The game script is missing.");
            return;
        }

        _inventory = new HashMap<String, InventoryItem>();
        _switches = new HashMap<String, Boolean>();

        if (initialSceneName == null) {
            initialSceneName = "initial";
        }
        Scene initialScene = Content.getScript().getScenes()
                .get(initialSceneName);
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
                if (Program.debugMode) {
                    String[] keywordParts = keyword.split(" ");
                    if (keyword.startsWith("*tp")) {
                        currentAction = SceneCommand.getDefault().fromText(null,
                                keywordParts);
                    }
                    if (keyword.startsWith("*set")) {
                        currentAction = SwitchSetCommand.getDefault()
                                .fromText(null, keywordParts);
                    }
                    if (keyword.equalsIgnoreCase("*reload")) {
                        String location = Content.getDataPath().toString();
                        String oldScene = getScene().getName();
                        Program.initialize(location, oldScene);
                    }
                    if (keyword.equalsIgnoreCase("*restart")) {
                        String location = Content.getDataPath().toString();
                        Program.initialize(location);
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

    public static void run() {
        run(null);
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

    public static Scene getScene() {
        return _scene;
    }

    public static void setScene(Scene scene) {
        _scene = scene;
        _scene.runEntry();
    }

    public static HashMap<String, Boolean> getSwitches() {
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

    public static boolean isRunning() {
        return _isRunning;
    }

}
