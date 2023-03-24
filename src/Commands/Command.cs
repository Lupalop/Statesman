namespace Statesman.Commands
{
    public abstract class Command
    {
        public static Dictionary<string, Command> All { get; private set; }

        static Command()
        {
            All = new Dictionary<string, Command>
            {
                // XXX: manually input IDs of new commands here!
                { PrintCommand.ID, new PrintCommand() },
                { PrintRandomCommand.ID, new PrintRandomCommand() },
                { PrintCombineCommand.ID, new PrintCombineCommand() },
                { SceneCommand.ID, new SceneCommand() },
                { GotoCommand.ID, new GotoCommand() },
                { JumpCommand.ID, new JumpCommand() },
                { ReturnCommand.ID, new ReturnCommand() },
                { SwitchSetCommand.ID, new SwitchSetCommand() },
                { SwitchJumpCommand.ID, new SwitchJumpCommand() },
                { SwitchConditionalCommand.ID, new SwitchConditionalCommand() },
                { InventoryCommand.ID, new InventoryCommand() },
                { InventoryJumpCommand.ID, new InventoryJumpCommand() },
                { InventoryConditionalCommand.ID, new InventoryConditionalCommand() },
                { PointsCommand.ID, new PointsCommand() },
                { QuitCommand.ID, new QuitCommand() },
                { SaveCommand.ID, new SaveCommand() },
                { LoadCommand.ID, new LoadCommand() }
            };
        }

        public static Command Find(string[] arguments)
        {
            string commandId = arguments[0].ToLowerInvariant();
            if (All.TryGetValue(commandId, out Command command))
            {
                return command.CreateInstance(arguments);
            }
            return null;
        }

        public virtual Command CreateInstance(string[] arguments) => null;

        public virtual void Execute() { }
    }
}
