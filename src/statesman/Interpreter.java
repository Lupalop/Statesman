package statesman;

import java.util.*;
import statesman.actions.*;

public class Interpreter {

    public static final int switchSize = 2000;
    
    private static Scanner _scanner;
    private static HashMap<String, Command> _commands;
    private static ContentTuple _source;
    private static Scene _currentScene;
    private static boolean[] _switches;
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
        _commands.put(BreakCommand.Id, new BreakCommand());
        _commands.put(ConditionalJumpCommand.Id, new ConditionalJumpCommand());
        _commands.put(SetSwitchCommand.Id, new SetSwitchCommand());

        _source = null;
        _currentScene = null;
        _switches = new boolean[switchSize];
        Arrays.fill(_switches, false);
    }

    public static Command findCommand(Scene parent, String[] arguments) {
        String commandId = arguments[0].toLowerCase();
        Command command = getCommands().getOrDefault(commandId, null);
        if (command != null) {
            return command.createInstance(parent, arguments);
        }
        return null;
    }

    public static void run() {
        if (_isRunning) {
            System.out.println("The interpreter is already running.");
            return;
        }
        
        if (_source == null) {
            System.out.println("Source is missing.");
            return;
        }
        
        Scene initialScene = _source.getScenes().get("initial");
        if (initialScene == null) {
            System.out.println("Initial scene is missing.");
            return;
        }
        setCurrentScene(initialScene);
        
        _isRunning = true;

        while (_isRunning) {
            System.out.print("> ");
            String currentKeyword = getScanner().nextLine().trim();
            
            if (currentKeyword.isBlank()) {
                continue;
            }
            System.out.println();
            
            Command currentAction = null;

            boolean hasKeyword = _currentScene.getActions().containsKey(currentKeyword);
            if (hasKeyword) {
                currentAction = _currentScene.getActions().get(currentKeyword);
                currentAction.execute();
                continue;
            }
            
            boolean hasGlobalKeyword = _source.getActions().containsKey(currentKeyword);
            if (hasGlobalKeyword) {
                currentAction = _source.getActions().get(currentKeyword);
                currentAction.execute();
                continue;
            }
            
            // Debug mode keywords
            if (App.debugMode) {
                if (currentKeyword.startsWith("tp")) {
                    String[] keywordParts = currentKeyword.split(" ");
                    if (keywordParts.length == 2) {
                        Command tpAction = new SceneCommand(keywordParts[1]);
                        tpAction.execute();
                        continue;
                    }
                }
            }
            
            currentAction = _source.getActions().get("fallback");
            if (currentAction == null && App.debugMode) {
                System.out.println("Fallback message is missing");
            } else {
                currentAction.execute();
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

    public static ContentTuple getSource() {
        return _source;
    }
    
    public static void setSource(ContentTuple source) {
        _source = source;
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

}
