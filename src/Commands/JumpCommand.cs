using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

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

        public override Command createInstance(string[] arguments)
        {
            if (arguments.Length == 2)
            {
                int line = int.Parse(arguments[1]);
                return new JumpCommand(line);
            }
            return null;
        }

        protected int getLineNumberFromString(string lineNumber)
        {
            int index = 0;
            if (lineNumber.Equals("ret", StringComparison.InvariantCultureIgnoreCase))
            {
                index = int.MaxValue;
            }
            else
            {
                index = int.Parse(lineNumber);
            }
            return index;
        }

        public virtual int getJumpIndex()
        {
            return _lineIfTrue;
        }
    }
}
