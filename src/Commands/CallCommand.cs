namespace Statesman.Commands
{
    public class CallCommand : Command
    {
        public enum CallType { None, Normal, Super, Global }

        public const string CommandGoto = "goto";
        public const string CommandCall = "call";
        public const string CommandCallGlobal = "callglob";
        public const string CommandSuper = "super";

        public CallType CallerType { get; private set; }
        private readonly string _groupName;

        public CallCommand(CallType callType, string commandGroupName)
        {
            if (callType == CallType.None ||
                (callType != CallType.Super &&
                 string.IsNullOrWhiteSpace(commandGroupName)))
            {
                throw new ArgumentException("Command group name cannot be empty.");
            }
            _groupName = commandGroupName;
            CallerType = callType;
        }

        public new static Command FromText(string commandName, string[] arguments)
        {
            CallType callType = CallType.None;
            string groupName = "";

            if (arguments.Length == 1 &&
                commandName.Equals(CommandSuper, StringComparison.InvariantCultureIgnoreCase))
            {
                callType = CallType.Super;
            }
            else if (arguments.Length == 2)
            {
                if (commandName.Equals(CommandGoto, StringComparison.InvariantCultureIgnoreCase) ||
                    commandName.Equals(CommandCall, StringComparison.InvariantCultureIgnoreCase))
                {
                    callType = CallType.Normal;
                }
                else if (commandName.Equals(CommandCallGlobal, StringComparison.InvariantCultureIgnoreCase))
                {
                    callType = CallType.Global;
                }
                groupName = arguments[1];
            }

            if (callType == CallType.None)
            {
                return null;
            }

            return new CallCommand(callType, groupName);
        }

        public override void Execute()
        {
            if (CallerType == CallType.Super)
            {
                return;
            }

            Content.Script.CommandGroups.TryGetValue(_groupName, out CommandGroup group);

            // Local scene groups override the global group
            if (Interpreter.Scene != null && CallerType == CallType.Normal)
            {
                if (Interpreter.Scene.CommandGroups.TryGetValue(_groupName, out CommandGroup localGroup))
                {
                    localGroup?.Execute();
                    return;
                }
            }

            group?.Execute();
        }
    }
}
