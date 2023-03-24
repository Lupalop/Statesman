using System;

namespace Statesman.Commands
{
    public abstract class Command
    {
        public virtual Command createInstance(string[] arguments)
        {
            return null;
        }

        public virtual void execute()
        {
        }
    }
}
