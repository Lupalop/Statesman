namespace Statesman.Commands
{
    public class JumpCommand : Command
    {
        public const string CommandJump = "jmp";
        public const string CommandInventoryJump = "ijmp";
        public const string CommandSwitchJump = "sjmp";
        public const string CommandReturn = "ret";

        private readonly string _itemName;
        private readonly bool _isUnconditional;

        protected int _lineIfTrue;
        protected int _lineIfFalse;

        public JumpCommand(string itemName, int lineIfTrue, int lineIfFalse)
        {
            if (itemName == null)
            {
                _isUnconditional = true;
            }
            _itemName = itemName;
            _lineIfTrue = lineIfTrue;
            _lineIfFalse = lineIfFalse;
        }

        public new static Command FromText(string commandName, string[] arguments)
        {
            if (arguments.Length == 1 && commandName == CommandReturn)
            {
                return new JumpCommand(null, int.MaxValue, -1);
            }
            else if (arguments.Length == 2 && commandName == CommandJump)
            {
                if (!GetLineNumberFromString(arguments[1], out int line))
                {
                    return null;
                }
                return new JumpCommand(null, line, -1);
            }
            else if (arguments.Length == 4 && (commandName == CommandInventoryJump || commandName == CommandSwitchJump))
            {
                string itemName = arguments[1];
                if (!GetLineNumberFromString(arguments[2], out int lineIfTrue))
                {
                    return null;
                }
                if (!GetLineNumberFromString(arguments[3], out int lineIfFalse))
                {
                    return null;
                }
                return new JumpCommand(itemName, lineIfTrue, lineIfFalse);
            }
            return null;
        }

        protected static bool GetLineNumberFromString(string lineNumberText, out int lineNumber)
        {
            if (lineNumberText.Equals("ret", StringComparison.InvariantCultureIgnoreCase))
            {
                lineNumber = int.MaxValue;
                return true;
            }
            else if (int.TryParse(lineNumberText, out int index))
            {
                lineNumber = index;
                return true;
            }
            lineNumber = -1;
            return false;
        }

        public virtual int GetJumpIndex()
        {
            if (_isUnconditional)
            {
                return _lineIfTrue;
            }
            else if (Interpreter.Inventory.ContainsKey(_itemName))
            {
                return _lineIfTrue;
            }
            return _lineIfFalse;
        }
    }
}
