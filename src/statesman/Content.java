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

public class Content {

    private static Path _dataPath = null;
    private static boolean _manualScript = false;
    private static boolean _scriptParsed = false;
    private static Script _script = null;
    private static ScriptParser _parser = null;

    public static void loadData() throws IOException, GameException {
        // Refuse to parse data if the script was set manually
        if (_manualScript) {
            return;
        }

        List<String> data = Files.readAllLines(_dataPath);
        _parser = new ScriptParser(data);
        
        _script = _parser.read();
        _scriptParsed = true;
    }

    public static boolean tryLoadData() {
        try {
            loadData();
        } catch (Exception e) {
            if (App.debugMode) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public static Script getScript() {
        if (!_manualScript && !_scriptParsed) {
            tryLoadData();
        }
        return _script;
    }
    
    public static void setScript(Script script) {
        _manualScript = true;
        _dataPath = null;
        _scriptParsed = false;
        _script = script;
    }

    public static Path getDataPath() {
        return _dataPath;
    }

    public static void setDataPath(String location) {
        _manualScript = false;
        _dataPath = Paths.get(location);
        _scriptParsed = false;
        _script = null;
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
                Scene currentScene = getScript().getScenes().get(lineParts[1]);
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
                    Scene targetScene = getScript().getScenes().get(itemParts[0]);
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
