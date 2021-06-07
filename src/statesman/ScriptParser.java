package statesman;

import java.util.List;

import statesman.commands.*;

public class ScriptParser {

    public static final int maxDepth = 1024;
    
    private enum Section {
        None, Root, Scene, Group, Actions, Messages, Items
    };

    // Data fields
    private List<String> _data;
    private String _line;
    private int _lineNumber;
    private Script _source;
    
    // Parser fields
    private Section _section;
    private Scene _scene;
    private CommandGroup _group;
    private boolean _blockComment;

    private ConditionalCommand[] _conditionals;
    private boolean[] _conditionalsElse;
    private int _depth;

    private ScriptParser() {
        _data = null;
        _line = "";
        _lineNumber = 0;
        _source = null;
        
        _section = Section.Root;
        _depth = 0;
        _scene = null;
        _group = null;
        _blockComment = false;
        
        _conditionals = new ConditionalCommand[maxDepth];
        _conditionalsElse = new boolean[maxDepth];
    }
    
    public ScriptParser(List<String> data) {
        this();
        _data = data;
    }

    public Script read() throws GameException {
        _source = new Script();
        
        for (int i = 0; i < _data.size(); i++) {
            _lineNumber = i + 1;
            _line = _data.get(i).trim();

            if (isComment()) {
                continue;
            }
            if (isTerminator()) {
                continue;
            }
            if (isPreference()) {
                continue;
            }
            if (isSection()) {
                continue;
            }
            if (isInnerSection()) {
                continue;
            }

            throw new GameException("Invalid string in line " + _lineNumber);
        }
        
        return _source;
    }
    
    private boolean isComment() {
        // Block comment start tag
        if (_line.startsWith("/*")) {
            _blockComment = true;
            return true;
        }
        // Block comment end tag
        if (_line.startsWith("*/") || _line.endsWith("*/")) {
            _blockComment = false;
            return true;
        }
        // Ignore blank lines and comments
        if (_line.isBlank() || _line.startsWith("//") || _blockComment) {
            return true;
        }
        return false;
    }
    
    private boolean isTerminator() throws GameException {
        if (_line.startsWith("end")) {
            switch (_section) {
            case Root:
            case None:
                throw new GameException("Stray end tag in line " + _lineNumber);
            case Scene:
                _scene = null;
                break;
            case Group:
                if (_depth > 0) {
                    _conditionalsElse[_depth] = false;
                    _conditionals[_depth] = null;
                    _depth--;
                    return true;
                } else {
                    _group = null;
                }
                break;
            default:
                break;
            }
            if (_scene != null) {
                _section = Section.Scene;
            } else {
                _section = Section.Root;
            }
            return true;
        }
        return false;
    }
    
    private boolean isPreference() throws GameException {
        String[] parts = _line.split(" ");
        if (_section == Section.Root && parts.length == 2) {
            switch (parts[0]) {
            case "maxpoints":
                int maxPoints = Integer.parseInt(parts[1]);
                if (maxPoints < 0) {
                    throw new GameException("The maximum number of points must be greater than or equal to zero, see line " + _lineNumber);
                }
                _source.setMaxPoints(maxPoints);
                break;
            case "switches":
                int switchSize = Integer.parseInt(parts[1]);
                if (switchSize < 0) {
                    throw new GameException("The maximum number of switches must be greater than or equal to zero, see line " + _lineNumber);
                }
                _source.setSwitchSize(switchSize);
                break;
            default:
                return false;
            }
            return true;
        }
        return false;
    }
    
    private boolean isSection() throws GameException {
        String[] parts = _line.split(" ");
        if (_section == Section.Root || _section == Section.Scene) {
            switch (parts[0]) {
            case "actions":
                _section = Section.Actions;
                break;
            case "group":
                if (parts.length == 2) {
                    _group = new CommandGroup(parts[1]);
                    if (_section == Section.Root) {
                        // Reserved group name (command ran on entry)
                        if (_group.getName().equals(Scene.entryCommandGroup)) {
                            throw new GameException("Use of reserved command group name, see line " + _lineNumber);
                        }
                        _source.getCommandGroups().put(_group.getName(), _group);
                    } else {
                        // Group name already in use locally
                        if (_scene.getCommandGroups().containsKey(_group.getName())) {
                            throw new GameException("Command group name already used in current scene, see line " + _lineNumber);
                        }
                        _scene.getCommandGroups().put(_group.getName(), _group);
                    }
                    _section = Section.Group;
                } else {
                    throw new GameException("Invalid group section tag, see line " + _lineNumber);
                }
                break;
            case "items":
                if (_section != Section.Scene) {
                    throw new GameException("The items section MUST be inside a scene section, see line " + _lineNumber);
                }
                _section = Section.Items;
                break;
            case "messages":
                _section = Section.Messages;
                break;
            case "scene":
                // Nested scenes
                if (_section == Section.Scene || _scene != null) {
                    throw new GameException("Nested scenes are not allowed, see line " + _lineNumber);
                }
                if (parts.length == 2) {
                    _scene = new Scene(parts[1]);
                    // Scene name already in use
                    if (_source.getScenes().containsKey(_scene.getName())) {
                        throw new GameException("Specified scene name is already in use, see line " + _lineNumber);
                    }
                    _source.getScenes().put(_scene.getName(), _scene);
                    _section = Section.Scene;
                } else {
                    throw new GameException("Invalid scene section tag, see line " + _lineNumber);
                }
                break;
            default:
                throw new GameException("Invalid or unknown section tag, see line " + _lineNumber);
            }
            return true;
        }
        
        if (_section == Section.Group) {
            if (parts[0].equals("else")) {
                if (_conditionals[_depth] == null || _conditionalsElse[_depth]) {
                    throw new GameException("Stray else tag, see line " + _lineNumber);
                }
                _conditionalsElse[_depth] = true;
                return true;
            }
            if (parts.length == 2) {
                ConditionalCommand instance = null;
                if (parts[0].equals("if")) {
                    instance = (ConditionalCommand)Interpreter
                            .getCommands()
                            .get(SwitchConditionalCommand.Id)
                            .createInstance(parts);
                } else if (parts[0].equals("if_inv")) {
                    instance = (ConditionalCommand)Interpreter
                            .getCommands()
                            .get(InventoryConditionalCommand.Id)
                            .createInstance(parts);
                }
                if (instance != null) {
                    _depth++;
                    _conditionals[_depth] = instance;
                    if (_section == Section.Group) {
                        _group.getCommands().add(instance);
                    } else if (_conditionalsElse[_depth]) {
                        _conditionals[_depth].getElseGroup().getCommands().add(instance);
                    } else {
                        _conditionals[_depth].getGroup().getCommands().add(instance);
                    }
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean isInnerSection() throws GameException {
        String[] parts = _line.split("\\|");
        switch (_section) {
        case Actions:
            if (parts.length == 2) {
                String[] keywords = parts[0].toLowerCase().split(",");
                String[] arguments = parts[1].split(",");
                Command command = Interpreter.findCommand(arguments);
                if (command == null) {
                    throw new GameException("Unknown command was referenced by the action in line " + _lineNumber);
                }
                for (int i = 0; i < keywords.length; i++) {
                    if (_scene == null) {
                        _source.getActions().put(keywords[i], command);
                    } else {
                        _scene.getActions().put(keywords[i], command);
                    }
                }
            } else {
                throw new GameException("Invalid arguments in action, see line " + _lineNumber);
            }
            break;
        case Group:
            if (_group == null) {
                throw new GameException("Invalid command group in line " + _lineNumber);
            }
            if (parts.length == 1) {
                String[] arguments = parts[0].split(",");
                Command command = Interpreter.findCommand(arguments);
                if (command == null) {
                    throw new GameException("Unknown command was referenced in line " + _lineNumber);
                }
                if (_depth > 0) {
                    if (_conditionalsElse[_depth]) {
                        _conditionals[_depth].getElseGroup().getCommands().add(command);
                    } else {
                        _conditionals[_depth].getGroup().getCommands().add(command);
                    }
                } else {
                    _group.getCommands().add(command);
                }
            } else {
                throw new GameException("Invalid command, see line " + _lineNumber);
            }
            break;
        case Items:
            if (parts.length == 2) {
                String keyword = parts[0];
                String description = parts[1];
                // Missing keys
                if (keyword.isBlank()) {
                    throw new GameException("Missing inventory item key in line " + _lineNumber);
                }
                // Key already in use 
                if (_scene.getItems().containsKey(keyword)) {
                    throw new GameException("Duplicate key was specified by the inventory item in line " + _lineNumber);
                }
                InventoryItem item = new InventoryItem(keyword, description, _scene);
                _scene.getItems().put(keyword, item);
            } else {
                throw new GameException("Invalid inventory item, see line " + _lineNumber);
            }
            break;
        case Messages:
            if (parts.length == 2) {
                String key = parts[0];
                String value = parts[1];
                // Missing keys
                if (key.isBlank()) {
                    throw new GameException("Missing message key, see line " + _lineNumber);
                }
                // Key already in use 
                if (_source.getMessages().containsKey(key)) {
                    throw new GameException("Duplicate key was specified by the message in line " + _lineNumber);
                }
                _source.getMessages().put(key, value);
            } else {
                throw new GameException("Invalid message, see line " + _lineNumber);
            }
            break;
        default:
            return false;
        }
        return true;
    }
    
}
