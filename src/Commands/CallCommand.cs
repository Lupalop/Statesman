namespace Statesman.Commands
{
    public class CallCommand : Command
    {
        public const string CommandGoto = "goto";
        public const string CommandCall = "call";

        private readonly string _groupName;

        public CallCommand(string commandGroupName)
        {
            if (string.IsNullOrWhiteSpace(commandGroupName))
            {
                throw new ArgumentException("Goto command group name cannot be empty");
            }
            _groupName = commandGroupName;
        }

        public new static Command CreateInstance(string commandName, string[] arguments)
        {
            if (commandName != CommandGoto && commandName != CommandCall)
            {
                return null;
            }
            if (arguments.Length == 2)
            {
                return new CallCommand(arguments[1]);
            }
            return null;
        }

        public override void Execute()
        {
            Content.Script.CommandGroups.TryGetValue(_groupName, out CommandGroup group);

            // Local scene groups override the global group
            if (Interpreter.Scene != null)
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
