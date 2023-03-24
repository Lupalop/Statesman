using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Statesman.Commands
{
    public class QuitCommand : Command
    {
        public static readonly string ID = "quit";

        public QuitCommand()
        {
        }

        public override Command createInstance(string[] arguments)
        {
            return new QuitCommand();
        }

        public override void execute()
        {
            Environment.Exit(0);
        }
    }
}
