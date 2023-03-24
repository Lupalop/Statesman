using System;

namespace Statesman.Commands
{
    public class SwitchSetCommand : Command
    {
        public static readonly string ID = "set";

        private readonly int _switchId;
        private readonly bool _value;

        public SwitchSetCommand()
        {
            _switchId = 0;
            _value = false;
        }

        public SwitchSetCommand(int switchId, bool value)
        {
            if (switchId < 0)
            {
                throw new ArgumentException("Switch ID must be a positive number");
            }
            _switchId = switchId;
            _value = value;
        }

        public override Command CreateInstance(string[] arguments)
        {
            if (arguments.Length == 3)
            {
                int switchId = int.Parse(arguments[1]);
                bool value = bool.Parse(arguments[2]);
                return new SwitchSetCommand(switchId, value);
            }
            return null;
        }

        public override void Execute()
        {
            Interpreter.Switches[_switchId] = _value;
        }
    }
}
