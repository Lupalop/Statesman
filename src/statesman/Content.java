package statesman;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import statesman.commands.*;

public class Content {

    private static boolean _manualSource = false;
    private static boolean _dataParsed = false;
    private static Path _dataPath = null;
    private static GameData _source = null;

    private enum SectionBlock { None, Scene, Group, Actions, Messages, Items };
    
    public static void loadData() throws IOException {
        // Refuse to parse data if the source was set manually
        if (_manualSource) {
            return;
        }
        
        List<String> _data = Files.readAllLines(_dataPath);
        _source = new GameData();

        try {
            Scene currentScene = null;
            CommandGroup currentGroup = null;
            SectionBlock currentSectionBlock = SectionBlock.None;
            boolean blockComment = false;
            ConditionalCommand[] conditionals = new ConditionalCommand[255];
            boolean[] conditionalsElse = new boolean[255];
            int conditionalDepth = 0;
            
            for (int i = 0; i < _data.size(); i++) {
                int lineNumber = i + 1;
                String line = _data.get(i).trim();

                // Block comment start tag
                if (!blockComment && line.startsWith("/*")) {
                    blockComment = true;
                    continue;
                }
                // Block comment end tag
                if (line.startsWith("*/") || line.endsWith("*/")) {
                    blockComment = false;
                    continue;
                }
                // Ignore blank lines and comments
                if (line.isBlank() || line.startsWith("//") || blockComment) {
                    continue;
                }

                // End tag
                if (line.startsWith("end")) {
                    // Terminating conditional blocks
                    if (currentGroup != null && conditionalDepth > 0) {
                        if (conditionalsElse[conditionalDepth]) {
                            conditionalsElse[conditionalDepth] = false;
                        }
                        if (conditionalDepth == 1) {
                            currentGroup
                                    .getCommands()
                                    .add(conditionals[conditionalDepth]);
                        } else {
                            if (conditionalsElse[conditionalDepth - 1]) {
                                conditionals[conditionalDepth - 1]
                                        .getElseGroup()
                                        .getCommands()
                                        .add(conditionals[conditionalDepth]);
                            } else {
                                conditionals[conditionalDepth - 1]
                                        .getGroup()
                                        .getCommands()
                                        .add(conditionals[conditionalDepth]);
                            }
                        }
                        conditionalDepth--;
                        continue;
                    }
                    // Terminating section blocks
                    switch (currentSectionBlock) {
                    case Group:
                        if (currentGroup == null)
                        {
                            throw new GameException("Ending an invalid group in line " + lineNumber);
                        }
                        // Global command groups
                        if (currentScene == null) {
                            _source.getCommandGroups().put(currentGroup.getName(), currentGroup);
                        // Local scene command groups
                        } else {
                            currentScene.getCommandGroups().put(currentGroup.getName(), currentGroup);
                        }
                        currentGroup = null;
                    case Actions:
                    case Messages:
                    case Items:
                        if (currentScene != null) {
                            currentSectionBlock = SectionBlock.Scene;
                        } else {
                            currentSectionBlock = SectionBlock.None;
                        }
                        continue;
                    case Scene:
                        if (currentScene == null)
                        {
                            throw new GameException("Ending an invalid scene in line " + lineNumber);
                        }
                        _source.getScenes().put(currentScene.getName(), currentScene);
                        currentScene = null;
                        currentSectionBlock = SectionBlock.None;
                        continue;
                    case None:
                    default:
                        throw new GameException("Stray end tag in line " + lineNumber);
                    }
                }
                
                if (currentSectionBlock == SectionBlock.Group) {
                    String[] sectionParts = line.split(" ");

                    if (sectionParts.length == 1) {
                        switch (sectionParts[0]) {
                        case "else":
                            if (conditionals[conditionalDepth] == null || conditionalsElse[conditionalDepth]) {
                                throw new GameException("Stray else tag in line " + lineNumber);
                            }
                            conditionalsElse[conditionalDepth] = true;
                            continue;
                        default:
                            break;
                        }
                    }
                    
                    if (sectionParts.length == 2) {
                        switch (sectionParts[0]) {
                        case "if":
                            if (currentGroup == null) {
                                throw new GameException("Conditional sections are only allowed inside command groups, see line " + lineNumber);
                            }
                            conditionalDepth++;
                            conditionals[conditionalDepth] = (ConditionalCommand)Interpreter
                                    .getCommands()
                                    .get(SwitchConditionalCommand.Id)
                                    .createInstance(sectionParts);
                            continue;
                        case "if_inv":
                            if (currentGroup == null) {
                                throw new GameException("Conditional sections are only allowed inside command groups, see line " + lineNumber);
                            }
                            conditionalDepth++;
                            conditionals[conditionalDepth] = (ConditionalCommand)Interpreter
                                    .getCommands()
                                    .get(InventoryConditionalCommand.Id)
                                    .createInstance(sectionParts);
                            continue;
                        default:
                            break;
                        }
                    }
                }
                
                // Section tag
                if (currentSectionBlock == SectionBlock.None ||
                    currentSectionBlock == SectionBlock.Scene) {
                    String[] sectionParts = line.split(" ");
                    
                    // Section tags without parameters
                    if (sectionParts.length == 1) {
                        switch (sectionParts[0]) {
                        case "actions":
                            currentSectionBlock = SectionBlock.Actions;
                            continue;
                        case "messages":
                            currentSectionBlock = SectionBlock.Messages;
                            continue;
                        case "items":
                            if (currentSectionBlock != SectionBlock.Scene) {
                                throw new GameException("The items section MUST be inside a scene section, see line " + lineNumber);
                            }
                            currentSectionBlock = SectionBlock.Items;
                            continue;
                        default:
                            break;
                        }
                    }

                    // Section tags with one (1) parameter
                    if (sectionParts.length == 2) {
                        switch (sectionParts[0]) {
                        case "maxpoints":
                            if (currentSectionBlock == SectionBlock.Scene) {
                                throw new GameException("The maxpoints section tag MUST NOT appear inside a scene section block, see line " + lineNumber);
                            }
                            int maxPoints = Integer.parseInt(sectionParts[1]);
                            if (maxPoints < 0) {
                                throw new GameException("The maximum number of points must be greater than or equal to zero, see line " + lineNumber);
                            }
                            _source.setMaxPoints(maxPoints);
                            continue;
                        case "switches":
                            if (currentSectionBlock == SectionBlock.Scene) {
                                throw new GameException("The switchsize section tag MUST NOT appear inside a scene section block, see line " + lineNumber);
                            }
                            int switchSize = Integer.parseInt(sectionParts[1]);
                            if (switchSize < 0) {
                                throw new GameException("The maximum number of switches must be greater than or equal to zero, see line " + lineNumber);
                            }
                            _source.setSwitchSize(switchSize);
                            continue;
                        case "scene":
                            // Nested scenes
                            if (currentScene != null) {
                                throw new GameException("Nested scenes are not allowed, see line " + lineNumber);
                            }
                            String sceneName = sectionParts[1];
                            // Missing keys (empty/blank)
                            if (sceneName.isBlank()) {
                                throw new GameException("Missing scene name in line " + lineNumber);
                            }
                            // Scene name already in use
                            if (_source.getScenes().containsKey(sceneName)) {
                                throw new GameException("Scene name already in use was specified in line " + lineNumber);
                            }
                            currentScene = new Scene(sceneName);
                            currentSectionBlock = SectionBlock.Scene;
                            continue;
                        case "group":
                            // Nested groups
                            if (currentGroup != null) {
                                throw new GameException("Nested command groups are not allowed, see line " + lineNumber);
                            }
                            String groupName = sectionParts[1];
                            // Missing keys
                            if (groupName.isBlank()) {
                                throw new GameException("Missing command group name in line " + lineNumber);
                            }
                            if (currentScene == null) {
                                // Reserved group name (command ran on entry)
                                if (groupName.equals(Scene.entryCommandGroup)) {
                                    throw new GameException("Use of reserved command group name in line " + lineNumber);
                                }
                            } else {
                                // Group name already in use locally
                                // Overriding global command groups are allowed
                                if (currentScene.getCommandGroups().containsKey(groupName)) {
                                    throw new GameException("Command group name already in use locally was specified in line " + lineNumber);
                                }
                            }
                            currentGroup = new CommandGroup(groupName);
                            currentSectionBlock = SectionBlock.Group;
                            continue;
                        default:
                            break;
                        }
                    }

                    // Invalid or unknown section tag
                    throw new GameException("Invalid or unknown section tag in line " + lineNumber);
                }
                
                String[] parts = line.split("\\|");
                
                // Actions section: commands linked to keywords
                if (currentSectionBlock == SectionBlock.Actions) {
                    if (parts.length != 2) {
                        throw new GameException("Incorrect argument count for the action in line " + lineNumber);
                    }
                    String[] keywords = parts[0].toLowerCase().split(",");
                    String[] arguments = parts[1].split(",");
                    Command command = Interpreter.findCommand(arguments);
                    if (command == null) {
                        throw new GameException("Unknown command was referenced by the action in line " + lineNumber);
                    }
                    for (int j = 0; j < keywords.length; j++) {
                        // Global actions
                        if (currentScene == null) {
                            _source.getActions().put(keywords[j], command);
                        // Local scene actions
                        } else {
                            currentScene.getActions().put(keywords[j], command);
                        }
                    }
                    continue;
                }
                
                // Command group section: a group of commands
                if (currentSectionBlock == SectionBlock.Group) {
                    if (currentGroup == null) {
                        throw new GameException("Invalid command group in line " + lineNumber);
                    }
                    String[] arguments = parts[0].split(",");
                    Command command = Interpreter.findCommand(arguments);
                    if (command == null) {
                        throw new GameException("Unknown command was referenced in line " + lineNumber);
                    }
                    if (conditionalDepth > 0) {
                        if (conditionalsElse[conditionalDepth]) {
                            conditionals[conditionalDepth].getElseGroup().getCommands().add(command);
                        } else {
                            conditionals[conditionalDepth].getGroup().getCommands().add(command);
                        }
                    } else {
                        currentGroup.getCommands().add(command);
                    }
                    continue;
                }
                
                // Messages section
                // XXX: messages are always global regardless of placement
                if (currentSectionBlock == SectionBlock.Messages) {
                    if (parts.length != 2) {
                        throw new GameException("Incorrect argument count was specified by the message in line " + lineNumber);
                    }
                    String key = parts[0];
                    String value = parts[1];
                    // Missing keys
                    if (key.isBlank()) {
                        throw new GameException("Missing message key in line " + lineNumber);
                    }
                    // Key already in use 
                    if (_source.getMessages().containsKey(key)) {
                        throw new GameException("Duplicate key was specified by the message in line " + lineNumber);
                    }
                    _source.getMessages().put(key, value);
                    continue;
                }
                
                // Items section
                if (currentSectionBlock == SectionBlock.Items) {
                    if (parts.length != 2) {
                        throw new GameException("Incorrect argument count was specified by the inventory item in line " + lineNumber);
                    }
                    String keyword = parts[0];
                    String description = parts[1];
                    // Missing keys
                    if (keyword.isBlank()) {
                        throw new GameException("Missing inventory item key in line " + lineNumber);
                    }
                    // Key already in use 
                    if (currentScene.getItems().containsKey(keyword)) {
                        throw new GameException("Duplicate key was specified by the inventory item in line " + lineNumber);
                    }
                    InventoryItem item = new InventoryItem(keyword, description, currentScene);
                    currentScene.getItems().put(keyword, item);
                    continue;
                }
                
                throw new GameException("Invalid text in line " + lineNumber);
            }
        } catch (Exception e) {
            if (App.debugMode) {
                e.printStackTrace();
            }
            _source = null;
        }
        _dataParsed = true;
    }

    public static boolean tryLoadData() {
        try {
            loadData();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static GameData getSource() {
        if (!_manualSource && !_dataParsed) {
            tryLoadData();
        }
        return _source;
    }
    
    public static void setSource(GameData source) {
        _manualSource = true;
        _dataPath = null;
        _dataParsed = false;
        _source = source;
    }

    public static Path getDataPath() {
        return _dataPath;
    }

    public static void setDataPath(String location) {
        _manualSource = false;
        _dataPath = Paths.get(location);
        _dataParsed = false;
        _source = null;
    }
    
    public static void saveState(String name) throws IOException {
        String filename = name + ".sav";
        File output = new File(filename);
        PrintWriter writer = new PrintWriter(new FileWriter(output));
        // current scene, points, inventory, switches
        writer.printf("scene %s%n", Interpreter.getCurrentScene().getName());
        writer.printf("points %s%n", Interpreter.getPoints());
        writer.print("switches ");
        for (int i = 0; i < Interpreter.getSwitches().length; i++) {
            writer.print(Interpreter.getSwitches()[i]);
            if (i < Interpreter.getSwitches().length - 1) {
                writer.print(",");
            } else {
                writer.println();
            }
        }
        if (Interpreter.getInventory().size() > 0) {
            writer.print("inventory ");
            Iterator<InventoryItem> inventoryIterator = Interpreter.getInventory().values().iterator();
            while (inventoryIterator.hasNext()) {
                InventoryItem currentItem = inventoryIterator.next();
                writer.print(currentItem.getOwnerScene().getName());
                writer.print(";");
                writer.print(currentItem.getName());
                if (inventoryIterator.hasNext()) {
                    writer.print(",");
                } else {
                    writer.println();
                }
            }
        }
        writer.flush();
        writer.close();
    }
    
    public static void loadState(String name) throws IOException, GameException {
        String filename = name + ".sav";
        Path savePath = Path.of(filename);
        List<String> data = Files.readAllLines(savePath);
        
        boolean sceneFound = false;
        boolean pointsFound = false;
        boolean switchesFound = false;
        boolean inventoryFound = false;
        
        for (int i = 0; i < data.size(); i++) {
            String line = data.get(i);
            String[] lineParts = line.split(" ");
            
            switch (lineParts[0]) {
            case "scene":
                if (sceneFound) {
                    throw new GameException("Invalid save file: multiple declarations of `scene`");
                }
                // Try to get the named scene if it exists
                Scene currentScene = getSource().getScenes().get(lineParts[1]);
                if (currentScene != null) {
                    Interpreter.setCurrentScene(currentScene);
                    sceneFound = true;
                }
                break;
            case "points":
                if (pointsFound) {
                    throw new GameException("Invalid save file: multiple declarations of `points`");
                }
                Interpreter.setPoints(Integer.valueOf(lineParts[1]));
                pointsFound = true;
                break;
            case "switches":
                if (switchesFound) {
                    throw new GameException("Invalid save file: multiple declarations of `switches`");
                }
                String[] boolParts = lineParts[1].split(",");
                for (int j = 0; j < Interpreter.getSwitches().length; j++) {
                    Interpreter.getSwitches()[j] = Boolean.valueOf(boolParts[j]);
                }
                switchesFound = true;
                break;
            case "inventory":
                if (inventoryFound) {
                    throw new GameException("Invalid save file: multiple declarations of `inventory`");
                }
                HashMap<String, InventoryItem> inventory = new HashMap<String, InventoryItem>();
                String[] items = lineParts[1].split(",");
                for (int j = 0; j < items.length; j++) {
                    String[] itemParts = items[j].split(";");
                    // get scene name (first part)
                    Scene targetScene = getSource().getScenes().get(itemParts[0]);
                    // get item name (second part)
                    String itemName = itemParts[1];
                    // If scene exists, try to get the inventory item
                    if (targetScene != null) {
                        InventoryItem item = targetScene.getItems().get(itemName);
                        if (item != null) {
                            Interpreter.getInventory().put(itemName, item);
                        }
                    }
                    // Silently ignore if either the inventory item or scene does not exist 
                }
                inventoryFound = true;
                break;
            default:
                break;
            }
        }
        
        if (!sceneFound) {
            throw new GameException("Invalid save file: missing target scene");
        }
        if (!pointsFound) {
            throw new GameException("Invalid save file: missing points");
        }
        if (!switchesFound) {
            throw new GameException("Invalid save file: missing switches");
        }
    }

}
