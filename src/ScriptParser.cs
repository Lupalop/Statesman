using Statesman.Commands;

namespace Statesman
{
    public class ScriptParser
    {
        public static readonly int MaxDepth = 1024;

        private enum Section
        {
            Action,
            Function,
            Item,
            String,
            Root,
            Scene
        };

        // Data fields
        private readonly Script _script;
        private readonly string _scriptName;
        private readonly StreamReader _reader;
        private string _line;
        private int _lineNumber;

        // Parser fields
        private Section _section;
        private Scene _scene;
        private Function _function;
        private bool _blockComment;

        private readonly ConditionalCommand[] _conditionals;
        private readonly bool[] _conditionalsElse;
        private int _depth;

        public ScriptParser(StreamReader reader, string scriptName, Script script)
        {
            _script = script;
            _scriptName = scriptName;
            _reader = reader;
            _line = "";
            _lineNumber = 0;

            _section = Section.Root;
            _depth = 0;
            _scene = null;
            _function = null;
            _blockComment = false;

            _conditionals = new ConditionalCommand[MaxDepth];
            _conditionalsElse = new bool[MaxDepth];
        }

        public void Read()
        {
            _lineNumber = 0;
            while (!_reader.EndOfStream)
            {
                _lineNumber++;
                _line = _reader.ReadLine().Trim();

                if (ParseComment())
                {
                    continue;
                }
                if (ParseTerminator())
                {
                    continue;
                }
                if (ParsePreference())
                {
                    continue;
                }
                if (ParseSection())
                {
                    continue;
                }
                if (ParseInnerSection())
                {
                    continue;
                }

                ThrowParserException("Invalid string");
            }
        }

        private bool ParseComment()
        {
            // Block comment start tag
            if (_line.StartsWith("/*"))
            {
                _blockComment = true;
                return true;
            }
            // Block comment end tag
            if (_line.EndsWith("*/"))
            {
                _blockComment = false;
                return true;
            }
            // Ignore blank lines and comments
            if (_blockComment || string.IsNullOrWhiteSpace(_line) || _line.StartsWith("//"))
            {
                return true;
            }
            return false;
        }

        private bool ParseTerminator()
        {
            if (!_line.StartsWith("end"))
            {
                return false;
            }

            switch (_section)
            {
                case Section.Root:
                    ThrowParserException("Stray end tag");
                    break;
                case Section.Scene:
                    _scene = null;
                    break;
                case Section.Function:
                    if (_depth > 0)
                    {
                        _conditionalsElse[_depth] = false;
                        _conditionals[_depth] = null;
                        _depth--;
                        return true;
                    }
                    else
                    {
                        _function = null;
                    }
                    break;
                default:
                    break;
            }
            if (_scene != null)
            {
                _section = Section.Scene;
            }
            else
            {
                _section = Section.Root;
            }

            return true;
        }

        private bool ParsePreference()
        {
            string[] parts = _line.Split(" ");
            if (_section != Section.Root || parts.Length != 2)
            {
                return false;
            }

            switch (parts[0])
            {
                case "maxpoints":
                    int maxPoints = int.Parse(parts[1]);
                    if (maxPoints < 0)
                    {
                        ThrowParserException(
                            "The maximum number of points must be greater than or equal to zero");
                    }
                    _script.MaxPoints = maxPoints;
                    break;
                case "switches":
                    // XXX: This is a legacy property and is ignored.
                    break;
                default:
                    return false;
            }

            return true;
        }

        private bool ParseSection()
        {
            string[] parts = _line.Split(" ");
            if (_section == Section.Root || _section == Section.Scene)
            {
                return ParseRootOrSceneSection(parts);
            }
            else if (_section == Section.Function)
            {
                return ParseFunctionSection(parts);
            }
            return false;
        }

        private bool ParseRootOrSceneSection(string[] parts)
        {
            if (!Enum.TryParse(typeof(Section), parts[0], true, out object parsedSection))
            {
                return false;
            }
            Section nextSection = (Section)parsedSection;
            switch (nextSection)
            {
                case Section.Action:
                case Section.String:
                    break;
                case Section.Item:
                    if (_section != Section.Scene)
                    {
                        ThrowParserException("The items section MUST be inside a scene section");
                    }
                    break;
                case Section.Function:
                    if (parts.Length == 2)
                    {
                        _function = new Function(parts[1]);
                        if (_section == Section.Root)
                        {
                            // Reserved function name (command ran on entry)
                            if (_function.Name.Equals(Scene.FunctionEntry, StringComparison.InvariantCultureIgnoreCase))
                            {
                                ThrowParserException("Use of reserved function name");
                            }
                            _script.Functions[_function.Name] = _function;
                        }
                        else
                        {
                            // Function name already in use locally
                            if (_scene.Functions.ContainsKey(_function.Name))
                            {
                                ThrowParserException("Function name is already in use in the current scene");
                            }
                            _scene.Functions[_function.Name] = _function;
                        }
                    }
                    else
                    {
                        ThrowParserException("Invalid function section tag");
                    }
                    break;
                case Section.Scene:
                    // Nested scenes
                    if (_section == Section.Scene || _scene != null)
                    {
                        ThrowParserException("Nested scenes are not allowed");
                    }
                    if (parts.Length == 2)
                    {
                        _scene = new Scene(parts[1]);
                        // Scene name already in use
                        if (_script.Scenes.ContainsKey(_scene.Name))
                        {
                            ThrowParserException("Scene name is already in use");
                        }
                        _script.Scenes.Add(_scene.Name, _scene);
                    }
                    else
                    {
                        ThrowParserException("Invalid scene section tag");
                    }
                    break;
                default:
                    return false;
            }
            _section = nextSection;

            return true;
        }

        private bool ParseFunctionSection(string[] parts)
        {
            if (parts.Length == 1 && parts[0].Equals("else", StringComparison.InvariantCultureIgnoreCase))
            {
                if (_conditionals[_depth] == null || _conditionalsElse[_depth])
                {
                    ThrowParserException("Stray else tag");
                }
                _conditionalsElse[_depth] = true;
                return true;
            }
            else if (parts.Length >= 2)
            {
                if (parts[0].Equals("if", StringComparison.InvariantCultureIgnoreCase) ||
                    parts[0].Equals("elsif", StringComparison.InvariantCultureIgnoreCase) ||
                    parts[0].Equals("if_inv", StringComparison.InvariantCultureIgnoreCase) ||
                    parts[0].Equals("elsif_inv", StringComparison.InvariantCultureIgnoreCase))
                {
                    bool isElseIf = parts[0].StartsWith("els");
                    if (isElseIf)
                    {
                        _conditionalsElse[_depth] = true;
                    }
                    ConditionalCommand command =
                            ConditionalCommand.FromText(
                                ConditionalCommand.kIdConditional, parts) as ConditionalCommand;
                    // Set the name of the anonymous functions contained within
                    // conditional functions to be the same with the function
                    // that contains them.
                    command.Group.Name = _function.Name;
                    command.ElseGroup.Name = _function.Name;
                    if (_depth == 0 && _section == Section.Function)
                    {
                        _function.Commands.Add(command);
                    }
                    else if (_conditionalsElse[_depth])
                    {
                        _conditionals[_depth].ElseGroup.Commands.Add(command);
                    }
                    else
                    {
                        _conditionals[_depth].Group.Commands.Add(command);
                    }
                    if (isElseIf)
                    {
                        _conditionalsElse[_depth] = false;
                    }
                    else
                    {
                        _depth++;
                    }
                    _conditionals[_depth] = command;
                    return true;
                }
            }

            return false;
        }

        private bool ParseInnerSection()
        {
            string[] parts = _line.Split("|");
            switch (_section)
            {
                case Section.Action:
                    if (parts.Length == 2)
                    {
                        string[] keywords = parts[0].ToLowerInvariant().Split(",");
                        string[] arguments = parts[1].Split(",");
                        Command command = Command.Find(arguments);
                        if (command == null)
                        {
                            ThrowParserException("Unknown command");
                        }
                        for (int i = 0; i < keywords.Length; i++)
                        {
                            if (_scene == null)
                            {
                                _script.Actions[keywords[i]] = command;
                            }
                            else
                            {
                                _scene.Actions[keywords[i]] = command;
                            }
                        }
                    }
                    else
                    {
                        ThrowParserException("Invalid action");
                    }
                    break;
                case Section.Function:
                    if (_function == null)
                    {
                        ThrowParserException("Invalid function");
                    }
                    if (parts.Length == 1)
                    {
                        string[] arguments = parts[0].Split(",");
                        Command command = Command.Find(arguments);
                        if (command == null)
                        {
                            ThrowParserException("Undefined command");
                        }
                        if (_depth > 0)
                        {
                            if (_conditionalsElse[_depth])
                            {
                                _conditionals[_depth].ElseGroup.Commands.Add(command);
                            }
                            else
                            {
                                _conditionals[_depth].Group.Commands.Add(command);
                            }
                        }
                        else
                        {
                            _function.Commands.Add(command);
                        }
                    }
                    else
                    {
                        ThrowParserException("Invalid command");
                    }
                    break;
                case Section.Item:
                    if (parts.Length == 2)
                    {
                        string keyword = parts[0];
                        string description = parts[1];
                        // Missing keys
                        if (string.IsNullOrWhiteSpace(keyword))
                        {
                            ThrowParserException("Missing inventory item key");
                        }
                        // Key already in use 
                        if (_scene.Items.ContainsKey(keyword))
                        {
                            ThrowParserException("Duplicate key specified by the inventory item");
                        }
                        InventoryItem item = new(keyword, description, _scene);
                        _scene.Items.Add(keyword, item);
                    }
                    else
                    {
                        ThrowParserException("Invalid inventory item");
                    }
                    break;
                case Section.String:
                    if (parts.Length == 2)
                    {
                        string key = parts[0];
                        string value = parts[1];
                        // Missing keys
                        if (string.IsNullOrWhiteSpace(key))
                        {
                            ThrowParserException("Missing message key");
                        }
                        // Key already in use 
                        if (_script.Messages.ContainsKey(key))
                        {
                            ThrowParserException("Duplicate key was specified by the message");
                        }
                        _script.Messages.Add(key, value);
                    }
                    else
                    {
                        ThrowParserException("Invalid message");
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }

        private void ThrowParserException(string innerMessage)
        {
            string message = "{0}.\nLine: {1}\nFile: {2}";
            throw new GameException(
                string.Format(message, innerMessage, _lineNumber, _scriptName));
        }
    }
}
