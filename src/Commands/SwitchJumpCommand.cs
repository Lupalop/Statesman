using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Statesman.Commands
{
    public class SwitchJumpCommand : JumpCommand
    {
        public static readonly string ID = "sjmp";

        private int _switchId;

        public SwitchJumpCommand()
            : base()
        {
            _switchId = 0;
        }

        public SwitchJumpCommand(int switchId, int lineIfTrue, int lineIfFalse)
        {
            if (switchId < 0)
            {
                throw new ArgumentException();
            }
            _switchId = switchId;
            _lineIfTrue = lineIfTrue;
            _lineIfFalse = lineIfFalse;
        }

        public override Command createInstance(string[] arguments)
        {
            if (arguments.Length == 4)
            {
                int switchId = int.Parse(arguments[1]);
                int lineIfTrue = getLineNumberFromString(arguments[2]);
                int lineIfFalse = getLineNumberFromString(arguments[3]);
                return new SwitchJumpCommand(switchId, lineIfTrue, lineIfFalse);
            }
            return null;
        }

        public override int getJumpIndex()
        {
            if (Interpreter.getSwitches()[_switchId])
            {
                return _lineIfTrue;
            }
            return _lineIfFalse;
        }
    }
}
