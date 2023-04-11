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

        public const string kIdGoto = "goto";
        public const string kIdCall = "call";
        public const string kIdCallGlobal = "callglob";
        public const string kIdSuper = "super";

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

        public new static Command FromText(string commandId, string[] arguments)
        {
            CallType callType = CallType.None;
            string functionName = "";

            if (arguments.Length == 1 &&
                commandId.Equals(kIdSuper, StringComparison.InvariantCultureIgnoreCase))
            {
                callType = CallType.Super;
            }
            else if (arguments.Length == 2)
            {
                if (commandId.Equals(kIdGoto, StringComparison.InvariantCultureIgnoreCase) ||
                    commandId.Equals(kIdCall, StringComparison.InvariantCultureIgnoreCase))
                {
                    callType = CallType.Normal;
                }
                else if (commandId.Equals(kIdCallGlobal, StringComparison.InvariantCultureIgnoreCase))
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
