namespace Statesman.Commands
{
    public class CallCommand : Command
    {
        public const string CommandGoto = "goto";
        public const string CommandCall = "call";
        public const string CommandCallGlobal = "callglob";

        private readonly string _groupName;
        private readonly bool _ignoreLocal;

        public CallCommand(string commandGroupName, bool ignoreLocal)
        {
            if (string.IsNullOrWhiteSpace(commandGroupName))
            {
                throw new ArgumentException("Command group name cannot be empty.");
            }
            _groupName = commandGroupName;
            _ignoreLocal = ignoreLocal;
        }

        public new static Command FromText(string commandName, string[] arguments)
        {
            if (commandName != CommandGoto &&
                commandName != CommandCall &&
                commandName != CommandCallGlobal)
            {
                return null;
            }
            if (arguments.Length == 2)
            {
                bool ignoreLocal = commandName == CommandCallGlobal;
                return new CallCommand(arguments[1], ignoreLocal);
            }
            return null;
        }

        public override void Execute()
        {
            Content.Script.CommandGroups.TryGetValue(_groupName, out CommandGroup group);

            // Local scene groups override the global group
            if (Interpreter.Scene != null && !_ignoreLocal)
            {
                if (Interpreter.Scene.CommandGroups.TryGetValue(_groupName, out CommandGroup localGroup))
                {
                    if (localGroup != null)
                    {
                        group = localGroup;
                    }
                }
            }

            group?.Execute();
        }
    }
}
