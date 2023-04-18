using Statesman.Commands;
using System.Text;

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
        private readonly Stream _stream;
        private int _lineNumber;

        // Parser fields
        private Section _section;
        private Scene _scene;
        private Function _function;
        private bool _blockComment;

        private string _token;
        private int _tokenLength;
        private string _prevToken;
        private int _prevTokenLength;
        private bool _tokenConsumed;

        private readonly ConditionalCommand[] _conditionals;
        private readonly bool[] _conditionalsElse;
        private int _depth;

        public ScriptParser(Stream stream, string scriptName, Script script)
        {
            _script = script;
            _scriptName = scriptName;
            _stream = stream;
            _lineNumber = 0;

            _section = Section.Root;
            _depth = 0;
            _scene = null;
            _function = null;
            _blockComment = false;
            _tokenConsumed = true;

            _token = "";
            _tokenLength = 0;
            _prevToken = "";
            _prevTokenLength = 0;

            _conditionals = new ConditionalCommand[MaxDepth];
            _conditionalsElse = new bool[MaxDepth];
        }

        public void Read()
        {
            _lineNumber = 1;
            _stream.Position = 0;
            while (_stream.Position < _stream.Length)
            {
                if (!ParseComment())
                {
                    continue;
                }
                if (!ParseTerminator())
                {
                    continue;
                }
                if (!ParsePreference())
                {
                    continue;
                }
                if (!ParseRootOrSceneSection())
                {
                    continue;
                }
                if (!ParseConditionalInFunctionSection())
                {
                    continue;
                }
                if (!ParseInnerSection())
                {
                    continue;
                }

                ThrowParserException("Invalid string");
            }
        }

        private int IsNewline(int currentChar)
        {
            if (currentChar == '\r' || currentChar == '\n')
            {
                int possibleLF = _stream.ReadByte();
                if (possibleLF == '\n')
                {
                    return 2;
                }
                return 1;
            }
            return 0;
        }

        private void MarkNextLine()
        {
            _lineNumber++;

            _token = "";
            _tokenLength = 0;
            _prevToken = null;
            _prevTokenLength = 0;
        }

        private string NextLine()
        {
            List<byte> buffer = new();
            while (true)
            {
                int currentChar = _stream.ReadByte();
                if (IsNewline(currentChar) != 0)
                {
                    MarkNextLine();
                    break;
                }
                else if (currentChar == -1)
                {
                    break;
                }
                buffer.Add((byte)currentChar);
            }

            return Encoding.UTF8.GetString(buffer.ToArray());
        }

        private bool NextToken(bool pushback = true)
        {
            if (_stream.Position == (_stream.Length - 1))
            {
                return false;
            }

            if (!_tokenConsumed)
            {
                _tokenConsumed = true;
                return true;
            }
            long startIndex = _stream.Position;
            List<byte> buffer = new();
            int whitespaceLength = 0;
            int tokenLength = 0;
            while (true)
            {
                int currentChar = _stream.ReadByte();
                if (currentChar == ' ')
                {
                    if (tokenLength == 0)
                    {
                        startIndex++;
                        whitespaceLength++;
                        continue;
                    }
                    _stream.Position--;
                    break;
                }
                else if (currentChar == -1)
                {
                    break;
                }
                else
                {
                    int newlineCharCount = IsNewline(currentChar);
                    if (newlineCharCount != 0)
                    {
                        if (tokenLength > 0)
                        {
                            _stream.Position -= newlineCharCount;
                            break;
                        }

                        if (pushback)
                        {
                            _stream.Position -= newlineCharCount;
                        }
                        else
                        {
                            MarkNextLine();
                        }
                        return false;
                    }
                }
                buffer.Add((byte)currentChar);
                tokenLength++;
            }

            _prevToken = _token;
            _prevTokenLength = _tokenLength;

            _token = Encoding.UTF8.GetString(buffer.ToArray());
            _tokenLength = tokenLength + whitespaceLength;
            _tokenConsumed = true;

            return true;
        }

        private void UngetToken(bool consumed = true)
        {
            if (string.IsNullOrWhiteSpace(_token) ||
                _tokenLength == 0)
            {
                return;
            }

            _stream.Position -= _tokenLength;

            _token = _prevToken;
            _tokenLength = _prevTokenLength;
            _tokenConsumed = consumed;

            _prevToken = null;
            _prevTokenLength = 0;
        }

        private bool ParseComment()
        {
            if (!NextToken(false))
            {
                return false;
            }

            // Block comment start tag.
            if (_token.StartsWith("/*"))
            {
                _blockComment = true;
                return false;
            }
            // Block comment end tag.
            else if (_token.EndsWith("*/"))
            {
                _blockComment = false;
                return true;
            }
            // Skip until we find the block comment end tag.
            else if (_blockComment)
            {
                return false;
            }
            // Ignore single-line comments.
            else if (_token.StartsWith("//"))
            {
                UngetToken();
                NextLine();
                return false;
            }

            UngetToken();
            return true;
        }

        private bool ParseTerminator()
        {
            if (!NextToken(false))
            {
                return false;
            }

            if (!_token.StartsWith("end"))
            {
                UngetToken();
                return true;
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

            return false;
        }

        private bool ParsePreference()
        {
            if (_section != Section.Root)
            {
                return true;
            }
            if (!NextToken(false))
            {
                return false;
            }
            string preferenceName = _token;
            if (!NextToken())
            {
                UngetToken();
                return true;
            }
            string preferenceValue = _token;

            switch (preferenceName)
            {
                case "maxpoints":
                    int maxPoints = int.Parse(preferenceValue);
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
                    // Likely not a preference.
                    UngetToken(false);
                    return true;
            }

            return true;
        }

        private bool ParseRootOrSceneSection()
        {
            if (_section != Section.Root && _section != Section.Scene)
            {
                return true;
            }

            if (!NextToken(false))
            {
                return false;
            }

            if (!Enum.TryParse(typeof(Section), _token, true, out object parsedSection))
            {
                UngetToken();
                return true;
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
                    if (!NextToken())
                    {
                        ThrowParserException("Invalid function section tag");
                    }

                    _function = new Function(_token);
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
                    break;
                case Section.Scene:
                    // Nested scenes
                    if (_section == Section.Scene || _scene != null)
                    {
                        ThrowParserException("Nested scenes are not allowed");
                    }
                    else if (!NextToken())
                    {
                        ThrowParserException("Invalid scene section tag");
                    }

                    _scene = new Scene(_token);
                    // Scene name already in use
                    if (_script.Scenes.ContainsKey(_scene.Name))
                    {
                        ThrowParserException("Scene name is already in use");
                    }
                    _script.Scenes.Add(_scene.Name, _scene);
                    break;
                default:
                    // Unknown section.
                    ThrowParserException("This should not be reached.");
                    return true;
            }
            _section = nextSection;

            return false;
        }

        private bool ParseConditionalInFunctionSection()
        {
            if (_section != Section.Function)
            {
                return true;
            }

            if (!NextToken(false))
            {
                return false;
            }

            bool isElse =
                _token.Equals("else", StringComparison.InvariantCultureIgnoreCase);
            bool isIf =
                _token.Equals("if", StringComparison.InvariantCultureIgnoreCase) ||
                _token.Equals("elsif", StringComparison.InvariantCultureIgnoreCase) ||
                _token.Equals("if_inv", StringComparison.InvariantCultureIgnoreCase) ||
                _token.Equals("elsif_inv", StringComparison.InvariantCultureIgnoreCase);

            if (isElse)
            {
                if (_conditionals[_depth] == null || _conditionalsElse[_depth])
                {
                    ThrowParserException("Stray else tag");
                }
                _conditionalsElse[_depth] = true;
                return false;
            }
            else if (isIf)
            {
                bool isElseIf = _token.StartsWith("els");
                if (isElseIf)
                {
                    _conditionalsElse[_depth] = true;
                }

                List<string> conditions = new() {
                    ConditionalCommand.kIdConditional
                };
                while (true)
                {
                    if (!ParseComment() || !NextToken(false))
                    {
                        break;
                    }
                    conditions.Add(_token);
                }

                ConditionalCommand command =
                        ConditionalCommand.FromText(
                            ConditionalCommand.kIdConditional,
                            conditions.ToArray()) as ConditionalCommand;
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
                return false;
            }

            UngetToken();
            return true;
        }

        private bool ParseInnerSection()
        {
            string line = NextLine().Trim();
            string[] parts = line.Split("|");
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
                    return true;
            }
            return false;
        }

        private void ThrowParserException(string innerMessage)
        {
            string message = "{0}.\nLine: {1}\nFile: {2}";
            throw new GameException(
                string.Format(message, innerMessage, _lineNumber, _scriptName));
        }
    }
}
