namespace Statesman.Commands
{
    public class QuitCommand : Command
    {
        public static readonly string ID = "quit";

        public QuitCommand()
        {
        }

        public override Command CreateInstance(string[] arguments)
        {
            return new QuitCommand();
        }

        public override void Execute()
        {
            Environment.Exit(0);
        }
    }
}
