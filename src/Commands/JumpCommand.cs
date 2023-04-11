namespace Statesman.Commands
{
    public class JumpCommand : Command
    {
        public const string kIdJump = "jmp";
        public const string kIdInventoryJump = "ijmp";
        public const string kIdSwitchJump = "sjmp";
        public const string kIdReturn = "ret";

        private readonly string _targetName;
        private readonly bool _isUnconditional;

        protected int _lineIfTrue;
        protected int _lineIfFalse;

        public JumpCommand(string targetName, int lineIfTrue, int lineIfFalse)
        {
            if (targetName == null)
            {
                _isUnconditional = true;
            }
            _targetName = targetName;
            _lineIfTrue = lineIfTrue;
            _lineIfFalse = lineIfFalse;
        }

        public new static Command FromText(string commandId, string[] arguments)
        {
            if (arguments.Length == 1 && commandId == kIdReturn)
            {
                return new JumpCommand(null, int.MaxValue, -1);
            }
            else if (arguments.Length == 2 && commandId == kIdJump)
            {
                if (!GetLineNumberFromString(arguments[1], out int line))
                {
                    return null;
                }
                return new JumpCommand(null, line, -1);
            }
            else if (arguments.Length == 4 && (commandId == kIdInventoryJump || commandId == kIdSwitchJump))
            {
                string targetName = arguments[1];
                if (!GetLineNumberFromString(arguments[2], out int lineIfTrue))
                {
                    return null;
                }
                if (!GetLineNumberFromString(arguments[3], out int lineIfFalse))
                {
                    return null;
                }
                return new JumpCommand(targetName, lineIfTrue, lineIfFalse);
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
            if (_isUnconditional || GetConditionValue(_targetName))
            {
                return _lineIfTrue;
            }
            return _lineIfFalse;
        }

        public static bool GetConditionValue(string targetName)
        {
            if (targetName.StartsWith("i:", StringComparison.InvariantCultureIgnoreCase))
            {
                targetName = targetName.Substring(2);
                return Interpreter.Inventory
                    .TryGetValue(targetName, out InventoryItem targetItem) && targetItem != null;
            }
            return Interpreter.Switches
                .TryGetValue(targetName, out bool targetSwitch) && targetSwitch;
        }
    }
}
