namespace Statesman.Commands
{
    public class SwitchSetCommand : Command
    {
        public const string CommandSwitchSet = "set";

        private readonly string _switchId;
        private readonly bool _value;

        public SwitchSetCommand(string switchId, bool value)
        {
            _switchId = switchId;
            _value = value;
        }

        public new static Command FromText(string commandName, string[] arguments)
        {
            if (commandName == CommandSwitchSet && arguments.Length == 3)
            {
                string switchId = arguments[1];
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
