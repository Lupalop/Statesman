namespace Statesman.Commands
{
    public class CommandGroup
    {
        public string Name { get; set; }
        public List<Command> Commands { get; }

        public CommandGroup(string name, List<Command> commands)
        {
            Name = name;
            Commands = commands;
        }

        public CommandGroup(string name)
            : this(name, new List<Command>())
        {
        }

        public void Execute()
        {
            int i = 0;
            while (i < Commands.Count)
            {
                Command command = Commands[i];
                // Handle jumps
                if (command is JumpCommand)
                {
                    i = (command as JumpCommand).GetJumpIndex();
                    // Stop execution if the index is invalid
                    if (i < 0 || i > Commands.Count)
                    {
                        break;
                    }
                    continue;
                }
                // Call base function.
                if (command is CallCommand callCommand)
                {
                    CallCommand.CallType callType = callCommand.CallerType;
                    if (callType == CallCommand.CallType.Super &&
                        Content.Script.Functions.TryGetValue(
                            Name, out Function baseFunction))
                    {
                        baseFunction.Execute();
                    }
                }
                command.Execute();
                i++;
            }
        }
    }
}
