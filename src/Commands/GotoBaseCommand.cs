using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Statesman.Commands
{
    public class GotoBaseCommand : Command
    {
        public static readonly string ID = "gotob";

        public GotoBaseCommand()
        {
        }

        public override Command createInstance(string[] arguments)
        {
            return new GotoBaseCommand();
        }

        public override void execute()
        {
        }
    }
}
