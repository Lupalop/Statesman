namespace Statesman.Commands
{
    public abstract class Command
    {
        public static Dictionary<string, Type> All { get; private set; }

        static Command()
        {
            All = new Dictionary<string, Type>
            {
                // XXX: manually input IDs of new commands here!
                { PrintCommand.CommandPrintSingle, typeof(PrintCommand) },
                { PrintCommand.CommandPrintConcatenate, typeof(PrintCommand) },
                { PrintCommand.CommandPrintRandom, typeof(PrintCommand) },
                { JumpCommand.CommandJump, typeof(JumpCommand) },
                { JumpCommand.CommandSwitchJump, typeof(JumpCommand) },
                { JumpCommand.CommandInventoryJump, typeof(JumpCommand) },
                { JumpCommand.CommandReturn, typeof(JumpCommand) },
                { SwitchSetCommand.CommandSwitchSet, typeof(SwitchSetCommand) },
                { ConditionalCommand.CommandConditional, typeof(ConditionalCommand) },
                { InventoryCommand.CommandInventory, typeof(InventoryCommand) },
                { SceneCommand.CommandScene, typeof(SceneCommand) },
                { CallCommand.CommandCall, typeof(CallCommand) },
                { CallCommand.CommandGoto, typeof(CallCommand) },
                { MenuCommand.CommandQuit, typeof(MenuCommand) },
                { MenuCommand.CommandSave, typeof(MenuCommand) },
                { MenuCommand.CommandLoad, typeof(MenuCommand) },
                { PointsCommand.CommandPoints, typeof(PointsCommand) },
            };
        }

        public static Command Find(string[] arguments)
        {
            string commandId = arguments[0].ToLowerInvariant();
            Command command;
            if (All.TryGetValue(commandId, out Type commandType))
            {
                command = (Command)commandType.GetMethod(nameof(FromText))
                    .Invoke(null, new object[] { commandId, arguments });
                return command;
            }
            return null;
        }

        public static Command FromText(string commandName, string[] arguments)
        {
            if (string.IsNullOrEmpty(commandName))
            {
                throw new ArgumentException($"'{nameof(commandName)}' cannot be null or empty.", nameof(commandName));
            }

            if (arguments is null)
            {
                throw new ArgumentNullException(nameof(arguments));
            }

            return null;
        }

        public virtual void Execute() { }
    }
}
