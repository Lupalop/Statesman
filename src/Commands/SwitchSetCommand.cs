using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Statesman.Commands
{
    public class SwitchSetCommand : Command
    {
        public static readonly string ID = "set";

        private int _switchId;
        private bool _value;

        public SwitchSetCommand()
        {
            _switchId = 0;
            _value = false;
        }

        public SwitchSetCommand(int switchId, bool value)
        {
            if (switchId < 0)
            {
                throw new ArgumentException();
            }
            _switchId = switchId;
            _value = value;
        }

        public override Command createInstance(string[] arguments)
        {
            if (arguments.Length == 3)
            {
                int switchId = int.Parse(arguments[1]);
                bool value = bool.Parse(arguments[2]);
                return new SwitchSetCommand(switchId, value);
            }
            return null;
        }

        public override void execute()
        {
            Interpreter.getSwitches()[_switchId] = _value;
        }
    }
}
