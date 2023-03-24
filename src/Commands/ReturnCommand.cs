namespace Statesman.Commands
{
    public class ReturnCommand : JumpCommand
    {
        public static readonly string ID = "ret";

        public ReturnCommand()
            : base()
        {
        }

        public override Command CreateInstance(string[] arguments)
        {
            return new ReturnCommand();
        }

        public override int GetJumpIndex()
        {
            return int.MaxValue;
        }
    }
}
