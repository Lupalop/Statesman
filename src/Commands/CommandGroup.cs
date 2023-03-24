using System.Collections.Generic;

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
                if (command is JumpCommand ||
                    command is ReturnCommand)
                {
                    i = (command as JumpCommand).GetJumpIndex();
                    // Stop execution if the index is invalid
                    if (i < 0 || i > Commands.Count)
                    {
                        break;
                    }
                    continue;
                }
                // Call overridden global command
                if (command is GotoBaseCommand)
                {
                    if (Content.Script.CommandGroups.TryGetValue(Name, out CommandGroup group))
                    {
                        group.Execute();
                    }
                }
                command.Execute();
                i++;
            }
        }
    }
}
