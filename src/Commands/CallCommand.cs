namespace Statesman.Commands
{
    public class CallCommand : Command
    {
        public enum CallType
        {
            None,
            Normal,
            Super,
            Global
        }

        public const string CommandGoto = "goto";
        public const string CommandCall = "call";
        public const string CommandCallGlobal = "callglob";
        public const string CommandSuper = "super";

        public CallType CallerType { get; private set; }
        private readonly string _functionName;

        public CallCommand(CallType callType, string functionName)
        {
            if (callType == CallType.None ||
                (callType != CallType.Super &&
                 string.IsNullOrWhiteSpace(functionName)))
            {
                throw new ArgumentException("Function name cannot be empty.");
            }
            _functionName = functionName;
            CallerType = callType;
        }

        public new static Command FromText(string commandName, string[] arguments)
        {
            CallType callType = CallType.None;
            string functionName = "";

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
                functionName = arguments[1];
            }

            if (callType == CallType.None)
            {
                return null;
            }

            return new CallCommand(callType, functionName);
        }

        public override void Execute()
        {
            if (CallerType == CallType.Super)
            {
                return;
            }

            Content.Script.Functions.TryGetValue(_functionName, out Function function);

            // Local functions override global functions if we're a normal call.
            if (Interpreter.Scene != null && CallerType == CallType.Normal)
            {
                if (Interpreter.Scene.Functions.TryGetValue(_functionName, out Function localFunction))
                {
                    localFunction?.Execute();
                    return;
                }
            }

            function?.Execute();
        }
    }
}
