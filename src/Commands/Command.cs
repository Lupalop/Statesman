namespace Statesman.Commands
{
    public abstract class Command
    {
        public virtual Command CreateInstance(string[] arguments) => null;

        public virtual void Execute() { }
    }
}
