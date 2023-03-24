using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Statesman.Commands
{
    public class GotoCommand : Command
    {
        public static readonly string ID = "goto";

        private string _groupName;

        public GotoCommand()
        {
            _groupName = "";
        }

        public GotoCommand(string commandGroupName)
                : this()
        {
            if (string.IsNullOrWhiteSpace(commandGroupName))
            {
                throw new ArgumentException();
            }
            _groupName = commandGroupName;
        }

        public override Command createInstance(string[] arguments)
        {
            if (arguments.Length == 2)
            {
                return new GotoCommand(arguments[1]);
            }
            return null;
        }

        public override void execute()
        {
            CommandGroup group = null;
            if (Content.getScript().getCommandGroups().ContainsKey(_groupName))
            {
                group = Content.getScript().getCommandGroups()[_groupName];
            }

            // Local scene groups override the global group
            if (Interpreter.getScene() != null)
            {
                CommandGroup localGroup = null;
                if (Interpreter.getScene().getCommandGroups().ContainsKey(_groupName))
                {
                    localGroup = Interpreter.getScene().getCommandGroups()[_groupName];
                }
                if (localGroup != null)
                {
                    group = localGroup;
                }
            }

            if (group != null)
            {
                group.execute();
            }
        }
    }
}
