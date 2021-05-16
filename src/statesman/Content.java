package statesman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import statesman.actions.*;

public class Content {

    private static boolean _dataParsed;
    private static Path _dataPath;
    private static List<String> _data;
    private static ContentTuple _source;

    static {
        _dataParsed = false;
        _dataPath = null;
        _data = null;
        _source = null;
    }

    public static void loadData() throws IOException {
        _data = Files.readAllLines(_dataPath);
    }
    
    public static boolean tryLoadData() {
        try {
            loadData();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private enum SectionBlock { None, Scene, Group, Actions, Messages };
    
    private static void parseData() {
        _source = new ContentTuple();

        try {
            Scene currentScene = null;
            CommandGroup currentGroup = null;
            SectionBlock currentSectionBlock = SectionBlock.None;
            boolean blockComment = false;
            
            for (int i = 0; i < _data.size(); i++) {
                int lineNumber = i + 1;
                String line = _data.get(i).trim();

                // Block comment start tag
                if (!blockComment && line.startsWith("/*")) {
                    blockComment = true;
                    continue;
                }
                // Block comment end tag
                if (line.startsWith("*/")) {
                    blockComment = false;
                    continue;
                }
                // Ignore blank lines and comments
                if (line.isBlank() || line.startsWith("//") || blockComment) {
                    continue;
                }

                // End tag: used for closing section blocks
                if (line.startsWith("end")) {
                    switch (currentSectionBlock) {
                    case Group:
                        if (currentGroup == null)
                        {
                            throw new MalformedResourceException("Ending an invalid group in line " + lineNumber);
                        }
                        // Global command groups
                        if (currentScene == null) {
                            _source.getCommandGroups().put(currentGroup.getName(), currentGroup);
                        // Local scene command groups
                        } else {
                            currentScene.getGroupCommands().put(currentGroup.getName(), currentGroup);
                        }
                        currentGroup = null;
                    case Actions:
                    case Messages:
                        if (currentScene != null) {
                            currentSectionBlock = SectionBlock.Scene;
                        } else {
                            currentSectionBlock = SectionBlock.None;
                        }
                        continue;
                    case Scene:
                        if (currentScene == null)
                        {
                            throw new MalformedResourceException("Ending an invalid scene in line " + lineNumber);
                        }
                        _source.getScenes().put(currentScene.getName(), currentScene);
                        currentScene = null;
                        currentSectionBlock = SectionBlock.None;
                        continue;
                    case None:
                    default:
                        throw new MalformedResourceException("Stray end tag in line " + lineNumber);
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
                        default:
                            break;
                        }
                    }
                    
                    // Section tags with one (1) parameter
                    if (sectionParts.length == 2) {
                        switch (sectionParts[0]) {
                        case "scene":
                            // Nested scenes
                            if (currentScene != null) {
                                throw new MalformedResourceException("Nested scenes are not allowed, see line " + lineNumber);
                            }
                            String sceneName = sectionParts[1];
                            // Missing keys (empty/blank)
                            if (sceneName.isBlank()) {
                                throw new MalformedResourceException("Missing scene name in line " + lineNumber);
                            }
                            // Scene name already in use
                            if (_source.getScenes().containsKey(sceneName)) {
                                throw new MalformedResourceException("Scene name already in use was specified in line " + lineNumber);
                            }
                            currentScene = new Scene(sceneName);
                            currentSectionBlock = SectionBlock.Scene;
                            continue;
                        case "group":
                            // Nested groups
                            if (currentGroup != null) {
                                throw new MalformedResourceException("Nested command groups are not allowed, see line " + lineNumber);
                            }
                            String groupName = sectionParts[1];
                            // Missing keys
                            if (groupName.isBlank()) {
                                throw new MalformedResourceException("Missing command group name in line " + lineNumber);
                            }
                            if (currentScene == null) {
                                // Reserved group name (command ran on entry)
                                if (groupName.equals(Scene.entryCommandGroup)) {
                                    throw new MalformedResourceException("Use of reserved command group name in line " + lineNumber);
                                }
                            } else {
                                // Group name already in use locally
                                // Overriding global command groups are allowed
                                if (currentScene.getGroupCommands().containsKey(groupName)) {
                                    throw new MalformedResourceException("Command group name already in use locally was specified in line " + lineNumber);
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
                    throw new MalformedResourceException("Invalid or unknown section tag in line " + lineNumber);
                }
                
                String[] parts = line.split("\\|");
                
                // Actions section: commands linked to keywords
                if (currentSectionBlock == SectionBlock.Actions) {
                    if (parts.length != 2) {
                        throw new MalformedResourceException("Incorrect argument count for the action in line " + lineNumber);
                    }
                    String[] keywords = parts[0].split(",");
                    String[] arguments = parts[1].split(",");
                    Command command = Interpreter.findCommand(currentScene, arguments);
                    if (command == null) {
                        throw new MalformedResourceException("Unknown command was referenced by the action in line " + lineNumber);
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
                        throw new MalformedResourceException("Invalid command group in line " + lineNumber);
                    }
                    String[] arguments;
                    // With line numbers
                    if (parts.length == 2) {
                        arguments = parts[1].split(",");
                    // Without line numbers
                    } else if (parts.length == 1) {
                        arguments = parts[0].split(",");
                    // Weird argument count
                    } else {
                        throw new MalformedResourceException("Incorrect argument count was specified by the command in line " + lineNumber);
                    }
                    Command command = Interpreter.findCommand(currentScene, arguments);
                    if (command == null) {
                        throw new MalformedResourceException("Unknown command was referenced in line " + lineNumber);
                    }
                    currentGroup.getCommands().add(command);
                    continue;
                }
                
                // Messages section
                // XXX: messages are always global regardless of placement
                if (currentSectionBlock == SectionBlock.Messages) {
                    if (parts.length != 2) {
                        throw new MalformedResourceException("Incorrect argument count was specified by the message in line " + lineNumber);
                    }
                    String key = parts[0];
                    String value = parts[1];
                    // Missing keys
                    if (key.isBlank()) {
                        throw new MalformedResourceException("Missing message key in line " + lineNumber);
                    }
                    // Key already in use 
                    if (_source.getMessages().containsKey(key)) {
                        throw new MalformedResourceException("Duplicate key was specified by the message in line " + lineNumber);
                    }
                    _source.getMessages().put(key, value);
                    continue;
                }
                
                throw new MalformedResourceException("Invalid text in line " + lineNumber);
            }
        } catch (Exception e) {
            if (App.debugMode) {
                e.printStackTrace();
            }
            _source = null;
        }
        _dataParsed = true;
    }
    
    public static ContentTuple getSource() {
        if (!_dataParsed) {
            parseData();
        }
        return _source;
    }

    public static Path getDataPath() {
        return _dataPath;
    }

    public static void setDataPath(String location) {
        _dataPath = Paths.get(location);
        _dataParsed = false;
        _data = null;
        _source = null;
    }

    public static List<String> getData() {
        return _data;
    }

}
