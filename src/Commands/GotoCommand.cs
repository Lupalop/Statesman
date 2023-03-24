using System;

namespace Statesman.Commands
{
    public class GotoCommand : Command
    {
        public static readonly string ID = "goto";

        private readonly string _groupName;

        public GotoCommand()
        {
            _groupName = "";
        }

        public GotoCommand(string commandGroupName)
                : this()
        {
            if (string.IsNullOrWhiteSpace(commandGroupName))
            {
                throw new ArgumentException("Goto command group name cannot be empty");
            }
            _groupName = commandGroupName;
        }

        public override Command CreateInstance(string[] arguments)
        {
            if (arguments.Length == 2)
            {
                return new GotoCommand(arguments[1]);
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
