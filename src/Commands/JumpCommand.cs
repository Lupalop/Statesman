using System;

namespace Statesman.Commands
{
    public class JumpCommand : Command
    {
        public static readonly string ID = "jmp";

        protected int _lineIfTrue;
        protected int _lineIfFalse;

        public JumpCommand()
        {
            _lineIfTrue = 0;
            _lineIfFalse = 0;
        }

        public JumpCommand(int line)
            : this()
        {
            _lineIfTrue = line;
        }

        public override Command CreateInstance(string[] arguments)
        {
            if (arguments.Length == 2)
            {
                int line = int.Parse(arguments[1]);
                return new JumpCommand(line);
            }
            return null;
        }

        protected static int GetLineNumberFromString(string lineNumber)
        {
            if (lineNumber.Equals("ret", StringComparison.InvariantCultureIgnoreCase))
            {
                return int.MaxValue;
            }
            else if (int.TryParse(lineNumber, out int index))
            {
                return index;
            }
            return 0;
        }

        public virtual int GetJumpIndex()
        {
            return _lineIfTrue;
        }
    }
}
