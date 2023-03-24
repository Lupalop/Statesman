namespace Statesman.Commands
{
    public class SwitchJumpCommand : JumpCommand
    {
        public static readonly string ID = "sjmp";

        private readonly int _switchId;

        public SwitchJumpCommand()
            : base()
        {
            _switchId = 0;
        }

        public SwitchJumpCommand(int switchId, int lineIfTrue, int lineIfFalse)
        {
            if (switchId < 0)
            {
                throw new ArgumentException("Switch ID must be a positive number");
            }
            _switchId = switchId;
            _lineIfTrue = lineIfTrue;
            _lineIfFalse = lineIfFalse;
        }

        public override Command CreateInstance(string[] arguments)
        {
            if (arguments.Length == 4)
            {
                int switchId = int.Parse(arguments[1]);
                int lineIfTrue = GetLineNumberFromString(arguments[2]);
                int lineIfFalse = GetLineNumberFromString(arguments[3]);
                return new SwitchJumpCommand(switchId, lineIfTrue, lineIfFalse);
            }
            return null;
        }

        public override int GetJumpIndex()
        {
            if (Interpreter.Switches[_switchId])
            {
                return _lineIfTrue;
            }
            return _lineIfFalse;
        }
    }
}
