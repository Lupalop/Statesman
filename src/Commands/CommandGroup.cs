using System;
using System.Collections.Generic;

namespace Statesman.Commands
{
    public class CommandGroup
    {
        private string _name;
        private List<Command> _commands;

        public CommandGroup(string name, List<Command> commands)
        {
            _name = name;
            _commands = commands;
        }

        public CommandGroup(string name)
            : this(name, new List<Command>())
        {
        }

        public string getName()
        {
            return _name;
        }

        public void setName(string name)
        {
            _name = name;
        }

        public List<Command> getCommands()
        {
            return _commands;
        }

        public void execute()
        {
            int i = 0;
            while (i < _commands.Count)
            {
                Command command = _commands[i];
                // Handle jumps
                if (command is JumpCommand ||
                    command is ReturnCommand) {
                    i = (command as JumpCommand).getJumpIndex();
                    // Stop execution if the index is invalid
                    if (i < 0 || i > _commands.Count)
                    {
                        break;
                    }
                    continue;
                }
                // Call overridden global command
                if (command is GotoBaseCommand) {
                    if (Content.getScript().getCommandGroups().TryGetValue(_name, out CommandGroup group))
                    {
                        group.execute();
                    }
                }
                command.execute();
                i++;
            }
        }
    }
}
