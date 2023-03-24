namespace Statesman.Commands
{
    public class GotoBaseCommand : Command
    {
        public static readonly string ID = "gotob";

        public GotoBaseCommand()
        {
        }

        public override Command CreateInstance(string[] arguments)
        {
            return new GotoBaseCommand();
        }
    }
}
