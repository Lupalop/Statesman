using Statesman.Commands;

namespace Statesman
{
    public class ScriptParser
    {
        public static readonly int MaxDepth = 1024;

        private enum Section
        {
            Action,
            Group,
            Item,
            String,
            Root,
            Scene
        };

        // Data fields
        private readonly List<string> _data;
        private string _line;
        private int _lineNumber;
        private Script _script;

        // Parser fields
        private Section _section;
        private Scene _scene;
        private CommandGroup _group;
        private bool _blockComment;

        private readonly ConditionalCommand[] _conditionals;
        private readonly bool[] _conditionalsElse;
        private int _depth;

        public ScriptParser(List<string> data)
        {
            _data = data;
            _line = "";
            _lineNumber = 0;
            _script = null;

            _section = Section.Root;
            _depth = 0;
            _scene = null;
            _group = null;
            _blockComment = false;

            _conditionals = new ConditionalCommand[MaxDepth];
            _conditionalsElse = new bool[MaxDepth];
        }

        public Script Read()
        {
            _script = new Script();

            for (int i = 0; i < _data.Count; i++)
            {
                _lineNumber = i + 1;
                _line = _data[i].Trim();

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

                throw new GameException("Invalid string in line " + _lineNumber);
            }

            return _script;
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
                    throw new GameException("Stray end tag in line " + _lineNumber);
                case Section.Scene:
                    _scene = null;
                    break;
                case Section.Group:
                    if (_depth > 0)
                    {
                        _conditionalsElse[_depth] = false;
                        _conditionals[_depth] = null;
                        _depth--;
                        return true;
                    }
                    else
                    {
                        _group = null;
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
                        throw new GameException("The maximum number of points must be greater than or equal to zero, see line " + _lineNumber);
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
            else if (_section == Section.Group)
            {
                return ParseGroupSection(parts);
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
                        throw new GameException("The items section MUST be inside a scene section, see line " + _lineNumber);
                    }
                    break;
                case Section.Group:
                    if (parts.Length == 2)
                    {
                        _group = new CommandGroup(parts[1]);
                        if (_section == Section.Root)
                        {
                            // Reserved group name (command ran on entry)
                            if (_group.Name.Equals(Scene.CommandGroupEntry, StringComparison.InvariantCultureIgnoreCase))
                            {
                                throw new GameException("Use of reserved command group name, see line " + _lineNumber);
                            }
                            _script.CommandGroups[_group.Name] = _group;
                        }
                        else
                        {
                            // Group name already in use locally
                            if (_scene.CommandGroups.ContainsKey(_group.Name))
                            {
                                throw new GameException("Command group name already used in current scene, see line " + _lineNumber);
                            }
                            _scene.CommandGroups[_group.Name] = _group;
                        }
                    }
                    else
                    {
                        throw new GameException("Invalid group section tag, see line " + _lineNumber);
                    }
                    break;
                case Section.Scene:
                    // Nested scenes
                    if (_section == Section.Scene || _scene != null)
                    {
                        throw new GameException("Nested scenes are not allowed, see line " + _lineNumber);
                    }
                    if (parts.Length == 2)
                    {
                        _scene = new Scene(parts[1]);
                        // Scene name already in use
                        if (_script.Scenes.ContainsKey(_scene.Name))
                        {
                            throw new GameException("Specified scene name is already in use, see line " + _lineNumber);
                        }
                        _script.Scenes.Add(_scene.Name, _scene);
                    }
                    else
                    {
                        throw new GameException("Invalid scene section tag, see line " + _lineNumber);
                    }
                    break;
                default:
                    return false;
            }
            _section = nextSection;

            return true;
        }

        private bool ParseGroupSection(string[] parts)
        {
            if (parts.Length == 1 && parts[0].Equals("else", StringComparison.InvariantCultureIgnoreCase))
            {
                if (_conditionals[_depth] == null || _conditionalsElse[_depth])
                {
                    throw new GameException("Stray else tag, see line " + _lineNumber);
                }
                _conditionalsElse[_depth] = true;
                return true;
            }
            else if (parts.Length == 2)
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
                            ConditionalCommand.CreateInstance(
                                ConditionalCommand.CommandConditional, parts) as ConditionalCommand;
                    // Set the name of the command groups contained within
                    // conditional commands to be the same with the group
                    // that contains them
                    command.Group.Name = _group.Name;
                    command.ElseGroup.Name = _group.Name;
                    if (_depth == 0 && _section == Section.Group)
                    {
                        _group.Commands.Add(command);
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
                            throw new GameException("Unknown command was referenced in line " + _lineNumber);
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
                        throw new GameException("Invalid action, see line " + _lineNumber);
                    }
                    break;
                case Section.Group:
                    if (_group == null)
                    {
                        throw new GameException("Invalid command group, see line " + _lineNumber);
                    }
                    if (parts.Length == 1)
                    {
                        string[] arguments = parts[0].Split(",");
                        Command command = Command.Find(arguments);
                        if (command == null)
                        {
                            throw new GameException("Unknown command was referenced in line " + _lineNumber);
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
                            _group.Commands.Add(command);
                        }
                    }
                    else
                    {
                        throw new GameException("Invalid command, see line " + _lineNumber);
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
                            throw new GameException("Missing inventory item key in line " + _lineNumber);
                        }
                        // Key already in use 
                        if (_scene.Items.ContainsKey(keyword))
                        {
                            throw new GameException("Duplicate key was specified by the inventory item in line " + _lineNumber);
                        }
                        InventoryItem item = new(keyword, description, _scene);
                        _scene.Items.Add(keyword, item);
                    }
                    else
                    {
                        throw new GameException("Invalid inventory item, see line " + _lineNumber);
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
                            throw new GameException("Missing message key, see line " + _lineNumber);
                        }
                        // Key already in use 
                        if (_script.Messages.ContainsKey(key))
                        {
                            throw new GameException("Duplicate key was specified by the message in line " + _lineNumber);
                        }
                        _script.Messages.Add(key, value);
                    }
                    else
                    {
                        throw new GameException("Invalid message, see line " + _lineNumber);
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }
    }
}
