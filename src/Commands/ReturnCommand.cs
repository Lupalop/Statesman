using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Statesman.Commands
{
    public class ReturnCommand : JumpCommand
    {
        public static readonly string ID = "ret";

        public ReturnCommand()
            : base()
        {
        }

        public override Command createInstance(string[] arguments)
        {
            return new ReturnCommand();
        }

        public override int getJumpIndex()
        {
            return int.MaxValue;
        }
    }
}
