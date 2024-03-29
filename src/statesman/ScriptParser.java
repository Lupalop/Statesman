package statesman;

import java.util.HashMap;
import java.util.List;

import statesman.commands.Command;
import statesman.commands.ConditionalCommand;
import statesman.commands.Function;

public class ScriptParser {

    public static final int MAX_DEPTH = 1024;

    private enum Section {

        ACTION("action"),
        FUNCTION("function"),
        ITEM("item"),
        STRING("string"),
        ROOT("root"),
        SCENE("scene");

        private final String _tag;
        private static final HashMap<String, Section> BY_TAG = new HashMap<>();

        static {
            for (int i = 0; i < values().length; i++) {
                BY_TAG.put(values()[i].getTag(), values()[i]);
            }
        }

        private Section(String tag) {
            _tag = tag;
        }

        public String getTag() {
            return _tag;
        }

        public static Section valueOfTag(String tag) {
            return BY_TAG.get(tag);
        }

    }

    // Data fields
    private List<String> _data;
    private String _line;
    private int _lineNumber;
    private Script _script;

    // Parser fields
    private Section _section;
    private Scene _scene;
    private Function _function;
    private boolean _blockComment;

    private ConditionalCommand[] _conditionals;
    private boolean[] _conditionalsElse;
    private int _depth;

    private ScriptParser() {
        _data = null;
        _line = "";
        _lineNumber = 0;
        _script = null;

        _section = Section.ROOT;
        _depth = 0;
        _scene = null;
        _function = null;
        _blockComment = false;

        _conditionals = new ConditionalCommand[MAX_DEPTH];
        _conditionalsElse = new boolean[MAX_DEPTH];
    }

    public ScriptParser(List<String> data) {
        this();
        _data = data;
    }

    public Script read() throws GameException {
        _script = new Script();

        for (int i = 0; i < _data.size(); i++) {
            _lineNumber = i + 1;
            _line = _data.get(i).trim();

            if (parseComment()) {
                continue;
            }
            if (parseTerminator()) {
                continue;
            }
            if (parsePreference()) {
                continue;
            }
            if (parseSection()) {
                continue;
            }
            if (parseInnerSection()) {
                continue;
            }

            throw new GameException("Invalid string in line " + _lineNumber);
        }

        return _script;
    }

    private boolean parseComment() {
        // Block comment start tag
        if (_line.startsWith("/*")) {
            _blockComment = true;
            return true;
        }
        // Block comment end tag
        if (_line.endsWith("*/")) {
            _blockComment = false;
            return true;
        }
        // Ignore blank lines and comments
        if (_blockComment || _line.isBlank() || _line.startsWith("//")) {
            return true;
        }
        return false;
    }

    private boolean parseTerminator() throws GameException {
        if (!_line.startsWith("end")) {
            return false;
        }
        switch (_section) {
        case ROOT:
            throw new GameException("Stray end tag in line " + _lineNumber);
        case SCENE:
            _scene = null;
            break;
        case FUNCTION:
            if (_depth > 0) {
                _conditionalsElse[_depth] = false;
                _conditionals[_depth] = null;
                _depth--;
                return true;
            } else {
                _function = null;
            }
            break;
        default:
            break;
        }
        if (_scene != null) {
            _section = Section.SCENE;
        } else {
            _section = Section.ROOT;
        }
        return true;
    }

    private boolean parsePreference() throws GameException {
        String[] parts = _line.split(" ");
        if (_section != Section.ROOT || parts.length != 2) {
            return false;
        }

        switch (parts[0]) {
        case "maxpoints":
            int maxPoints = Integer.parseInt(parts[1]);
            if (maxPoints < 0) {
                throw new GameException(
                        "The maximum number of points must be greater than or equal to zero, see line "
                                + _lineNumber);
            }
            _script.setMaxPoints(maxPoints);
            break;
        case "switches":
            // XXX: This is a legacy property and is ignored.
            break;
        default:
            return false;
        }
        return true;
    }

    private boolean parseSection() throws GameException {
        String[] parts = _line.split(" ");
        if (_section == Section.ROOT || _section == Section.SCENE) {
            return parseRootOrSceneSection(parts);
        } else if (_section == Section.FUNCTION) {
            return parseFunctionSection(parts);
        }
        return false;
    }

    private boolean parseRootOrSceneSection(String[] parts)
            throws GameException {
        Section nextSection = Section.valueOfTag(parts[0]);
        if (nextSection == null) {
            return false;
        }
        switch (nextSection) {
        case ACTION:
        case STRING:
            break;
        case ITEM:
            if (_section != Section.SCENE) {
                throw new GameException(
                        "The items section MUST be inside a scene section, see line "
                                + _lineNumber);
            }
            break;
        case FUNCTION:
            if (parts.length == 2) {
                _function = new Function(parts[1]);
                if (_section == Section.ROOT) {
                    // Reserved function name (command ran on entry)
                    if (_function.getName().equals(Scene.FN_ENTRY)) {
                        throw new GameException(
                                "Use of reserved function name, see line "
                                        + _lineNumber);
                    }
                    _script.getFunctions().put(_function.getName(), _function);
                } else {
                    // Function name already in use locally
                    if (_scene.getFunctions()
                            .containsKey(_function.getName())) {
                        throw new GameException(
                                "Function name already used in current scene, see line "
                                        + _lineNumber);
                    }
                    _scene.getFunctions().put(_function.getName(), _function);
                }
            } else {
                throw new GameException(
                        "Invalid function section tag, see line "
                                + _lineNumber);
            }
            break;
        case SCENE:
            // Nested scenes
            if (_section == Section.SCENE || _scene != null) {
                throw new GameException(
                        "Nested scenes are not allowed, see line "
                                + _lineNumber);
            }
            if (parts.length == 2) {
                _scene = new Scene(parts[1]);
                // Scene name already in use
                if (_script.getScenes().containsKey(_scene.getName())) {
                    throw new GameException(
                            "Specified scene name is already in use, see line "
                                    + _lineNumber);
                }
                _script.getScenes().put(_scene.getName(), _scene);
            } else {
                throw new GameException(
                        "Invalid scene section tag, see line " + _lineNumber);
            }
            break;
        default:
            return false;
        }
        _section = nextSection;
        return true;
    }

    private boolean parseFunctionSection(String[] parts) throws GameException {
        if (parts.length == 1 && parts[0].equals("else")) {
            if (_conditionals[_depth] == null || _conditionalsElse[_depth]) {
                throw new GameException(
                        "Stray else tag, see line " + _lineNumber);
            }
            _conditionalsElse[_depth] = true;
            return true;
        } else if (parts.length >= 2) {
            String Id = null;
            if (parts[0].equals("if") || parts[0].equals("elsif")
                    || parts[0].equals("if_inv")
                    || parts[0].equals("elsif_inv")) {
                Id = ConditionalCommand.ID_CONDITIONAL;
            }
            if (Id != null) {
                boolean isElseIf = parts[0].startsWith("els");
                if (isElseIf) {
                    _conditionalsElse[_depth] = true;
                }
                ConditionalCommand command = (ConditionalCommand) ConditionalCommand
                        .getDefault().fromText(null, parts);
                // Set the name of the anonymous functions contained within
                // conditional commands to be the same with the function
                // that contains them.
                command.getTrueGroup().setName(_function.getName());
                command.getFalseGroup().setName(_function.getName());
                if (_depth == 0 && _section == Section.FUNCTION) {
                    _function.getCommands().add(command);
                } else if (_conditionalsElse[_depth]) {
                    _conditionals[_depth].getFalseGroup().getCommands()
                            .add(command);
                } else {
                    _conditionals[_depth].getTrueGroup().getCommands()
                            .add(command);
                }
                if (isElseIf) {
                    _conditionalsElse[_depth] = false;
                } else {
                    _depth++;
                }
                _conditionals[_depth] = command;
                return true;
            }
        }
        return false;
    }

    private boolean parseInnerSection() throws GameException {
        String[] parts = _line.split("\\|");
        switch (_section) {
        case ACTION:
            if (parts.length == 2) {
                String[] keywords = parts[0].toLowerCase().split(",");
                String[] arguments = parts[1].split(",");
                Command command = Command.find(arguments);
                if (command == null) {
                    throw new GameException(
                            "Unknown command was referenced in line "
                                    + _lineNumber);
                }
                for (String keyword : keywords) {
                    if (_scene == null) {
                        _script.getActions().put(keyword, command);
                    } else {
                        _scene.getActions().put(keyword, command);
                    }
                }
            } else {
                throw new GameException(
                        "Invalid action, see line " + _lineNumber);
            }
            break;
        case FUNCTION:
            if (_function == null) {
                throw new GameException(
                        "Invalid function, see line " + _lineNumber);
            }
            if (parts.length == 1) {
                String[] arguments = parts[0].split(",");
                Command command = Command.find(arguments);
                if (command == null) {
                    throw new GameException(
                            "Unknown command was referenced in line "
                                    + _lineNumber);
                }
                if (_depth > 0) {
                    if (_conditionalsElse[_depth]) {
                        _conditionals[_depth].getFalseGroup().getCommands()
                                .add(command);
                    } else {
                        _conditionals[_depth].getTrueGroup().getCommands()
                                .add(command);
                    }
                } else {
                    _function.getCommands().add(command);
                }
            } else {
                throw new GameException(
                        "Invalid command, see line " + _lineNumber);
            }
            break;
        case ITEM:
            if (parts.length == 2) {
                String keyword = parts[0];
                String description = parts[1];
                // Missing keys
                if (keyword.isBlank()) {
                    throw new GameException(
                            "Missing inventory item key in line "
                                    + _lineNumber);
                }
                // Key already in use
                if (_scene.getItems().containsKey(keyword)) {
                    throw new GameException(
                            "Duplicate key was specified by the inventory item in line "
                                    + _lineNumber);
                }
                InventoryItem item = new InventoryItem(keyword, description,
                        _scene);
                _scene.getItems().put(keyword, item);
            } else {
                throw new GameException(
                        "Invalid inventory item, see line " + _lineNumber);
            }
            break;
        case STRING:
            if (parts.length == 2) {
                String key = parts[0];
                String value = parts[1];
                // Missing keys
                if (key.isBlank()) {
                    throw new GameException(
                            "Missing message key, see line " + _lineNumber);
                }
                // Key already in use
                if (_script.getMessages().containsKey(key)) {
                    throw new GameException(
                            "Duplicate key was specified by the message in line "
                                    + _lineNumber);
                }
                _script.getMessages().put(key, value);
            } else {
                throw new GameException(
                        "Invalid message, see line " + _lineNumber);
            }
            break;
        default:
            return false;
        }
        return true;
    }

}
